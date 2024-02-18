package com.initezz.ebookshop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {

View view;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_setting, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        view.findViewById(R.id.changePasswordBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity().getApplicationContext(), "Check Your Email. Password has been updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }
}