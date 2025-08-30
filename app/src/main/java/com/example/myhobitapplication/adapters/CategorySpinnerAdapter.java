package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.models.Category;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private final Context context;
    private final List<Category> categories;

    public CategorySpinnerAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_category, parent, false);
        }

        Category currentCategory = categories.get(position);

        View colorView = convertView.findViewById(R.id.categoryColorView);
        TextView nameTextView = convertView.findViewById(R.id.categoryNameTextView);

        if (currentCategory != null) {

            try {
                colorView.setBackgroundColor(Color.parseColor(currentCategory.getColour()));
            } catch (IllegalArgumentException e) {

                colorView.setBackgroundColor(Color.BLACK);
            }
            nameTextView.setText(currentCategory.getName());
        }

        return convertView;
    }


}
