package com.example.myapplication.SendGridPackage;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import static android.content.ContentValues.TAG;


import static com.example.myapplication.BuildConfig.SENDGRID_API_KEY;

import com.example.myapplication.adminfragements.AdminHome;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PdfExporter {

    private static Context context;

    public PdfExporter(Context context) {
        this.context = context;
    }

    public void exportEventDetails(String eventName, List<Map<String, String>> eventDataList) {
        try {
            Log.d("PdfExporter", "Exporting Activity Details");
            Log.d("PdfExporter", "Event Name: " + eventName);
            Log.d("PdfExporter", "Event Data List Size: " + eventDataList.size());

            File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EventManagement");
            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            File file = new File(pdfDir, eventName + "_EventDetails.pdf");
            if (file.exists()) {
                file.delete();
            }
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.setMargins(10, 10, 10, 10);

            Paragraph title = new Paragraph(eventName + " - Event Details")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(title);
            document.add(new Paragraph("\n"));

            if (eventDataList.isEmpty()) {
                document.add(new Paragraph("No Data Available").setFontSize(14).setTextAlignment(TextAlignment.CENTER));
                document.close();
                return;
            }

            Set<String> headers = eventDataList.get(0).keySet();

            float[] columnWidths = {40, 60};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            int activtiyCount = 1;
            for (Map<String, String> participant : eventDataList) {
                Log.d("PdfExporter", "Processing events: " + activtiyCount);

                table.addCell(new Cell(1, 2).add(new Paragraph("Event Activity " + activtiyCount))
                        .setBackgroundColor(ColorConstants.ORANGE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setPadding(5));

                for (String key : headers) {
                    Log.d("PdfExporter", "Field: " + key + ", Value: " + participant.getOrDefault(key, "N/A"));

                    table.addCell(new Cell().add(new Paragraph(key)
                                    .setBold()
                                    .setFontSize(10))
                            .setBackgroundColor(ColorConstants.CYAN )
                            .setTextAlignment(TextAlignment.LEFT)
                            .setPadding(3));

                    table.addCell(new Cell().add(new Paragraph(participant.getOrDefault(key, "N/A"))
                                    .setFontSize(10))
                            .setTextAlignment(TextAlignment.LEFT)
                            .setPadding(3));
                }
                activtiyCount++;
            }

            document.add(table);
            document.close();

            Toast.makeText(context, "PDF Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }
    }


    public static void sendEmailWithPdf(String toEmail, String Name, File pdfFile) {
        Log.d(TAG, "sendEmailWithPdf: Sending email with PDF" + toEmail);
        String subject = "Your Requested Details - Campus Connect";
        String message = "<html><body>" +
                "<h1>Campus Connect - Requested Details</h1>" +
                "<p>Dear " + Name + ",</p>" +
                "<p>As per your request, please find the attached PDF containing the necessary details.</p>" +
                "<p>The attached PDF includes all relevant information related to the event.</p>" +
                "<p>If you have any queries, feel free to contact our support team :</p>" +
                "<p>hub.campusconnect@gmail.com</p>" +
                "<p>Best regards,<br>The Campus Connect Team</p>" +
                "<p>&#169; 2025 Campus Connect. All rights reserved.</p>" +
                "</body></html>";
        try {
            Log.d(TAG, "SendGrid API Key: " + SENDGRID_API_KEY);

            if (!pdfFile.exists()) {
                Log.e("SendGrid", "PDF file not found: " + pdfFile.getAbsolutePath());
                return;
            }

            String pdfBase64 = encodeFileToBase64(pdfFile);
            if (pdfBase64 == null) {
                Log.e("SendGrid", "Error encoding PDF file to Base64");
                return;
            }

            OkHttpClient client = new OkHttpClient();

            JsonObject jsonBody = new JsonObject();

            JsonArray personalizations = new JsonArray();
            JsonObject personalization = new JsonObject();
            JsonArray toEmails = new JsonArray();
            JsonObject toEmailObj = new JsonObject();
            toEmailObj.addProperty("email", toEmail);
            toEmails.add(toEmailObj);
            personalization.add("to", toEmails);
            personalization.addProperty("subject", subject);
            personalizations.add(personalization);

            jsonBody.add("personalizations", personalizations);

            JsonObject fromObj = new JsonObject();
            fromObj.addProperty("email", "hub.campusconnect@gmail.com"); // Replace with your verified SendGrid sender
            fromObj.addProperty("name", "ĊäṁṗüṡĊöṅṅëċẗ");
            jsonBody.add("from", fromObj);

            JsonArray content = new JsonArray();
            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("type", "text/html");
            contentObj.addProperty("value", message);
            content.add(contentObj);
            jsonBody.add("content", content);

            JsonArray attachmentsArray = new JsonArray();
            JsonObject attachment = new JsonObject();
            attachment.addProperty("content", pdfBase64);
            attachment.addProperty("filename", pdfFile.getName());
            attachment.addProperty("type", "application/pdf");
            attachmentsArray.add(attachment);
            jsonBody.add("attachments", attachmentsArray);

            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonBody.toString());

            Log.d("SendGrid", "Sending email with PDF to: " + toEmail);
            Request request = new Request.Builder()
                    .url("https://api.sendgrid.com/v3/mail/send")
                    .addHeader("Authorization", "Bearer " + SENDGRID_API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("SendGrid", "Error sending email: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("SendGrid", "Email with PDF sent successfully.");
                    } else {
                        Log.e("SendGrid", "Failed to send email. Response Code: " + response.code() + ", Body: " + response.body().string());
                    }
                }
            });
            Toast.makeText(context, "Email send to your Email Succesfully" , Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e("SendGrid", "Error sending email: " + ex.getMessage());
        }
    }

    private static String encodeFileToBase64(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            fileInputStream.close();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IOException e) {
            Log.e("SendGrid", "Error encoding file to Base64: " + e.getMessage());
            return null;
        }
    }

    public void exportParticipantDetails(String eventName, List<Map<String, String>> participantDataList) {
        try {
            Log.d("PdfExporter", "Exporting Activity Details");
            Log.d("PdfExporter", "Event Name: " + eventName);
            Log.d("PdfExporter", "Participants Data List Size: " + participantDataList.size());

            File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EventManagement");
            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            File file = new File(pdfDir, eventName + "_ParticipantsDetails.pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDoc = new PdfDocument(writer);

            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

            Document document = new Document(pdfDoc);
            document.setMargins(10, 10, 10, 10);

            Paragraph title = new Paragraph(eventName + " - Participants Details")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(title);
            document.add(new Paragraph("\n"));

            if (participantDataList.isEmpty()) {
                document.add(new Paragraph("No Data Available").setFontSize(14).setTextAlignment(TextAlignment.CENTER));
                document.close();
                return;
            }

            Set<String> headers = participantDataList.get(0).keySet();

            float[] columnWidths = new float[headers.size()];
            Arrays.fill(columnWidths, 100f / headers.size());

            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            for (String header : headers) {
                Cell headerCell = new Cell().add(new Paragraph(header)
                                .setBold().setFontSize(10))
                        .setBackgroundColor(ColorConstants.CYAN)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setPadding(2)
                        .setBorder(new SolidBorder(1));
                table.addHeaderCell(headerCell);
            }

            for (Map<String, String> participant : participantDataList) {
                for (String key : headers) {
                    String value = participant.getOrDefault(key, "N/A");

                    Cell dataCell = new Cell().add(new Paragraph(value)
                            .setFontSize(9)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1)
                            .setBorder(new SolidBorder(0.5f))
                            .setVerticalAlignment(VerticalAlignment.MIDDLE));
                    dataCell.setKeepTogether(true);
                    table.addCell(dataCell);
                }
            }
            document.add(table);
            document.close();

            Toast.makeText(context, "PDF Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void generateUserDetails(Context context,List<Map<String, Object>> userList) {
        try {
            File pdfFile;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, "UserDetails.pdf");
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);

                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
                pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

                Document document = new Document(pdfDocument);
                document.setMargins(10, 10, 10, 10);

                Paragraph title = new Paragraph("User Details")
                        .setBold()
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.BLUE);
                document.add(title);
                document.add(new Paragraph("\n"));

                float[] columnWidths = {100, 30, 50, 50, 100, 50, 50};
                Table table = new Table(UnitValue.createPercentArray(columnWidths));
                table.setWidth(UnitValue.createPercentValue(100));

                String[] headers = {"Name", "Gender", "Email", "Phone", "College", "Role", "Status"};
                for (String header : headers) {
                    Cell headerCell = new Cell().add(new Paragraph(header)
                                    .setBold().setFontSize(10))
                            .setBackgroundColor(ColorConstants.CYAN)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPadding(2)
                            .setBorder(new SolidBorder(1));
                    table.addHeaderCell(headerCell);
                }

                for (Map<String, Object> user : userList) {
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("name", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("gender", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("email", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("contact", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("college", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("role", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("status", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                }
                document.add(table);
                document.close();

                Toast.makeText(context.getApplicationContext(), "PDF saved to Downloads!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getApplicationContext(), "PDF Downlaoding format error", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("PDF", "Error generating PDF: ", e);
            Toast.makeText(context.getApplicationContext(), "Error downloading PDF!", Toast.LENGTH_SHORT).show();
        }
    }

    public void generateOrganiserDetails(Context context,List<Map<String, Object>> userList) {
        try {
            File pdfFile;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, "EventOrganiserDetails.pdf");
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);

                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
                pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

                Document document = new Document(pdfDocument);
                document.setMargins(10, 10, 10, 10);

                Paragraph title = new Paragraph("User Details")
                        .setBold()
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.BLUE);
                document.add(title);
                document.add(new Paragraph("\n"));

                float[] columnWidths = {100, 30, 50, 50, 100, 50, 50};
                Table table = new Table(UnitValue.createPercentArray(columnWidths));
                table.setWidth(UnitValue.createPercentValue(100));

                String[] headers = {"Name", "Gender", "Email", "Phone", "College", "Role", "Status"};
                for (String header : headers) {
                    Cell headerCell = new Cell().add(new Paragraph(header)
                                    .setBold().setFontSize(10))
                            .setBackgroundColor(ColorConstants.CYAN)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPadding(2)
                            .setBorder(new SolidBorder(1));
                    table.addHeaderCell(headerCell);
                }

                for (Map<String, Object> user : userList) {
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("name", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("gender", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("email", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("contact", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("college", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("role", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph((String) user.getOrDefault("status", "N/A")))
                            .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                }
                document.add(table);
                document.close();

                Toast.makeText(context.getApplicationContext(), "PDF saved to Downloads!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getApplicationContext(), "PDF Downlaoding format error", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("PDF", "Error generating PDF: ", e);
            Toast.makeText(context.getApplicationContext(), "Error downloading PDF!", Toast.LENGTH_SHORT).show();
        }
    }
}
