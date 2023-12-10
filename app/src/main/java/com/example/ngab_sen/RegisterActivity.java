package com.example.ngab_sen;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText textEmail, textPassword, textConfirm, editNama, editAbsen;
    TextView textView;
    Button button;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String gkelas = "XII RPL A";
    private Spinner spinner;
    FirebaseFirestore db;
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
        editNama = findViewById(R.id.nama);
        editAbsen = findViewById(R.id.absen);

        spinner = (Spinner) findViewById(R.id.spinKelas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.kelas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (spinner == null) {
            Toast.makeText(getApplicationContext(), "Spinner Null!!!", Toast.LENGTH_SHORT).show();
        } else {
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    gkelas = adapter.getItem(position).toString();
                    //Toast.makeText(getApplicationContext(), adapter.getItem(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            });
            db = FirebaseFirestore.getInstance();
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    String email, password, confirm;
                    email = textEmail.getText().toString();
                    password = textPassword.getText().toString();
                    confirm = textConfirm.getText().toString();
                    String nama = editNama.getText().toString();
                    String kelas = gkelas;
                    String absen = editAbsen.getText().toString();


                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Masukan Alamat Email", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Masukan Password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    } else if (TextUtils.isEmpty(nama)) {
                        Toast.makeText(getApplicationContext(), "Masukan Nama Lengkap", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    } else if (TextUtils.isEmpty(absen)) {
                        Toast.makeText(getApplicationContext(), "Masukan Nomer Absen", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    } else if (!(confirm.equals(password))) {
                        Toast.makeText(getApplicationContext(), "Password Tidak Sesuai", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isComplete()) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("nama", nama);
                                data.put("kelas", kelas);
                                data.put("absen", absen);

                                DocumentReference dr = db.collection("Akun").document(email);
                                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot doc = task.getResult();
                                            if (!doc.exists()) {
                                                db.collection("Akun").document(email).set(data).addOnCompleteListener(new OnCompleteListener<Void>(){
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(getApplicationContext(),"Sukses!",Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(getApplicationContext(),"CreateUserWithEmail failed",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                                Log.d(TAG, "CreateUserWithEmail:success");
                                Toast.makeText(getApplicationContext(), "Akun Dibuat!", Toast.LENGTH_SHORT);
                                FirebaseUser user = mAuth.getCurrentUser();

                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
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
}