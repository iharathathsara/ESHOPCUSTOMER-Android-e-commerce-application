package com.initezz.ebookshop;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.initezz.ebookshop.model.User;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getName();
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private SignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(getApplicationContext());

        Animation view6anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_down);
        view6anime.setDuration(2000);
        view6anime.setStartOffset(400);
        view6anime.setFillAfter(true);
        findViewById(R.id.view6).startAnimation(view6anime);

        Animation view5anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_down);
        view5anime.setDuration(2000);
        view5anime.setStartOffset(200);
        view5anime.setFillAfter(true);
        findViewById(R.id.view5).startAnimation(view5anime);

        Animation view4anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_down);
        view4anime.setDuration(2000);
        view4anime.setFillAfter(true);
        findViewById(R.id.view4).startAnimation(view4anime);

        Animation textView2anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_down);
        textView2anime.setDuration(1000);
        textView2anime.setFillAfter(true);
        findViewById(R.id.textView2).startAnimation(textView2anime);

        Animation imageView3anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_down);
        imageView3anime.setDuration(1000);
        imageView3anime.setStartOffset(100);
        imageView3anime.setFillAfter(true);
        findViewById(R.id.imageView3).startAnimation(imageView3anime);

        Animation textView3anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        textView3anime.setDuration(1000);
        textView3anime.setFillAfter(true);
        findViewById(R.id.textView3).startAnimation(textView3anime);

        Animation userNameTxtanime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        userNameTxtanime.setDuration(1000);
        userNameTxtanime.setStartOffset(100);
        userNameTxtanime.setFillAfter(true);
        findViewById(R.id.userNameTxt).startAnimation(userNameTxtanime);

        Animation textView4anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        textView4anime.setDuration(1000);
        textView4anime.setStartOffset(200);
        textView4anime.setFillAfter(true);
        findViewById(R.id.textView4).startAnimation(textView4anime);

        Animation userEmailTxtanime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        userEmailTxtanime.setDuration(1000);
        userEmailTxtanime.setStartOffset(300);
        userEmailTxtanime.setFillAfter(true);
        findViewById(R.id.userEmailTxt).startAnimation(userEmailTxtanime);

        Animation textView5anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        textView5anime.setDuration(1000);
        textView5anime.setStartOffset(400);
        textView5anime.setFillAfter(true);
        findViewById(R.id.textView5).startAnimation(textView5anime);

        Animation userMobileTxtanime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        userMobileTxtanime.setDuration(1000);
        userMobileTxtanime.setStartOffset(500);
        userMobileTxtanime.setFillAfter(true);
        findViewById(R.id.userMobileTxt).startAnimation(userMobileTxtanime);

        Animation textView6anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        textView6anime.setDuration(1000);
        textView6anime.setStartOffset(600);
        textView6anime.setFillAfter(true);
        findViewById(R.id.textView6).startAnimation(textView6anime);

        Animation userPasswordTxtanime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        userPasswordTxtanime.setDuration(1000);
        userPasswordTxtanime.setStartOffset(700);
        userPasswordTxtanime.setFillAfter(true);
        findViewById(R.id.userPasswordTxt).startAnimation(userPasswordTxtanime);

        Animation registerBtnanime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        registerBtnanime.setDuration(1000);
        registerBtnanime.setStartOffset(800);
        registerBtnanime.setFillAfter(true);
        findViewById(R.id.registerBtn).startAnimation(registerBtnanime);

        Animation textView9anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        textView9anime.setDuration(1000);
        textView9anime.setStartOffset(900);
        textView9anime.setFillAfter(true);
        findViewById(R.id.textView9).startAnimation(textView9anime);

        Animation textView10anime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        textView10anime.setDuration(1000);
        textView10anime.setStartOffset(1000);
        textView10anime.setFillAfter(true);
        findViewById(R.id.textView10).startAnimation(textView10anime);

        Animation regGoogleanime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        regGoogleanime.setDuration(1000);
        regGoogleanime.setStartOffset(1100);
        regGoogleanime.setFillAfter(true);
        findViewById(R.id.regGoogle).startAnimation(regGoogleanime);


        Animation gotoLoginLinkanime = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.fade_up);
        gotoLoginLinkanime.setDuration(1000);
        gotoLoginLinkanime.setStartOffset(1200);
        gotoLoginLinkanime.setFillAfter(true);
        findViewById(R.id.gotoLoginLink).startAnimation(gotoLoginLinkanime);


        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText userNameText = findViewById(R.id.userNameTxt);
                EditText userEmailText = findViewById(R.id.userEmailTxt);
                EditText userMobileText = findViewById(R.id.userMobileTxt);
                EditText userPasswordText = findViewById(R.id.userPasswordTxt);

                String userId = UUID.randomUUID().toString();
                String userName = userNameText.getText().toString();
                String email = userEmailText.getText().toString();
                String mobile = userMobileText.getText().toString();
                String password = userPasswordText.getText().toString();

                if (userName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter User Name", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else if (mobile.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Mobile", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {

                    firestore.collection("Users").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                boolean existsCat = false;

                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        User existsUser = snapshot.toObject(User.class);

                                        if (email.equals(existsUser.getEmail())) {
                                            existsCat = true;
                                        }
                                    }
                                    if (existsCat) {
                                        Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                                        dialog.setMessage("Registering...");
                                        dialog.setCancelable(false);
                                        dialog.show();
                                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful()) {

                                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                                    currentUser.sendEmailVerification();

                                                    User user = new User(userId, userName, email, mobile, password);

                                                    firestore.collection("Users").add(user)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });

        findViewById(R.id.gotoLoginLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        findViewById(R.id.regGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSignInIntentRequest signInIntentRequest = GetSignInIntentRequest.builder()
                        .setServerClientId(getString(R.string.web_client_id)).build();

                Task<PendingIntent> signInIntent = signInClient.getSignInIntent(signInIntentRequest);
                signInIntent.addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
                        signInLauncher.launch(intentSenderRequest);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            handleSignInResult(o.getData());
                        }
                    });

    private void handleSignInResult(Intent intent) {
        try {
            SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(intent);
            String idToken = signInCredential.getGoogleIdToken();
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        Task<AuthResult> authResultTask = firebaseAuth.signInWithCredential(authCredential);
        authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    String userId = UUID.randomUUID().toString();
                    String email = user.getEmail();
                    String userName = user.getDisplayName();
                    String mobile = user.getPhoneNumber();

                    firestore.collection("Users").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                boolean existsCat = false;

                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        User existsUser = snapshot.toObject(User.class);

                                        if (email.equals(existsUser.getEmail())) {
                                            existsCat = true;
                                        }
                                    }
                                    if (existsCat) {
                                        Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                                        dialog.setMessage("Registering...");
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        User user = new User(userId, userName, email, mobile);

                                        firestore.collection("Users").add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                }
                            });

                    updateUI(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

}