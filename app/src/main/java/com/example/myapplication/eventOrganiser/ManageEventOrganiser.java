package com.example.myapplication.eventOrganiser;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adminfragements.AdminHome;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManageEventOrganiser extends Fragment {
    View view;
    CardView AddEventOrganiser,VerifyRequest,UpdateDetails,DeleteEventOrganiser,ExportOrganiser;
    TextView addOrganiser,exportOrganiser,pendingRequest,deleteOrganiser,updateOrganiserDetails;
    public ManageEventOrganiser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_manage_event_organiser, container, false);

        AddEventOrganiser=view.findViewById(R.id.AddEventOrganiser);
        VerifyRequest=view.findViewById(R.id.VerifyRequest);
        UpdateDetails=view.findViewById(R.id.UpdateDetails);
        DeleteEventOrganiser=view.findViewById(R.id.DeleteEventOrganiser);
        ExportOrganiser=view.findViewById(R.id.ExportOrganiser);

        exportOrganiser=view.findViewById(R.id.exportOrganiserDetails);
        deleteOrganiser=view.findViewById(R.id.deleteOrganiser);
        updateOrganiserDetails=view.findViewById(R.id.updateOrganiserDetails);


        animateCardView(AddEventOrganiser,500);
        animateCardView(VerifyRequest,1000);
        animateCardView(UpdateDetails,1500);
        animateCardView(DeleteEventOrganiser,2000);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                        getFragment(new AdminHome());
                    }
                });

        addOrganiser=view.findViewById(R.id.addOrganiser);
        addOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new addEventOrganiser());
            }
        });
        pendingRequest=view.findViewById(R.id.pendingRequest);
        pendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new PendingOrganisersRequest());
            }
        });

        exportOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadDialog();
            }
        });

        deleteOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        updateOrganiserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return view;
    }

    private void animateCardView(final CardView cardView, long delay) {
        cardView.setVisibility(View.INVISIBLE);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(delay);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Optional: You can add additional behavior after the animation ends
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Not needed in this case
            }
        });

        cardView.startAnimation(fadeIn);
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Download PDF")
                .setMessage("Do you want to download the Event Organiser Details as a PDF?")
                .setPositiveButton("Download", (dialog, which) -> {
                    fetchUserData();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }
    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User").whereEqualTo("role", "Event Organiser").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userList.add(document.getData());
                }
                generatePDF(userList);
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });

    }
    public void generatePDF(List<Map<String, Object>> userList) {
        try {
            File pdfFile;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, "EventOrganiserDetails.pdf");
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = requireContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);

                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                document.add(new Paragraph("User Details")
                        .setFontSize(18)
                        .setBold());

                Table table = createUserTable(userList);

                document.add(table);
                document.close();

                Toast.makeText(getActivity(), "PDF saved to Downloads!", Toast.LENGTH_LONG).show();
                getFragment(new AdminHome());
            } else {
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                pdfFile = new File(downloadDir, "UserDetails.pdf");

                PdfWriter writer = new PdfWriter(pdfFile);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                document.add(new Paragraph("User Details").setFontSize(18).setBold());
                Table table = createUserTable(userList);

                document.add(table);
                document.close();

                Toast.makeText(getActivity(), "PDF saved to Downloads!", Toast.LENGTH_LONG).show();
                getFragment(new AdminHome());
            }
        } catch (Exception e) {
            Log.e("PDF", "Error generating PDF: ", e);
            Toast.makeText(getActivity(), "Error downloading PDF!", Toast.LENGTH_SHORT).show();
        }
    }
    private Table createUserTable(List<Map<String, Object>> userList) {
        float[] columnWidths = {100, 30, 50, 50, 100, 50, 50};
        Table table = new Table(columnWidths);

        table.addHeaderCell("Name");
        table.addHeaderCell("Gender");
        table.addHeaderCell("Email");
        table.addHeaderCell("Phone");
        table.addHeaderCell("College");
        table.addHeaderCell("Role");
        table.addHeaderCell("Status");

        for (Map<String, Object> user : userList) {
            table.addCell((String) user.getOrDefault("name", "N/A"));
            table.addCell((String) user.getOrDefault("gender", "N/A"));
            table.addCell((String) user.getOrDefault("email", "N/A"));
            table.addCell((String) user.getOrDefault("contact", "N/A"));
            table.addCell((String) user.getOrDefault("college", "N/A"));
            table.addCell((String) user.getOrDefault("role", "N/A"));
            table.addCell((String) user.getOrDefault("status", "N/A"));
        }
        return table;
    }

    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}