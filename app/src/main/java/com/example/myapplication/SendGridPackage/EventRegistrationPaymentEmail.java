package com.example.myapplication.SendGridPackage;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

public class EventRegistrationPaymentEmail {
    private static final String TAG = "EventEmail";
    private static final String SENDGRID_API_KEY = BuildConfig.SENDGRID_API_KEY;

    public static void generatePDF(Context context,String uid,String studentId, String toEmail, String userName, String eventName,
                                   String activityName, String paymentID, String paymentAmount, String paymentStatus) {
        try {
            Log.d(TAG, "Generating PDF...");
            Uri pdfUri;
            String registrationDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "EventRegistrationReceipt.pdf");
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

                try {
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_foreground);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bitmapData = stream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(bitmapData);
                    Image logo = new Image(imageData);

                    logo.setWidth(100);
                    logo.setHeight(100);
                    logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

                    document.add(logo);
                } catch (Exception e) {
                    Log.e(TAG, "Error adding logo: ", e);
                }


                Paragraph header = new Paragraph("CAMPUS CONNECT")
                        .setFontSize(20)
                        .setBold()
                        .setFontColor(new DeviceRgb(255, 255, 255))
                        .setBackgroundColor(new DeviceRgb(230, 81, 0))
                        .setBorder(new SolidBorder(new DeviceRgb(200, 55, 0), 1))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(6)
                        .setMarginBottom(10);

                document.add(header);


                document.add(new Paragraph("\n"));

                float[] columnWidths = {150f, 250f};
                Table userInfoTable = new Table(columnWidths);
                userInfoTable.setMarginBottom(20);

                userInfoTable.addCell(new Cell().add(new Paragraph("UID:").setBold()));
                userInfoTable.addCell(new Cell().add(new Paragraph(uid)));

                userInfoTable.addCell(new Cell().add(new Paragraph("Student Id:").setBold()));
                userInfoTable.addCell(new Cell().add(new Paragraph(studentId)));

                userInfoTable.addCell(new Cell().add(new Paragraph("Name:").setBold()));
                userInfoTable.addCell(new Cell().add(new Paragraph(userName)));

                userInfoTable.addCell(new Cell().add(new Paragraph("Email:").setBold()));
                userInfoTable.addCell(new Cell().add(new Paragraph(toEmail)));

                userInfoTable.addCell(new Cell().add(new Paragraph("Registration Date:").setBold()));
                userInfoTable.addCell(new Cell().add(new Paragraph(registrationDate)));

                document.add(userInfoTable);

                document.add(new Paragraph("Event Details")
                        .setFontSize(18)
                        .setBold()
                        .setFontColor(new DeviceRgb(0, 0, 0))
                        .setMarginBottom(10));

