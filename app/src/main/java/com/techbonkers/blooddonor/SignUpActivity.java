package com.techbonkers.blooddonor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    TextView logInButton;
    EditText fullName,userMail,userPass;
    Button signUpButton;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        refrences

        logInButton = findViewById(R.id.logIn);
        signUpButton = findViewById(R.id.signUpButton);
        userMail = findViewById(R.id.usermail);
        userPass = findViewById(R.id.password);
        fullName = findViewById(R.id.fullName);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LogInActivity.class));
                finishAffinity();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                checkUserCouldCreated();
            }
        });
    }

    private void checkUserCouldCreated() {
        String emailStr = userMail.getText().toString().trim(),
                passwordStr = userPass.getText().toString().trim(),
                fullNameStr = fullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullNameStr)){
            Toast.makeText(getApplicationContext(), "You can't leave your name empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if (TextUtils.isEmpty(emailStr) || TextUtils.isEmpty(passwordStr)) {
                Toast.makeText(getApplicationContext(), "Either email or password is empty", Toast.LENGTH_SHORT).show();
            }
            else {
                if (passwordStr.length()<6){
                    Toast.makeText(getApplicationContext(), "Password should be longer than 6", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    createUser(emailStr, passwordStr, fullNameStr);
                }
            }
        }
    }

    private void createUser(final String email, final String password, final String fullName) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToDB(email,password,fullName,user);
                        }
                        else {
                            Log.i("aunu", "createUserWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void addUserToDB(String email, String password, String fullName, FirebaseUser user) {

        User userData = new User(fullName,email,password);
        userData.setDonating(false);
        userData.setUserBloodType("Not found!");
        userData.setUserAddress("Not found!");
        userData.setUserPhone("Not found!");

        userRef.document(user.getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                finishAffinity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.w("aunu", "createUserWithEmail:failure"+ e);
            }
        });

    }
}