package com.example.finalapp.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalapp.R;
import com.example.finalapp.user.agent.AgentFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


public class ChangePasswordFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Context myContext;

    private EditText oldPassword, newPassword;
    private Button changePassword;
    private String textOldPassword, textNewPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        mAuth = FirebaseAuth.getInstance();

        oldPassword = view.findViewById(R.id.editText1);
        newPassword = view.findViewById(R.id.editText2);
        changePassword = view.findViewById(R.id.button);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textOldPassword = oldPassword.getText().toString();
                textNewPassword = newPassword.getText().toString();
                if (textNewPassword.length()>5) {
                    AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), textOldPassword);
                    mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().updatePassword(textNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(myContext, "Something went wrong. Please try again later", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(myContext, "Password Successfully Modified", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(myContext, "Authentication Faile", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else Toast.makeText(myContext, "New password is too short", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}

