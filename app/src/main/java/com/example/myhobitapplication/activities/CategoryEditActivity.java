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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.ActivityCategoryEditBinding;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.viewModels.categoryViewModels.CategoryEditViewModel;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoryEditActivity extends AppCompatActivity {


    private int selectedColor = Color.BLACK;

    private ActivityCategoryEditBinding binding;

    private CategoryEditViewModel categoryEditViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int categoryId = getIntent().getIntExtra("CATEGORY_ID_EXTRA", -1);

        CategoryRepository repository = new CategoryRepository(this);
        TaskRepository taskRepository = new TaskRepository(this);
        CategoryService categoryService = new CategoryService(repository,taskRepository);


        categoryEditViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CategoryEditViewModel(categoryService);
            }
        }).get(CategoryEditViewModel.class);

        if (categoryId != -1) {
            categoryEditViewModel.loadCategoryDetails(categoryId);
        } else {
            Toast.makeText(this, "Greška: Kategorija nije pronađena.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        categoryEditViewModel.getCategoryDetails().observe(this, categoryDto -> {
            if (categoryDto != null) {
                binding.ctgName.setText(categoryDto.getName());
                try {
                    int color = Color.parseColor(categoryDto.getColour());
                    binding.selectedColorPreview.setBackgroundColor(color);
                } catch (IllegalArgumentException e) {
                    binding.selectedColorPreview.setBackgroundColor(Color.BLACK);
                }
            }
        });

        categoryEditViewModel.getColour().observe(this, hexColor -> {
            if (hexColor != null) {
                updateColorPreview(hexColor);
            }
        });


        binding.pickColorButton.setOnClickListener(v -> {
            openColorPickerDialog();
        });





        binding.ctgName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryEditViewModel.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        binding.btnAddCategory.setOnClickListener(v -> {
            categoryEditViewModel.updateCategory();
        });

        categoryEditViewModel.getSubmissionError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Error saving")
                        .setMessage(error)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        categoryEditViewModel.getSaveSuccessEvent().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {

                Toast.makeText(this, "Category successfully saved!", Toast.LENGTH_SHORT).show();

                setResult(Activity.RESULT_OK);

                finish();

                categoryEditViewModel.onSaveSuccessEventHandled();
            }
        });categoryEditViewModel.getSaveSuccessEvent().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {

                Toast.makeText(this, "Kategorija je uspešno ažurirana!", Toast.LENGTH_SHORT).show();

                setResult(Activity.RESULT_OK);

                finish();

                categoryEditViewModel.onSaveSuccessEventHandled();
            }
        });
    }

    private void openColorPickerDialog() {
        String currentColorHex = categoryEditViewModel.getColour().getValue();
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
                categoryEditViewModel.setColour(hexColor);
                binding.selectedColorPreview.setBackgroundColor(color);
            }
        });
        dialog.show();
    }

    private void updateColorPreview(String hexColor) {
        try {
            int color = Color.parseColor(hexColor);
            binding.selectedColorPreview.setBackgroundColor(color);
        } catch (Exception e) {
            binding.selectedColorPreview.setBackgroundColor(Color.BLACK);
        }
    }
}
