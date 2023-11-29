package com.example.ngab_sen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ngab_sen.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

//    FirebaseAuth auth;
//    Button button;
//    TextView textView;
//    FirebaseUser user;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
                switch (item.getItemId()) {
                    case R.id.home:
                        replaceFragment(new HomeFragment());
                        break;

                    case R.id.account:
                        replaceFragment(new AccountFragment());
                        break;
                }
                return true;
        });

//        auth = FirebaseAuth.getInstance();
//        button = (Button) findViewById(R.id.logout);
//        textView = (TextView) findViewById(R.id.userDetails);
//        user = auth.getCurrentUser();
//        if (user == null) {
//            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            finish();
//        }else {
//            textView.setText(user.getEmail());
//        }
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                if (auth.getCurrentUser() == null) {
//                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                    finish();
//                }
//            }
//        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        ft.commit();
    }
}