package com.example.ngab_sen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, OnMapReadyCallback {

    EditText tanggalText;
    FirebaseFirestore db;
    Button button;
    String date;
    TextView textNama, textKelas, textAbsen, textJam, textLat, textLng, textStatus;
    String nama,kelas,absen,jam, lat,lng = "-";
    Marker mm;
    boolean ready = false;
    private GoogleMap gMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);

        tanggalText = findViewById(R.id.tanggal);
        button = findViewById(R.id.cari);
        textNama = findViewById(R.id.textNama);
        textKelas = findViewById(R.id.textKelas);
        textAbsen = findViewById(R.id.textAbsen);
        textJam = findViewById(R.id.textJam);
        textLat = findViewById(R.id.textLat);
        textLng = findViewById(R.id.textLng);
        textStatus = findViewById(R.id.textStatus);
        tanggalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date Picker");
            }
        });
        db = FirebaseFirestore.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date == null) {
                    Toast.makeText(getApplicationContext(), "Mohon Isi Tanggal", Toast.LENGTH_SHORT).show();
                    return;
                }
                DocumentReference dr = db.collection("Absensi").document(date).collection(MainActivity.getUserKelas()).document(MainActivity.getUserAbsen());
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                nama = doc.get("nama").toString();
                                absen = doc.get("absen").toString();
                                kelas = doc.get("kelas").toString();
                                jam = doc.get("waktu").toString();
                                lat = doc.get("lat").toString();
                                lng = doc.get("lng").toString();
                            }else {
                                Toast.makeText(getApplicationContext(), "Tidak Ada Data", Toast.LENGTH_SHORT).show();
                                nama = "";
                                kelas = "";
                                absen = "";
                                jam = "";
                                lat = "";
                                lng = "-";
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                textNama.setText("Nama: "+nama);
                textKelas.setText("Kelas: "+kelas);
                textAbsen.setText("Absen: "+absen);
                textJam.setText("Jam: "+jam);
                textLat.setText("Latitude: "+lat);
                textLng.setText("Longtitude: "+lng);

                try {
                    Date date = new SimpleDateFormat("hh:mm a").parse(jam);
                    Date date2 = new SimpleDateFormat("hh:mm a").parse("07:00 AM");
                    if(date.compareTo(date2)>=0) {
                        textStatus.setText("Status: Telat");
                    }else {
                        textStatus.setText("Status: Tidak Telat");
                    }
                }catch (Exception e) {
                    Log.e("DATE ERROR", e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String getDate = new SimpleDateFormat("dd-MM-YY").format(c.getTime());
        date = getDate;
        tanggalText.setText(getDate);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            if (mm!=null){
                mm.remove();
            }
            LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            mm = googleMap.addMarker(new MarkerOptions().position(location).title("Titik Absen"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
        }catch (Exception e){
            Log.e("MAP ERROR", e.getMessage());
        }

    }
}