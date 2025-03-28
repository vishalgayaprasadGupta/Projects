package com.example.myapplication.eventOrganiser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class storeData extends Fragment {

    private EditText edtGroupLink;
    private Button btnSave;
    private FirebaseFirestore db;
    FirebaseUser user;
    String eventName;

    public storeData() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_store_data, container, false);

        db = FirebaseFirestore.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();

        edtGroupLink = view.findViewById(R.id.edtGroupLink);
        btnSave = view.findViewById(R.id.btnSave);

        if(getArguments()!=null){
            eventName=getArguments().getString("eventName");
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLinkToFirestore();
            }
        });
        return view;
    }
    private void saveLinkToFirestore() {
        String groupLink = edtGroupLink.getText().toString().trim();

        if (TextUtils.isEmpty(groupLink)) {
            Toast.makeText(requireContext(), "Please enter a valid WhatsApp group link!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> groupData = new HashMap<>();
        groupData.put("groupLink", groupLink);
        groupData.put("userId", userId);

        db.collection(eventName)
                .document(userId)
                .set(groupData, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(requireContext(), "Group link saved successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error saving link: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }


}