package com.example.myhobitapplication.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databinding.FragmentCategoryBinding;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.fragments.tasksFragments.OneTimeTaskEditFragment;
import com.example.myhobitapplication.fragments.tasksFragments.RecurringTaskEditFragment;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.viewModels.categoryViewModels.CategoryViewModel;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoryCreateActivity extends AppCompatActivity {

    private int selectedColor = Color.BLACK;

    private FragmentCategoryBinding binding;


    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CategoryRepository repository = new CategoryRepository(this);
        CategoryService categoryService = new CategoryService(repository);

        categoryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CategoryViewModel(categoryService);
            }
        }).get(CategoryViewModel.class);


//        binding.selectedColorPreview.setBackgroundColor(selectedColor);

        binding.pickColorButton.setOnClickListener(v -> {
            openColorPickerDialog();
        });

//        binding.setColorButton.setOnClickListener(v -> {
//            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
//            categoryViewModel.setColour(hexColor);
//        });

        categoryViewModel.isFormValid().observe(this, isValid -> {
            if (isValid != null) {
                binding.btnAddCategory.setEnabled(isValid);
            }
        });


        categoryViewModel.getNameError().observe(this, errorMessage -> {
            binding.ctgName.setError(errorMessage);
        });

        binding.ctgName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryViewModel.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        categoryViewModel.getSubmissionError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Error saving")
                        .setMessage(error)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        categoryViewModel.getColour().observe(this, hexColor -> {
            if (hexColor != null) {
                try {
                    binding.selectedColorPreview.setBackgroundColor(Color.parseColor(hexColor));
                } catch (IllegalArgumentException e) {
                    binding.selectedColorPreview.setBackgroundColor(Color.BLACK);
                }
            }
        });

        categoryViewModel.getSaveSuccessEvent().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {

                Toast.makeText(this, "Category successfully saved!", Toast.LENGTH_SHORT).show();

                setResult(Activity.RESULT_OK);

                finish();

                categoryViewModel.onSaveSuccessEventHandled();
            }
        });


        binding.btnAddCategory.setOnClickListener(v -> {
            categoryViewModel.saveCategory();
        });
    }

    private void openColorPickerDialog() {
        String currentColorHex = categoryViewModel.getColour().getValue();
        int initialColor = Color.BLACK;
        if (currentColorHex != null) {
            try {
                initialColor = Color.parseColor(currentColorHex);
            } catch (IllegalArgumentException e) {
            }
        }

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                categoryViewModel.setColour(hexColor);
            }
        });
        dialog.show();
    }
}
