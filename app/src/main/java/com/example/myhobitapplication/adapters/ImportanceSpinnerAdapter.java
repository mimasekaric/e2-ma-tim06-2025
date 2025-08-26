package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myhobitapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportanceSpinnerAdapter extends ArrayAdapter<Integer> {

    private final Context context;


    public ImportanceSpinnerAdapter(Context context) {
        super(context, 0);
        this.context = context;
        List<Integer> importance = new ArrayList<>(Arrays.asList(1, 3, 10, 100));
        this.addAll(importance);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }


    private View createCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_importance, parent, false);
        }

        Integer currentDifficulty = getItem(position);
        TextView difficultyTextView = convertView.findViewById(R.id.importance_text_view);


        if (currentDifficulty != null) {

            difficultyTextView.setText(String.valueOf(currentDifficulty));
        }

        return convertView;
    }

    public int getPosition(Integer value) {
        if (value == null) {
            return -1;
        }
        for (int i = 0; i < getCount(); i++) {
            if (value.equals(getItem(i))) {
                return i;
            }
        }
        return -1;
    }

}
