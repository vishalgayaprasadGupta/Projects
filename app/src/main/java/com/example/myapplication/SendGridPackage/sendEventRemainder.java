package com.example.myapplication.SendGridPackage;

import static com.example.myapplication.BuildConfig.SENDGRID_API_KEY;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sendEventRemainder {

    public static String generateQRCodeBase64(String uid,String participantName, String eventName,String activityId, String activityName, String activityDate, String activityTime) {
        try {
            String qrContent = "Participant Name: " + participantName +"\n  |  "
                    + "Event: " + eventName + "\n  |  "
                    + "Activity: " + activityName + "\n  |  "
                    + "Date: " + activityDate + "\n  |  "
                    + "Time: " + activityTime;

            BitMatrix bitMatrix = new QRCodeWriter().encode(qrContent, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void sendEvetReminderQR(Context context, String toEmail, String uid, String participantName,
                                                        String eventName,String activityId, String activityName, String activityDate, String activityTime) {
        try {
            OkHttpClient client = new OkHttpClient();

            String base64QrCode = generateQRCodeBase64(uid,participantName, eventName,activityId, activityName, activityDate, activityTime);
            if (base64QrCode == null) {
                Log.e("SendGrid", "Failed to generate QR code.");
                return;
            }

            String subject = "Get Ready for " + eventName + "! Your Event Pass is Ready!";
            String adminEmail = "hub.campusconnect@gmail.com";
            String message = "<html><body>" +
                    "<p>Dear " + participantName + ",</p>" +
                    "<p>We are excited to begin with the event <strong>" + eventName + "</strong>!</p>" +
                    "<p><strong>Event Details:</strong></p>" +
                    "<ul>" +
                    "<li><strong>Event Name:</strong> " + eventName + "</li>" +
                    "<li><strong>Activity Name:</strong> " + activityName + "</li>" +
                    "<li><strong>Activity Date:</strong> " + activityDate + "</li>" +
                    "<li><strong>Activity Time:</strong> " + activityTime + "</li>" +
                    "</ul>" +
                    "<p>We kindly request you to arrive at least <strong>30 minutes before</strong> the event starts for smooth check-in and verification.</p>" +
                    "<p>Your unique QR code is attached to this email. Please present it upon arrival for verification.</p>" +
                    "<p>If you have any questions or need further assistance, feel free to reach out to us at: " +
                    "<a href='mailto:" + adminEmail + "'>" + adminEmail + "</a></p>" +
                    "<p>Looking forward to seeing you at the event!</p>" +
                    "<br>" +
                    "<p>Best regards,<br><strong>The Campus Connect Team</strong></p>" +
                    "<hr>" +
                    "<p>&#169; 2025 Campus Connect. All rights reserved.</p>" +
                    "</body></html>";

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
            fromObj.addProperty("email", adminEmail);
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
            attachmentObj.addProperty("content", base64QrCode);
            attachmentObj.addProperty("type", "image/png");
            attachmentObj.addProperty("filename", "EventPass.png");
            attachmentObj.addProperty("content_id", "qr_code_attachment");
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
                    Log.e("SendGrid", "Error sending email: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("SendGrid", "Email sent successfully to."+toEmail);
                        Log.d("SendGrid", "QR Code email sent successfully.");
                    } else {
                        Log.e("SendGrid", "Failed to send email. Response Code: " + response.code());
                    }
                }
            });

        } catch (Exception ex) {
            Log.e("SendGrid", "Error sending email with QR code: " + ex.getMessage());
        }
    }



}
