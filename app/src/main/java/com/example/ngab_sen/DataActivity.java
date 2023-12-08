package com.example.ngab_sen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DataActivity extends AppCompatActivity {

    private Spinner spinner;
    String gkelas = "XII RPL A";
    EditText textNama, textAbsen;
    TextView textHead;
    Button button;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textNama = findViewById(R.id.nama);
        textAbsen = findViewById(R.id.absen);
        button = findViewById(R.id.update);
        textHead = findViewById(R.id.header);

        textHead.setText(MainActivity.getUserEmail());
        textNama.setText(MainActivity.getUserNama());
        textAbsen.setText(MainActivity.getUserAbsen());

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
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        db = FirebaseFirestore.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = textNama.getText().toString();
                String kelas = gkelas;
                String absen = textAbsen.getText().toString();

                if (TextUtils.isEmpty(nama)) {
                    Toast.makeText(getApplicationContext(), "Masukan Nama Lengkap", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(absen)) {
                    Toast.makeText(getApplicationContext(), "Masukan Nomer Absen", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> data = new HashMap<>();
                data.put("nama", nama);
                data.put("kelas", kelas);
                data.put("absen", absen);

                db.collection("Akun").document(MainActivity.getUserEmail()).set(data).addOnCompleteListener(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Sukses Mengupdate!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"CreateUserWithEmail failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}