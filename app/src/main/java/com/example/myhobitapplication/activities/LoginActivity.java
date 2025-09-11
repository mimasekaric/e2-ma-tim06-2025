package com.example.myhobitapplication.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.AppDataBaseHelper;
import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.UserRepository;
import com.example.myhobitapplication.databinding.ActivityLoginBinding;
import com.example.myhobitapplication.services.UserService;
import com.example.myhobitapplication.viewModels.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    LottieAnimationView animationView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDataBaseHelper dbHelper = new AppDataBaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        animationView = binding.registrationLoading;
        animationView.setAnimation(R.raw.waiting);

        loginViewModel= new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LoginViewModel();
            }
        }).get(LoginViewModel.class);
        EdgeToEdge.enable(this);
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginViewModel.getLoginSuccess().observe(this, isSuccess -> {
            String message = loginViewModel.getResponse().getValue();
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                if (isSuccess) {
             //       Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    //Toast.makeText(this,FirebaseAuth.getInstance().getCurrentUser().getUid(),SHORT)
                    intent.putExtra("USER_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(intent);
                   /* Intent intent = new Intent(LoginActivity.this, TaskActivity.class);
                    startActivity(intent);*/
                }
                binding.buttonn.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.GONE);
                animationView.cancelAnimation();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.editTextText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginViewModel.setEmail(s.toString());
            }
        });

        binding.editTextText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginViewModel.setPassword(s.toString());
            }
        });

        binding.buttonn.setOnClickListener(v -> {
            binding.buttonn.setVisibility(View.INVISIBLE);
            animationView.setVisibility(View.VISIBLE);
            animationView.playAnimation();
            loginViewModel.loginUser();

        });

        binding.signup.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

    }
}