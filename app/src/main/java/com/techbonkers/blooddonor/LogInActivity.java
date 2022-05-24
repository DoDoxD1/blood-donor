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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    TextView signUpButton;
    EditText userMail,userPass;
    Button logInButton;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

//        refrences

        signUpButton = findViewById(R.id.signUp);
        logInButton = findViewById(R.id.loginButton);
        userMail = findViewById(R.id.usermail);
        userPass = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfUserCanLogin();
            }
        });
    }

    private void checkIfUserCanLogin() {
        String emailStr = userMail.getText().toString().trim(),
                passwordStr = userPass.getText().toString().trim();
        if (TextUtils.isEmpty(emailStr)||TextUtils.isEmpty(passwordStr)){
            Toast.makeText(getApplicationContext(),"Either email or password is empty",Toast.LENGTH_SHORT).show();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            logInUser(emailStr, passwordStr);
        }
    }

    private void logInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User.getCurrentUserFromDB();
                            startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                            finishAffinity();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.w("aunu", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Email or Password are incorrect",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}