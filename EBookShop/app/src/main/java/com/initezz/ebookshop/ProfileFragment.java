package com.initezz.ebookshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.initezz.ebookshop.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileFragment extends Fragment {
    View view;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ImageButton profileImgBtn;
    private Uri imagePath;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        EditText profileUNameEditText=view.findViewById(R.id.profileUNameEditText);
        TextView profileEmailText=view.findViewById(R.id.profileEmailText);
        EditText profileMobileText=view.findViewById(R.id.profileMobileText);
        EditText profileAddressLine1EditText=view.findViewById(R.id.profileAddressLine1EditText);
        EditText profileAddressLine2EditText=view.findViewById(R.id.profileAddressLine2EditText);
        EditText profileAddressCityEditText=view.findViewById(R.id.profileAddressCityEditText);
        EditText profileAddressPostalEditText=view.findViewById(R.id.profileAddressPostalEditText);

        //////////////////////////////Load Data
        if(firebaseAuth.getCurrentUser()!=null&&firebaseAuth.getCurrentUser().isEmailVerified()){
            firestore.collection("Users")
                    .whereEqualTo("email",firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                User user = documentSnapshot.toObject(User.class);

                                profileUNameEditText.setText(user.getUsername());
                                profileEmailText.setText(user.getEmail());
                                if(user.getMobile()!=null){
                                    profileMobileText.setText(user.getMobile());
                                }if(user.getLine1()!=null){
                                    profileAddressLine1EditText.setText(user.getLine1());
                                }if(user.getLine2()!=null){
                                    profileAddressLine2EditText.setText(user.getLine2());
                                }if(user.getCity()!=null){
                                    profileAddressCityEditText.setText(user.getCity());
                                }if(user.getPostal()!=null){
                                    profileAddressPostalEditText.setText(user.getPostal());
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Please Login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity().getApplicationContext(),LoginActivity.class));
        }
        //////////////////////////////Load Data

        //////////////////////////////Update Profile
        view.findViewById(R.id.updateProfileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uUserName = profileUNameEditText.getText().toString();
                String uEmail = profileEmailText.getText().toString();
                String uMobile = profileMobileText.getText().toString();
                String uAddressLine1 = profileAddressLine1EditText.getText().toString();
                String uAddressLine2 = profileAddressLine2EditText.getText().toString();
                String uCity = profileAddressCityEditText.getText().toString();
                String uPostal = profileAddressPostalEditText.getText().toString();


                if (uUserName.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter username", Toast.LENGTH_SHORT).show();
                } else if (uMobile.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter mobile", Toast.LENGTH_SHORT).show();
                } else if (uAddressLine1.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter address line 1", Toast.LENGTH_SHORT).show();
                } else if (uAddressLine2.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter address line 2", Toast.LENGTH_SHORT).show();
                }else if (uCity.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter city", Toast.LENGTH_SHORT).show();
                }else if (uPostal.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter postal code", Toast.LENGTH_SHORT).show();
                } else {

                    firestore.collection("Users")
                            .whereNotEqualTo("email", firebaseAuth.getCurrentUser().getEmail()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        String ref = String.valueOf(System.currentTimeMillis());

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("username", uUserName);
                                        updates.put("password", uUserName);
                                        updates.put("mobile", uMobile);
                                        updates.put("line1", uAddressLine1);
                                        updates.put("line2", uAddressLine2);
                                        updates.put("city", uCity);
                                        updates.put("postal", uPostal);

                                        firestore.collection("Users")
                                                .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            List<String> documentIds = new ArrayList<>();

                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String documentId = document.getId();

                                                                firestore.collection("Users").document(documentId).update(updates)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(getActivity().getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }

                                                        } else {
                                                        }
                                                    }
                                                });
                                }
                            });
                }

            }
        });
        //////////////////////////////Update Profile

        return view;
    }
}