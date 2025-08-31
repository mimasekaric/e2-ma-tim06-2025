package com.example.myhobitapplication.activities;

import static android.view.View.VISIBLE;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
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
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.adapters.AvatarSpinnerAdapter;
import com.example.myhobitapplication.databases.RegistrationRepository;
import com.example.myhobitapplication.databinding.ActivityRegistrationBinding;
import com.example.myhobitapplication.models.AvatarList;
import com.example.myhobitapplication.services.RegistrationService;
import com.example.myhobitapplication.viewModels.RegistrationViewModel;
import com.google.firebase.FirebaseApp;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private Spinner avatarspinner;
    private ActivityRegistrationBinding binding ;
    private RegistrationViewModel registrationViewModel;
    private RegistrationRepository repository;
    private AvatarSpinnerAdapter avataradapter;

    private List<Avatar> avatarList;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        repository = new RegistrationRepository();
        RegistrationService registrationService = new RegistrationService(repository);
        FirebaseApp.initializeApp(this);
        registrationViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RegistrationViewModel(registrationService);
            }
        }).get(RegistrationViewModel.class);




        EdgeToEdge.enable(this);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        avatarList = AvatarList.getAvatarList();
        avatarspinner = binding.avatarSpinner;
        avataradapter = new AvatarSpinnerAdapter(RegistrationActivity.this, avatarList);

        avatarspinner.setAdapter(avataradapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        animationView = binding.registrationLoading;
        animationView.setAnimation(R.raw.waiting);
        registrationViewModel.getRegistrationSuccess().observe(this, isSuccess -> {
            String message = registrationViewModel.getResponse().getValue();
            if (isSuccess) {
                    animationView.setVisibility(View.GONE);
                    animationView.cancelAnimation();
                    Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
            } else if (!message.isEmpty()) {
                binding.buttonn.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.GONE);
                animationView.cancelAnimation();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }});
    }

    @Override
protected void onResume(){
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
                registrationViewModel.setEmail(s.toString());
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
                registrationViewModel.setUsername(s.toString());
            }
        });

        binding.editTextText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationViewModel.setPassword(s.toString());
                binding.textView5.setVisibility(VISIBLE);
                binding.editTextText4.setVisibility(VISIBLE);
            }
        });

        binding.editTextText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationViewModel.setConfirmPassword(s.toString());
            }
        });

        binding.avatarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedAvatarPosition = position;
                registrationViewModel.setAvatarName( avatarList.get(selectedAvatarPosition).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                registrationViewModel.setAvatarName( avatarList.get(0).getName());
            }
        });
        binding.buttonn.setOnClickListener(v->{
            animationView.setVisibility(View.VISIBLE);
            binding.buttonn.setVisibility(View.INVISIBLE);
            animationView.playAnimation();
            registrationViewModel.saveUser();

            });

    }
}
