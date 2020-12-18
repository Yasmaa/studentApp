package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.finalapp.user.PrincipalActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText textEmail, textPass;
    private Button logIn;
    private ProgressBar progressBar;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        textEmail = findViewById(R.id.editText1);
        textPass = findViewById(R.id.editText2);
        logIn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = textEmail.getText().toString();
                password = textPass.getText().toString();
                if (validateForm(email, password)) userLogIn(email, password);
            }
        });
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            textEmail.setError("Email required.");
            textEmail.requestFocus();
            valid = false;
        } else {
            textEmail.setError(null);
        }

        if (password.isEmpty()) {
            textPass.setError("Password required.");
            textPass.requestFocus();
            valid = false;
        } else {
            textPass.setError(null);
        }
        return valid;
    }

    private void userLogIn(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email+"@maintenance.inpt.ma", password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startPrincipalActivity();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void startPrincipalActivity() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}


