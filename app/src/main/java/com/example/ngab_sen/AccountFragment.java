package com.example.ngab_sen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class AccountFragment extends Fragment {

    FirebaseAuth auth;
    Button button, buttonPass, buttonDat;
    TextView textEmail, textUser, textKelas;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_account, null);
        auth = FirebaseAuth.getInstance();
        button = (Button) root.findViewById(R.id.logout);
        buttonPass = (Button) root.findViewById(R.id.password);
        buttonDat = (Button) root.findViewById(R.id.dataAkun);
        textEmail = (TextView) root.findViewById(R.id.userDetails);
        textUser = (TextView) root.findViewById(R.id.userName);
        textKelas = (TextView) root.findViewById(R.id.userKelas);

        textEmail.setText(MainActivity.getUserEmail());
        textUser.setText(MainActivity.getUserNama());
        textKelas.setText(MainActivity.getUserKelas());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                if (auth.getCurrentUser() == null) {
                    startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        });
        buttonPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(MainActivity.getUserEmail());
                Toast.makeText(getActivity().getApplicationContext(), "Terkirim! Mohon Cek Email", Toast.LENGTH_SHORT).show();
            }
        });
        buttonDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), DataActivity.class));
            }
        });
        return root;
    }
}