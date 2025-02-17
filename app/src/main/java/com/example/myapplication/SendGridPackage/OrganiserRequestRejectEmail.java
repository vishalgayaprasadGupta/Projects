package com.example.myapplication.SendGridPackage;

import static android.content.ContentValues.TAG;

import android.util.Log;

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

public class OrganiserRequestRejectEmail {

    private static final String SENDGRID_API_KEY = BuildConfig.SENDGRID_API_KEY;

    public static void sendOrganiserAccountApprovalEmail(String toEmail, String organiserName, String adminUID, String adminName) {
        try {
            Log.d(TAG, "SendGrid API Key: " + SENDGRID_API_KEY);

            OkHttpClient client = new OkHttpClient();

            String timestamp = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(new Date());


            String subject = "Your Request for Event Organizer Role Has Been Rejected";
            String adminEmail = "hub.campusconnect@gmail.com";
            String message = "<html><body>" +
                    "<h1>Campus Connect - Request Update</h1>" +
                    "<p>Dear " + organiserName + ",</p>" +
                    "<p>We regret to inform you that your request to become an Event Organizer has been reviewed and <strong>rejected</strong> by our admin team.</p>" +
                    "<p><strong>Reviewed By:</strong></p>" +
                    "<ul>" +
                    "<li><strong>Name:</strong> " + adminName + "</li>" +
                    "<li><strong>UID:</strong> " + adminUID + "</li>" +
                    "</ul>" +
                    "<p><strong>Request Processed On: </strong> " + timestamp + "</p>" +
                    "<p>Your account access has been set to <strong>User</strong>. You can continue to log in and explore other features available on Campus Connect.</p>" +
                    "<p>If you wish to delete your account, you can do so by logging in and navigating to the account settings page.</p>" +
                    "<p>We appreciate your interest and encourage you to stay connected with Campus Connect for future opportunities.</p>" +
                    "<p>If you have any questions or would like to receive feedback on your application, feel free to contact the admin at: " +
                    "<a href='mailto:" + adminEmail + "'>" + adminEmail + "</a></p>" +
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

            Log.d("SendGrid", "Sending registration success email to: " + toEmail);
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
                        Log.d("SendGrid", "Registration success email sent.");
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
