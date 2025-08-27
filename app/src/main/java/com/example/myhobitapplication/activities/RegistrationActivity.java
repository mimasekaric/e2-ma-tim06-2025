package com.example.myhobitapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

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
import com.example.myhobitapplication.databinding.ActivityRegistrationBinding;
import com.example.myhobitapplication.services.RegistrationService;
import com.example.myhobitapplication.viewModels.RegistrationViewModel;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding ;
    private RegistrationViewModel registrationViewModel;
    private RegistrationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
protected void onResume(){
        super.onResume();
        repository = new RegistrationRepository();
        RegistrationService registrationService = new RegistrationService(repository);

        registrationViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RegistrationViewModel(registrationService);
            }
        }).get(RegistrationViewModel.class);


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

        binding.editTextText2.addTextChangedListener(new TextWatcher() {
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

        binding.editTextText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationViewModel.setPassword(s.toString());
            }
        });


        binding.buttonn.setOnClickListener(v->{
            registrationViewModel.saveUser();
            Intent intent = new Intent(RegistrationActivity.this, TaskActivity.class);
            startActivity(intent);
        });

    }
}


    /*    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new CategoryRepository(requireContext());
        CategoryService categoryService = new CategoryService(repository);



        categoryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CategoryViewModel(categoryService);
            }
        }).get(CategoryViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        categoryBinding = FragmentCategoryBinding.inflate(inflater, container, false);
        return categoryBinding.getRoot();
    }

}*/