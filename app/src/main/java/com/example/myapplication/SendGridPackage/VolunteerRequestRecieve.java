package com.example.myapplication.SendGridPackage;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.myapplication.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VolunteerRequestRecieve {

    private static final String SENDGRID_API_KEY = BuildConfig.SENDGRID_API_KEY;
    private static String stream,department;
    public static void sendVolunteerRequestToOrganiser(String Name,String Stream,String Department) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> organiserEmails = new ArrayList<>();
        stream=Stream;
        department=Department;
        db.collection("User")
                .whereEqualTo("role", "Event Organiser")
                .whereEqualTo("stream", stream)
                .whereEqualTo("department", department)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            if (email != null) {
                                organiserEmails.add(email);
                            }
                        }
                        sendEmailToVolunteer(organiserEmails, Name,Stream,Department);
                    } else {
                        Log.e(TAG, "Error fetching admin emails: ", task.getException());
                    }
                });
    }

    private static void sendEmailToVolunteer(List<String> organiserEmails, String Name,String Stream,String Department) {
        if (organiserEmails.isEmpty()) {
            Log.e(TAG, "No admin emails found. Email not sent.");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String timestamp = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(new Date());

        String subject = "Action Required: New Event Volunteer Registration Request";
        String message = "<html><body>" +
                "<p>Dear Event Organizer,</p>" +
                "<p>We have received a new <strong>Event Volunteer</strong> registration request that requires your review.</p>" +
                "<p><strong>Volunteer Name:</strong> " + Name + "</p>" +
                "<p><strong>Stream:</strong> " + Stream + "</p>" +
                "<p><strong>Department:</strong> " + Department + "</p>" +
                "<p><strong>Requested On:</strong> " + timestamp + "</p>" +
                "<p>Please log in to the CampusConnect  to review and take necessary action on this request.</p>" +
                "<p>If you have any questions or require assistance, feel free to contact support at <a href='mailto:hub.campusconnect@gmail.com'>hub.campusconnect@gmail.com</a>.</p>" +
                "<p>Best Regards,<br>The Campus Connect Team</p>" +
                "<p>&#169; 2025 Campus Connect. All rights reserved.</p>" +
                "</body></html>";

        JsonObject jsonBody = new JsonObject();
        JsonArray personalizations = new JsonArray();

        for (String adminEmail : organiserEmails) {
            JsonObject personalization = new JsonObject();
            JsonArray toEmails = new JsonArray();
            JsonObject toEmailObj = new JsonObject();
            toEmailObj.addProperty("email", adminEmail);
            toEmails.add(toEmailObj);
            personalization.add("to", toEmails);
            personalization.addProperty("subject", subject);
            personalizations.add(personalization);
        }

        jsonBody.add("personalizations", personalizations);

        JsonObject fromObj = new JsonObject();
        fromObj.addProperty("email", "hub.campusconnect@gmail.com");
        fromObj.addProperty("name", "CampusConnect");
        jsonBody.add("from", fromObj);

        JsonArray content = new JsonArray();
        JsonObject contentObj = new JsonObject();
        contentObj.addProperty("type", "text/html");
        contentObj.addProperty("value", message);
        content.add(contentObj);
        jsonBody.add("content", content);

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
                    Log.d("SendGrid", "Email sent to Admins.");
                } else {
                    Log.e("SendGrid", "Failed to send email. Response Code: " + response.code() + ", Body: " + response.body().string());
                }
            }
        });
    }
}
