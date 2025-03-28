package com.example.myapplication.SendGridPackage;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sendEventReportEmail {
    private static final String TAG = "EventEmail";
    private static final String SENDGRID_API_KEY = BuildConfig.SENDGRID_API_KEY;

    public static void generatePDF(Context context, String toEmail, String eventName, String startDate,
                                   String endDate,String eventStream,String eventDepartment,int registrationCount,
                                   int activityCount,int totalRevenue,String name,int attendeeCount,int dropoutsCount) {
        try {
            String timestamp = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(new Date());
            Log.d(TAG, "Generating Certificate PDF...");
            Uri pdfUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "EventReport.pdf");
                values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                pdfUri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (pdfUri == null) {
                    throw new IOException("Failed to create file");
                }

                OutputStream outputStream = context.getContentResolver().openOutputStream(pdfUri);
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument, PageSize.A4);

                InputStream templateStream = context.getResources().openRawResource(R.raw.report_template);
                Bitmap certificateBitmap = BitmapFactory.decodeStream(templateStream);
                templateStream.close();

                Bitmap mutableBitmap = certificateBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);
                Paint paint = new Paint();
                paint.setColor(android.graphics.Color.BLACK);
                paint.setTextSize(36);
                paint.setAntiAlias(true);
                Typeface typeface = ResourcesCompat.getFont(context, R.font.timesnewroman);
                paint.setTypeface(typeface);
                canvas.drawText(eventName, 850, 355, paint);
                canvas.drawText(startDate, 850, 418, paint);
                canvas.drawText(endDate, 850, 473, paint);
                canvas.drawText(name, 850, 586, paint);
                canvas.drawText(timestamp, 830, 530, paint);
                canvas.drawText(eventStream, 850, 837, paint);
                canvas.drawText(eventDepartment, 850, 899, paint);
                canvas.drawText(String.valueOf(registrationCount), 277, 1093, paint);
                canvas.drawText(String.valueOf(attendeeCount), 545, 1093, paint);
                canvas.drawText(String.valueOf(dropoutsCount), 840, 1093, paint);
                canvas.drawText(eventName, 255, 1340, paint);
                canvas.drawText(String.valueOf(activityCount), 680, 1345, paint);
                canvas.drawText(String.valueOf(totalRevenue),710,1600,paint);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapData = stream.toByteArray();

                ImageData imageData = ImageDataFactory.create(bitmapData);
                Image certificateImage = new Image(imageData);

                certificateImage.setFixedPosition(0, 0);
                certificateImage.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());

                document.add(certificateImage);
                document.close();
                outputStream.close();
                Toast.makeText(context, "Event Report saved to Downloads!", Toast.LENGTH_LONG).show();

                sendReport(context, toEmail, name, encodeFileToBase64(pdfUri, context));

            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating report PDF: ", e);
            Toast.makeText(context, "Error generating report!", Toast.LENGTH_SHORT).show();
        }
    }

    private static String encodeFileToBase64(Uri fileUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                Log.e(TAG, "Failed to open file stream.");
                return null;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            byte[] fileBytes = outputStream.toByteArray();
            return Base64.encodeToString(fileBytes, Base64.NO_WRAP);
        } catch (IOException e) {
            Log.e(TAG, "Error encoding file to Base64: ", e);
            return null;
        }
    }

    public static void sendReport(Context context, String toEmail, String userName, String base64PdfContent) {
        try {
            OkHttpClient client = new OkHttpClient();
            String subject = "Requested Document Received";
            String message = "<html><body>" +
                    "<h2>Dear " + userName + ",</h2>" +
                    "<p>We are pleased to inform you that the document you requested has been successfully generated.</p>" +
                    "<p>The document is attached to this email. Please review it at your convenience.</p>" +
                    "<p>If you have any questions, feel free to contact the admin team.</p>" +
                    "<br>" +
                    "<p>Best regards,</p>" +
                    "<p><b>Campus Connect Team</b></p>" +
                    "<p><i>&copy; 2025 Campus Connect. All rights reserved.</i></p>" +
                    "</body></html>";

            JsonObject jsonBody = new JsonObject();
            JsonArray personalizations = new JsonArray();
            JsonObject personalization = new JsonObject();
            JsonArray toEmails = new JsonArray();
            JsonObject toEmailObj = new JsonObject();
            Log.d(TAG, "Sending Certificate to: " + toEmail);
            toEmailObj.addProperty("email", toEmail);
            toEmails.add(toEmailObj);
            personalization.add("to", toEmails);
            personalization.addProperty("subject", subject);
            personalizations.add(personalization);
            jsonBody.add("personalizations", personalizations);

            JsonObject fromObj = new JsonObject();
            fromObj.addProperty("email", "hub.campusconnect@gmail.com");
            fromObj.addProperty("name", "Campus Connect");
            jsonBody.add("from", fromObj);

            JsonArray content = new JsonArray();
            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("type", "text/html");
            contentObj.addProperty("value", message);
            content.add(contentObj);
            jsonBody.add("content", content);

            JsonArray attachments = new JsonArray();
            JsonObject attachmentObj = new JsonObject();
            attachmentObj.addProperty("content", base64PdfContent);
            attachmentObj.addProperty("type", "application/pdf");
            attachmentObj.addProperty("filename", "EventReport.pdf");
            attachments.add(attachmentObj);
            jsonBody.add("attachments", attachments);

            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonBody.toString());
            Request request = new Request.Builder()
                    .url("https://api.sendgrid.com/v3/mail/send")
                    .addHeader("Authorization", "Bearer " + SENDGRID_API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Error sending email: " + e.getMessage());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Event Report email sent.");
                    } else {
                        Log.e(TAG, "Failed to send email. Response: " + response.body().string());
                    }
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, "Error sending email: " + ex.getMessage());
        }
    }
}
