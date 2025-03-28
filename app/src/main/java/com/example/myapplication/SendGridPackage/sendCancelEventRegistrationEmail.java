package com.example.myapplication.SendGridPackage;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.StringDef;

import com.example.myapplication.BuildConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
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

public class sendCancelEventRegistrationEmail {

    private static final String SENDGRID_API_KEY = BuildConfig.SENDGRID_API_KEY;

    public static void sendEventCancellationEmail(String toEmail,String Name, String eventName, String activityName, String eventDate,String eventTime) {
        try {
            Log.d(TAG, "SendGrid API Key: " + SENDGRID_API_KEY);

            OkHttpClient client = new OkHttpClient();

            String timestamp = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(new Date());

            String subject = "Event Registration Cancelled";
            String adminEmail = "hub.campusconnect@gmail.com";
            String message = "<html><body>" +
                    "<p>Dear " + Name + ",</p>" +
                    "<p>We have successfully processed your cancellation request for the following event:</p>" +
                    "<ul>" +
                    "<li><strong>Event Name:</strong> " + eventName + "</li>" +
                    "<li><strong>Activity:</strong> " + activityName + "</li>" +
                    "<li><strong>Date:</strong> " + eventDate + "</li>" +
                    "<li><strong>Time:</strong> " + eventTime + "</li>" +
                    "</ul>" +
                    "<p><strong>Refund :</strong></p>" +
                    "<p>If any amount was deducted during the registration process, it will be refunded shortly as per terms and conditions . Refunds typically take 5-7 business days to reflect in your account.</p>" +
                    "<p>If you have any questions or concerns regarding the cancellation or refund, feel free to contact us.</p>" +
                    "<p>For assistance, reach out to our support team at: " +
                    "<a href='mailto:" + adminEmail + "'>" + adminEmail + "</a></p>" +
                    "<p>We hope to see you in future events!</p>" +
                    "<p>Best regards,<br>The Campus Connect Team</p>" +
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
            fromObj.addProperty("email", "hub.campusconnect@gmail.com");
            fromObj.addProperty("name", "ĊäṁṗüṡĊöṅṅëċẗ");
            jsonBody.add("from", fromObj);

            JsonArray content = new JsonArray();
            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("type", "text/html");
            contentObj.addProperty("value", message);
            content.add(contentObj);
            jsonBody.add("content", content);

            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonBody.toString());

            Log.d("SendGrid", "Sending registration cancel email to: " + toEmail);
            Request request = new Request.Builder()
                    .url("https://api.sendgrid.com/v3/mail/send")
                    .addHeader("Authorization", "Bearer " + SENDGRID_API_KEY)
                    .post(body)
                    .build();

            // Logging the request URL and the body being sent
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
                        Log.d("SendGrid", "Registration cancel email sent.");
                    } else {
                        Log.e("SendGrid", "Failed to send email. Response Code: " + response.code() + ", Body: " + response.body().string());
                    }
                }
            });

        } catch (Exception ex) {
            Log.e("SendGrid", "Error sending email: " + ex.getMessage());
        }
    }
}