                Table eventTable = new Table(columnWidths);
                eventTable.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1));
                eventTable.setMarginBottom(20);

                eventTable.addCell(new Cell().add(new Paragraph("Event Name:").setBold()));
                eventTable.addCell(new Cell().add(new Paragraph(eventName)));

                eventTable.addCell(new Cell().add(new Paragraph("Activity:").setBold()));
                eventTable.addCell(new Cell().add(new Paragraph(activityName)));

                document.add(eventTable);

                document.add(new Paragraph("Payment Details")
                        .setFontSize(18)
                        .setBold()
                        .setFontColor(new DeviceRgb(0, 0, 0))
                        .setMarginBottom(10));

                Table paymentTable = new Table(columnWidths);
                paymentTable.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1));
                paymentTable.setMarginBottom(20);

                paymentTable.addCell(new Cell().add(new Paragraph("Payment ID:").setBold()));
                paymentTable.addCell(new Cell().add(new Paragraph(paymentID)));

                paymentTable.addCell(new Cell().add(new Paragraph("Amount Paid:").setBold()));
                paymentTable.addCell(new Cell().add(new Paragraph("Rs. " + paymentAmount)
                        .setFontColor(new DeviceRgb(0, 128, 0))));

                paymentTable.addCell(new Cell().add(new Paragraph("Payment Status:").setBold()));
                paymentTable.addCell(new Cell().add(new Paragraph(paymentStatus)));

                document.add(paymentTable);

                document.add(new Paragraph("Thank you for registering! We look forward to your participation.")
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(new DeviceRgb(0, 0, 0))
                        .setMarginTop(20));

                document.add(new Paragraph("\n"));

                document.close();
                outputStream.close();
                Toast.makeText(context, "PDF saved to Downloads!", Toast.LENGTH_LONG).show();

                sendEventRegistrationEmailWithAttachment(context, toEmail, userName, eventName, activityName, paymentID, paymentAmount, paymentStatus, encodeFileToBase64(pdfUri, context));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating PDF: ", e);
            Toast.makeText(context, "Error generating receipt PDF!", Toast.LENGTH_SHORT).show();
        }
    }

    public static String encodeFileToBase64(Uri fileUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) return "";

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();

            // Use Base64.NO_WRAP to ensure no line breaks are added
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
        } catch (IOException e) {
            Log.e(TAG, "Error encoding file to Base64: " + e.getMessage());
            return "";
        }
    }

    public static void sendEventRegistrationEmailWithAttachment(Context context, String toEmail, String userName,
                                                                String eventName, String activityName,
                                                                String paymentID, String paymentAmount, String paymentStatus,
                                                                String base64PdfContent) {
        try {
            Log.d("SendGrid", "SendGrid API Key: " + SENDGRID_API_KEY);

            OkHttpClient client = new OkHttpClient();

            String timestamp = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(new Date());

            String subject = "Your Event Registration Has Been Successful!";
            String message = "<html><body>" +
                    "<h2>Greetings From Campus Connect!</h2>" +
                    "<p>Dear " + userName + ",</p>" +
                    "<p>Congratulations! You have successfully registered for the event <strong>" + eventName + "</strong> </p>" +
                    "<p><strong>Registration Details:</strong></p>" +
                    "<ul>" +
                    "<li><strong>Event Name:</strong> " + eventName + "</li>" +
                    "<li><strong>Activity Name:</strong> " + activityName + "</li>" +
                    "<li><strong>Registration Date:</strong> " + timestamp + "</li>" +
                    "</ul>" +
                    "<p><strong>Payment Details:</strong></p>" +
                    "<ul>" +
                    "<li><strong>Payment ID:</strong> " + paymentID + "</li>" +
                    "<li><strong>Amount Paid:</strong> $" + paymentAmount + "</li>" +
                    "<li><strong>Payment Status:</strong> " + paymentStatus + "</li>" +
                    "</ul>" +
                    "<p><strong>Receipt:</strong> Please find the attached payment receipt for your reference.</p>" +
                    "<p>We look forward to seeing you at the event!</p>" +
                    "<p>Best regards,<br>The Campus Connect Team</p>" +
                    "</body></html>";

            JsonObject jsonBody = new JsonObject();

            JsonArray personalizations = new JsonArray();
            JsonObject personalization = new JsonObject();
            JsonArray toEmails = new JsonArray();
            JsonObject toEmailObj = new JsonObject();
            Log.d("SendGrid", "To Email: " + toEmail);
            toEmailObj.addProperty("email", toEmail);
            toEmails.add(toEmailObj);
            personalization.add("to", toEmails);
            personalization.addProperty("subject", subject);
            personalizations.add(personalization);

            jsonBody.add("personalizations", personalizations);

            JsonObject fromObj = new JsonObject();
            fromObj.addProperty("email", "hub.campusconnect@gmail.com");
            fromObj.addProperty("name", "ĊäṁṡṳṡĊöṅṅëċẗ");
            jsonBody.add("from", fromObj);

            JsonArray content = new JsonArray();
            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("type", "text/html");
            contentObj.addProperty("value", message);
            content.add(contentObj);
            jsonBody.add("content", content);

            JsonArray attachments = new JsonArray();
            JsonObject attachmentObj = new JsonObject();
            attachmentObj.addProperty("content", base64PdfContent); // Use the base64 string directly
            attachmentObj.addProperty("type", "application/pdf");
            attachmentObj.addProperty("filename", "EventRegistrationReceipt.pdf");
            attachments.add(attachmentObj);
            jsonBody.add("attachments", attachments);

            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonBody.toString());

            Log.d("SendGrid", "Sending registration success email to: " + toEmail);
            Request request = new Request.Builder()
                    .url("https://api.sendgrid.com/v3/mail/send")
                    .addHeader("Authorization", "Bearer " + SENDGRID_API_KEY)
                    .post(body)
                    .build();

            Log.d("SendGrid", "Request URL: " + request.url());
            Log.d("SendGrid", "Request JSON Body: " + jsonBody.toString());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("SendGrid", "Error sending email: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("SendGrid", "Registration success email sent.");
                    } else {
                        Log.e("SendGrid", "Failed to send email. Response Code: " + response.code() + ", Body: " + response.body().string());
                    }
                }
            });

        } catch (Exception ex) {
            Log.e("SendGrid", "Error sending email with attachment: " + ex.getMessage());
        }
    }
}