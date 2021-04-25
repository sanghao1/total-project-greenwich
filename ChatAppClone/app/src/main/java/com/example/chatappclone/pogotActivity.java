package com.example.chatappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class pogotActivity extends AppCompatActivity {
TextView send_email;
Button send;
FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pogot);
        send_email = findViewById(R.id.Pass_logo);
        send = findViewById(R.id.pogot_link);
        firebaseAuth = firebaseAuth.getInstance();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = send_email.getText().toString();

                if (email.equals(""))
                {
                    Toast.makeText(pogotActivity.this, "all file are requied", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(pogotActivity.this, "Please check E mail", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent( pogotActivity.this,SignInActivity.class));
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(pogotActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }


}