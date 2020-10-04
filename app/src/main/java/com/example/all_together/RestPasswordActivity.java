package com.example.all_together;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RestPasswordActivity extends AppCompatActivity {

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_password);

        Button confirmEmail = findViewById(R.id.confirm_email);
        final EditText emailTv =findViewById(R.id.email_for_reset_password);

        confirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailTv.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RestPasswordActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RestPasswordActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else
                                    Toast.makeText(RestPasswordActivity.this, "Email Error", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}