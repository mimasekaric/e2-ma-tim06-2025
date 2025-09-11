package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.myhobitapplication.R;
import com.example.myhobitapplication.dto.CategoryDTO;


import java.util.List;

public class CategoryListAdapter extends BaseAdapter {


    private final Context context;
    private List<CategoryDTO> categories;
    private final LayoutInflater inflater;

    public CategoryListAdapter(Context context, List<CategoryDTO> categories) {
        this.context = context;
        this.categories = categories;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return categories.size();
    }


    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<CategoryDTO> newCategories) {
        this.categories = newCategories;
        this.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item_category, parent, false);

            holder = new ViewHolder();
            holder.colorView = convertView.findViewById(R.id.categoryColorView);
            holder.nameTextView = convertView.findViewById(R.id.categoryNameTextView);

            // --- NOVI DEO: Pronađi i korenski element reda ---
            // Treba nam referenca na ceo red da bismo mu promenili pozadinu
            holder.rootLayout = convertView; // Pretpostavka da je koren LinearLayout ili slično

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CategoryDTO currentCategoryDto = (CategoryDTO) getItem(position);

        if (currentCategoryDto != null) {

            try {

                int categoryColor = Color.parseColor(currentCategoryDto.getColour());


                holder.colorView.setBackgroundColor(categoryColor);


                holder.rootLayout.setBackgroundColor(categoryColor);

                if (isColorDark(categoryColor)) {
                    holder.nameTextView.setTextColor(Color.WHITE);
                } else {
                    holder.nameTextView.setTextColor(Color.BLACK);
                }

            } catch (IllegalArgumentException e) {

                holder.colorView.setBackgroundColor(Color.BLACK);
                holder.rootLayout.setBackgroundColor(Color.LTGRAY);
                holder.nameTextView.setTextColor(Color.BLACK);
            }

            holder.nameTextView.setText(currentCategoryDto.getName());
        }

        return convertView;
    }

    private static class ViewHolder {
        View colorView;
        TextView nameTextView;
        View rootLayout;
    }

    private boolean isColorDark(int color){

        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }






}
