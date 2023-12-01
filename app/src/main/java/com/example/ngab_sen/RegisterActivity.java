package com.example.ngab_sen;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText textEmail, textPassword, textConfirm;
    TextView textView;
    Button button;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.textLogin);
        button = findViewById(R.id.register);
        textEmail = findViewById(R.id.email);
        textPassword = findViewById(R.id.password);
        textConfirm = findViewById(R.id.con_password);
        progressBar = findViewById(R.id.progressBar);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, confirm;
                email = textEmail.getText().toString();
                password = textPassword.getText().toString();
                confirm = textConfirm.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!(confirm.equals(password))) {
                    Toast.makeText(getApplicationContext(), "Confirm Password Different", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isComplete()) {
                            Log.d(TAG, "CreateUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT);
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            Log.d(TAG, "CreateUserWithEmail:failed");
                            Toast.makeText(getApplicationContext(), "CreateUserWithEmail failed", Toast.LENGTH_SHORT);
                            //updateUI(null);
                        }
                    }
                });
            }
        });
    }
}