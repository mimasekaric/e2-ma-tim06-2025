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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databinding.ActivityHomeBBinding;
import com.example.myhobitapplication.activities.ProfileActivity;
import com.example.myhobitapplication.fragments.ActivateEquipmentFragment;
import com.example.myhobitapplication.fragments.ShopFragment;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.viewModels.LoginViewModel;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBBinding binding;
    private DrawerLayout drawerLayout;
    private LoginViewModel loginViewModel;
    private ProfileViewModel profileViewModel;
    private Profile profile;
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
        BossRepository bossRepository = new BossRepository(HomeActivity.this);
        EquipmentRepository equipmentRepository = new EquipmentRepository(HomeActivity.this);
        BossService bossService = new BossService(bossRepository);
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
       profileViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProfileViewModel(HomeActivity.this, bossService, equipmentService);
            }
        }).get(ProfileViewModel.class);

        loginViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LoginViewModel();
            }
        }).get(LoginViewModel.class);


        profileViewModel.loadProfile(userId);
        profileViewModel.getProfile().observe(this, loadedProfile -> {
                    if (loadedProfile != null) {
                        this.profile = loadedProfile;
                    }
                });

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
                }else if(id == R.id.nav_shop) {
                    ShopFragment shopFragment = new ShopFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, shopFragment)
                            .addToBackStack(null)
                            .commit();
                }

                else if(id == R.id.nav_activate) {
                    ActivateEquipmentFragment activateFragment = new ActivateEquipmentFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, activateFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if (id == R.id.nav_boss_battle) {
                    Intent intent = new Intent(HomeActivity.this, BossActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_category) {
                    Intent intent = new Intent(HomeActivity.this, CategoryViewActivity.class);
                    startActivity(intent);
                }


                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


}