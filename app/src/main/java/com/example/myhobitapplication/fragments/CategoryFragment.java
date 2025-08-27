package com.example.myhobitapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.example.myhobitapplication.CloudStoreUtil;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databinding.FragmentCategoryBinding;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.viewModels.CategoryViewModel;


import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoryFragment extends Fragment {

    private int selectedColor = Color.BLACK;

    private FragmentCategoryBinding categoryBinding;
    private CategoryRepository repository;

    private CategoryViewModel categoryViewModel;

    @Override
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryBinding.selectedColorPreview.setBackgroundColor(selectedColor);

        categoryBinding.pickColorButton.setOnClickListener(v -> {

            openColorPickerDialog();
           } );

        categoryBinding.setColorButton.setOnClickListener(v -> {

            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
            categoryViewModel.setColour(hexColor);

        });

        categoryBinding.ctgName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryViewModel.setName(s.toString());
            }
        });

        categoryBinding.btnAddCategory.setOnClickListener(v -> {


            categoryViewModel.saveCategory();
            categoryViewModel.inDB();


            Toast.makeText(requireContext(), "Kategorija je uspešno kreirana!", Toast.LENGTH_SHORT).show();
        });


    }
    private void openColorPickerDialog() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(requireContext(), selectedColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                selectedColor = color;
                categoryBinding.selectedColorPreview.setBackgroundColor(selectedColor);
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        categoryBinding = null;
    }
    }



