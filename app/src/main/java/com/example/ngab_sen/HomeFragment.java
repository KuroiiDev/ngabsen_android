package com.example.ngab_sen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    TextView textHalo, textStatus;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    static String userEmail, userNama, userKelas, userAbsen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, null);

        textHalo = root.findViewById(R.id.textHalo);
        textStatus = root.findViewById(R.id.statusAbsen);

        Date getDate = new Date();
        String date = new SimpleDateFormat("dd-MM-YY").format(getDate);

        if (MainActivity.getUserNama() == null) {
            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
            getActivity().finish();
        }
        textHalo.setText("Halo, "+MainActivity.getUserNama()+"!");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            userEmail = user.getEmail();
            DocumentReference dr = db.collection("Akun").document(user.getEmail());
            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            userKelas = doc.get("kelas").toString();
                            userAbsen = doc.get("absen").toString();
                        }
                    }
                }

            });
        }
        try {
            DocumentReference dr2 = db.collection("Absensi").document(date).collection(userKelas).document(userAbsen);
            dr2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            textStatus.setText("Anda Sudah Absen Hari Ini");
                            textStatus.setTextColor(getResources().getColor(R.color.done));
                        }else {
                            textStatus.setText("Anda Belum Absen Hari Ini");
                            textStatus.setTextColor(getResources().getColor(R.color.dont));
                        }
                    }
                }
            });
        }catch (Exception e){
            Log.e("Absensi", e.getMessage());
        }
        return root;
    }
}