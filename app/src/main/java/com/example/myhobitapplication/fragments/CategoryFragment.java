package com.example.myhobitapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databinding.FragmentCategoryBinding;
import com.example.myhobitapplication.databinding.FragmentRecurringTaskBinding;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoryFragment extends Fragment {

    private int selectedColor = Color.BLACK;

    private FragmentCategoryBinding categoryBinding;

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

        categoryBinding.pickColorButton.setOnClickListener(v -> openColorPickerDialog());

        categoryBinding.setColorButton.setOnClickListener(v -> {
            // Primer kako mozes da primenis odabranu boju, npr. na TextView
            categoryBinding.categoryNameTextView.setTextColor(selectedColor);
        });
    }

    private void openColorPickerDialog() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(requireContext(), selectedColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // Boja je odabrana, ažuriraj boju i prikaži je u preview-u
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
