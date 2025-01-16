package com.example.myapplication.ManageUser;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.R;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class manageUser extends Fragment {
    public manageUser() {
        // Required empty public constructor
    }
    TextView addUser,updateUser,deactivateUser,activate,export;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_user, container, false);

        addUser=view.findViewById(R.id.addUser);
        updateUser=view.findViewById(R.id.updateUser);
        deactivateUser=view.findViewById(R.id.deactivateUser);
        activate=view.findViewById(R.id.activateUser);
        export=view.findViewById(R.id.export);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                        getFragment(new AdminHome());
                    }
                });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new AddUser());
            }
        });
        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new UpdateUser());
            }
        });
        deactivateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new fetchUserDetailsforDeactivate());
            }
        });
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new fetchUserDetailsandActivate());
            }
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadDialog();
            }
        });
        return view;
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Download PDF")
                .setMessage("Do you want to download the User Details as a PDF?")
                .setPositiveButton("Download", (dialog, which) -> {
                    // Fetch user data and generate the PDF
                    fetchUserData();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Close dialog
                    dialog.dismiss();
                })
                .show();

    }

    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User").get().addOnCompleteListener(task -> {
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
                values.put(MediaStore.Downloads.DISPLAY_NAME, "UserDetails.pdf");
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