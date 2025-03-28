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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VolunteerCertificateGeneration {
    private static final String TAG = "EventEmail";
    private static final String SENDGRID_API_KEY = BuildConfig.SENDGRID_API_KEY;

    public static void generatePDF(Context context, String toEmail, String userName,String College,String Role) {
        try {
            Log.d(TAG, "Generating Certificate PDF...");
            Uri pdfUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "VolunteerCertificate.pdf");
                values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                pdfUri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (pdfUri == null) {
                    throw new IOException("Failed to create file");
                }

                OutputStream outputStream = context.getContentResolver().openOutputStream(pdfUri);
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument, PageSize.A4.rotate());

                InputStream templateStream = context.getResources().openRawResource(R.raw.certificate_template);
                Bitmap certificateBitmap = BitmapFactory.decodeStream(templateStream);
                templateStream.close();

                Bitmap mutableBitmap = certificateBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);
                Paint paint = new Paint();
                paint.setColor(android.graphics.Color.BLACK);
                paint.setTextSize(45);
                paint.setAntiAlias(true);
                Typeface typeface = ResourcesCompat.getFont(context, R.font.timesnewroman_bold);
                paint.setTypeface(typeface);
                canvas.drawText(userName, 730, 750, paint);
                canvas.drawText(Role, 760, 915, paint);
                canvas.drawText(College, 1170, 915, paint);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapData = stream.toByteArray();

                ImageData imageData = ImageDataFactory.create(bitmapData);
                Image certificateImage = new Image(imageData);

                certificateImage.setFixedPosition(0, 0);
                certificateImage.scaleAbsolute(PageSize.A4.rotate().getWidth(), PageSize.A4.rotate().getHeight());

                document.add(certificateImage);
                document.close();
                outputStream.close();
                Toast.makeText(context, "Certificate saved to Downloads!", Toast.LENGTH_LONG).show();

                sendCertificate(context, toEmail, userName, encodeFileToBase64(pdfUri, context));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating certificate PDF: ", e);
            Toast.makeText(context, "Error generating certificate!", Toast.LENGTH_SHORT).show();
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

    public static void sendCertificate(Context context, String toEmail, String userName, String base64PdfContent) {
        try {
            OkHttpClient client = new OkHttpClient();
            String subject = "Your Volunteer Certificate";
            String message = "<html><body><h2>Congratulations " + userName + "!</h2>" +
                    "<p>Your volunteer certificate is attached to this email.</p>" +
                    "<p>You can use this certificate to claim your CC points for your involvement in the event.</p>" +
                    "<p>Thank you for your valuable contribution!</p>" +
                    "<p>For any Query contact Organiser</p>" +
                    "<p>Best regards,Campus Connect</p>" +
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
            attachmentObj.addProperty("filename", "VolunteerCertificate.pdf");
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
                        Log.d(TAG, "Certificate email sent.");
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