package com.example.myhobitapplication.activities;

import static android.view.View.VISIBLE;

import android.content.Intent;
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

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.RegistrationRepository;
import com.example.myhobitapplication.databinding.ActivityLoginBinding;
import com.example.myhobitapplication.databinding.ActivityRegistrationBinding;
import com.example.myhobitapplication.services.RegistrationService;
import com.example.myhobitapplication.viewModels.LoginViewModel;
import com.example.myhobitapplication.viewModels.RegistrationViewModel;
import com.google.firebase.FirebaseApp;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private RegistrationRepository repository;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        //FirebaseApp.initializeApp(this);

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        repository = new RegistrationRepository();
        RegistrationService registrationService = new RegistrationService(repository);
        loginViewModel= new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LoginViewModel(registrationService);
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
            loginViewModel.loginUser();
            loginViewModel.getLoginSuccess().observe(this, isSuccess -> {
                if (isSuccess) {
                    Toast.makeText(this, loginViewModel.getResponse().getValue(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, TaskActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(this, loginViewModel.getResponse().getValue(), Toast.LENGTH_SHORT).show();
            });
        });

    }
}