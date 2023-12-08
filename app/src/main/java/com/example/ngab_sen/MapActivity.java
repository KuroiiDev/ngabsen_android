package com.example.ngab_sen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.example.ngab_sen.databinding.ActivityMapBinding;
import com.google.android.gms.tasks.Task;

import java.util.Map;
import java.util.HashMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GetAddressTask.OnTaskCompleted {

    private GoogleMap mMap;
    private TextView locationTextView;
    private ActivityMapBinding binding;
    static final int REQUEST_LOCATION_PERMISSION=1;
    private Button btnAbsen;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth auth;
    EditText editNama, editKelas, editAbsen;
    Marker mm;
    String getLat, getLng = "No Location";
    String nama, kelas, absen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationTextView=findViewById(R.id.Lokasi);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnAbsen = findViewById(R.id.absen);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editNama = findViewById(R.id.namaT);
        editKelas = findViewById(R.id.kelasT);
        editAbsen = findViewById(R.id.absenT);

        DocumentReference drs = db.collection("Akun").document(user.getEmail());
        drs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot doc1 = task1.getResult();
                    if (doc1.exists()) {
                        nama = doc1.get("nama").toString();
                        kelas = doc1.get("kelas").toString();
                        absen = doc1.get("absen").toString();

                        editNama.setText(nama);
                        editKelas.setText(kelas);
                        editAbsen.setText(absen);
                    }
                }
            }
        });
        btnAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    insertData();
                }
        });
    }
    private void insertData(){
        btnAbsen.setActivated(false);

        Date getDate = new Date();
        String date = new SimpleDateFormat("dd-MM-YY").format(getDate);
        String time = new SimpleDateFormat("hh:mm").format(getDate);
        boolean isExisted = false;

        if (nama == null || kelas == null || absen == null ){
            Toast.makeText(getApplicationContext(),"Kesalahan!",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("nama", nama);
        data.put("kelas", kelas);
        data.put("absen", absen);
        data.put("waktu", time);
        data.put("lat", getLat);
        data.put("lng", getLng);

        Log.d("DataLah", nama+"_"+kelas+"_"+absen+"_"+date);
        DocumentReference dr = db.collection("Absensi").document(date).collection(kelas).document(absen);

        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Toast.makeText(getApplicationContext(),"Anda Telah Absen Sebelumnya Hari Ini!!!",Toast.LENGTH_SHORT).show();
                    }else {
                        db.collection("Absensi").document(date).collection(kelas).document(absen).set(data).addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Sukses!",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Gagal!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        btnAbsen.setActivated(true);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();
    }
    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }else {
            fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (mm!=null){
                        mm.remove();
                    }
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        String lat = location.getLatitude()+"";
                        getLat = lat;
                        String lng = location.getLongitude()+"";
                        getLng = lng;
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        mm =mMap.addMarker(new MarkerOptions().position(loc).title("LAT: "+lat+" - LNG: "+lng));
                        float zoom = 15;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,zoom));
                        new GetAddressTask(MapActivity.this, MapActivity.this).execute(location);
                    }
                }
            }, null);
        }
    }
    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }
        }
    }

    @Override
    public void onTaskCompleted(String result){
        locationTextView.setText("Alamat: "+result);
    }
}