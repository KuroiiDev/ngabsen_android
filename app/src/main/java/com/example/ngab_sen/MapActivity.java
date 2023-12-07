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
    private Spinner spinner;
    private EditText editNama, editAbsen;
    String gkelas = "";
    private Button btnAbsen;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseFirestore db;
    Marker mm;
    String getLat, getLng = "No Location";

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

        spinner = (Spinner) findViewById(R.id.spinKelas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.kelas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (spinner == null) {
            Toast.makeText(getApplicationContext(), "Spinner Null!!!", Toast.LENGTH_SHORT).show();
        }else {
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
            editNama = findViewById(R.id.namaLengkap);
            editAbsen = findViewById(R.id.noAbsen);
            btnAbsen = findViewById(R.id.absen);

            db = FirebaseFirestore.getInstance();

            btnAbsen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertData(gkelas);
                }
            });
        }
    }
    private void insertData(String getKelas){
        btnAbsen.setActivated(false);
        String nama = editNama.getText().toString();
        String kelas = getKelas;
        String absen = editAbsen.getText().toString();
        Date getDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY");
        String date = sdf.format(getDate);
        boolean isExisted = false;

        Map<String, Object> data = new HashMap<>();
        data.put("nama", nama);
        data.put("kelas", kelas);
        data.put("absen", absen);
        data.put("lat", getLat);
        data.put("lng", getLng);
        //data.put("tanggal", date);
        Log.d("DataLah", nama+"_"+kelas+"_"+absen+"_"+date);
        DocumentReference dr = db.collection("Absensi").document(kelas).collection(date).document(absen);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Toast.makeText(getApplicationContext(),"Anda Telah Absen Sebelumnya Hari Ini!!!",Toast.LENGTH_SHORT).show();
                        editNama.setText("");
                        editAbsen.setText("");
                    }else {
                        db.collection("Absensi").document(kelas).collection(date).document(absen).set(data).addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Sukses!",Toast.LENGTH_SHORT).show();
                                    editNama.setText("");
                                    editAbsen.setText("");
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