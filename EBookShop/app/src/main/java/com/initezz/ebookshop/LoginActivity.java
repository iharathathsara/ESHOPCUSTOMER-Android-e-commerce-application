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

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.initezz.ebookshop.model.User;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getName();
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private SignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(getApplicationContext());

        Animation view6anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_down);
        view6anime.setDuration(2000);
        view6anime.setStartOffset(400);
        view6anime.setFillAfter(true);
        findViewById(R.id.view6).startAnimation(view6anime);

        Animation view5anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_down);
        view5anime.setDuration(2000);
        view5anime.setStartOffset(200);
        view5anime.setFillAfter(true);
        findViewById(R.id.view5).startAnimation(view5anime);

        Animation view4anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_down);
        view4anime.setDuration(2000);
        view4anime.setFillAfter(true);
        findViewById(R.id.view4).startAnimation(view4anime);

        Animation textView2Anim= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_down);
        textView2Anim.setDuration(1000);
        textView2Anim.setStartOffset(400);
        textView2Anim.setFillAfter(true);
        findViewById(R.id.textView2).startAnimation(textView2Anim);

        Animation imageView3Anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_down);
        imageView3Anime.setDuration(1000);
        imageView3Anime.setStartOffset(600);
        imageView3Anime.setFillAfter(true);
        findViewById(R.id.imageView3).startAnimation(imageView3Anime);

        Animation textView3Anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        textView3Anime.setDuration(1000);
        textView3Anime.setStartOffset(600);
        textView3Anime.setFillAfter(true);
        findViewById(R.id.textView3).startAnimation(textView3Anime);

        Animation userEmailTxtAnime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        userEmailTxtAnime.setDuration(1000);
        userEmailTxtAnime.setStartOffset(700);
        userEmailTxtAnime.setFillAfter(true);
        findViewById(R.id.userEmailTxt).startAnimation(userEmailTxtAnime);

        Animation textView6Anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        textView6Anime.setDuration(1000);
        textView6Anime.setStartOffset(800);
        textView6Anime.setFillAfter(true);
        findViewById(R.id.textView6).startAnimation(textView6Anime);

        Animation userPasswordTxtAnime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        userPasswordTxtAnime.setDuration(1000);
        userPasswordTxtAnime.setStartOffset(900);
        userPasswordTxtAnime.setFillAfter(true);
        findViewById(R.id.userPasswordTxt).startAnimation(userPasswordTxtAnime);

        Animation loginbtnAnime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        loginbtnAnime.setDuration(1000);
        loginbtnAnime.setStartOffset(1000);
        loginbtnAnime.setFillAfter(true);
        findViewById(R.id.loginbtn).startAnimation(loginbtnAnime);

        Animation textView9Anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        textView9Anime.setDuration(1000);
        textView9Anime.setStartOffset(1100);
        textView9Anime.setFillAfter(true);
        findViewById(R.id.textView9).startAnimation(textView9Anime);

        Animation textView10Anime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        textView10Anime.setDuration(1000);
        textView10Anime.setStartOffset(1200);
        textView10Anime.setFillAfter(true);
        findViewById(R.id.textView10).startAnimation(textView10Anime);

        Animation loginGoogleAnime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        loginGoogleAnime.setDuration(1000);
        loginGoogleAnime.setStartOffset(1300);
        loginGoogleAnime.setFillAfter(true);
        findViewById(R.id.loginGoogle).startAnimation(loginGoogleAnime);

        Animation gotoRegisterLinkAnime= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.fade_up);
        gotoRegisterLinkAnime.setDuration(1000);
        gotoRegisterLinkAnime.setStartOffset(1400);
        gotoRegisterLinkAnime.setFillAfter(true);
        findViewById(R.id.gotoRegisterLink).startAnimation(gotoRegisterLinkAnime);

        findViewById(R.id.loginbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText userEmailText = findViewById(R.id.userEmailTxt);
                EditText userPasswordText = findViewById(R.id.userPasswordTxt);

                String email = userEmailText.getText().toString();
                String password = userPasswordText.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                }else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {

                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                        }else{
                                            Toast.makeText(getApplicationContext(),"Please verify your email",Toast.LENGTH_SHORT).show();
                                        }

                                    }else{
                                        Toast.makeText(getApplicationContext(),"Invalid Email or Password",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG,e.getMessage());
                                }
                            });

                }

            }
        });

        findViewById(R.id.gotoRegisterLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        findViewById(R.id.loginGoogle).setOnClickListener(new View.OnClickListener() {
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
                    String email=user.getEmail();
                    String userName=user.getDisplayName();
                    String mobile=user.getPhoneNumber();

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
                                        Toast.makeText(getApplicationContext(), "Log In Success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
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
                                                        Log.i(TAG, e.getMessage());
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
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}