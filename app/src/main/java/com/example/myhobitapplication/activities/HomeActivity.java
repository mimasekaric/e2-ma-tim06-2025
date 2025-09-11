package com.example.myhobitapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databinding.ActivityHomeBBinding;
import com.example.myhobitapplication.activities.ProfileActivity;
import com.example.myhobitapplication.viewModels.LoginViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBBinding binding;
    private DrawerLayout drawerLayout;
    private LoginViewModel loginViewModel;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        LoginViewModel viewModel= new LoginViewModel();
         userId = intent.getStringExtra("USER_ID");
        binding = ActivityHomeBBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.getRoot().findViewById(R.id.m);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher_menu);

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    /*Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.putExtra("USER_ID",userId);
                    startActivity(intent);*/

                    ProfileActivity profileFragment = new ProfileActivity();


                    Bundle args = new Bundle();
                    args.putString("USER_ID", userId);
                    profileFragment.setArguments(args);


                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, profileFragment)
                            .addToBackStack(null)
                            .commit();
                } else if (id == R.id.nav_logout) {
                    //Toast.makeText(HomeActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                    viewModel.logout();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }else if(id == R.id.nav_tasks) {
                    Intent intent = new Intent(HomeActivity.this, TaskActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_boss_battle) {
                    Intent intent = new Intent(HomeActivity.this, BossActivity.class);
                    startActivity(intent);
                }


                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


}