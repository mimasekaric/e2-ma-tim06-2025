package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.models.Avatar;

import java.util.List;

public class AvatarSpinnerAdapter extends ArrayAdapter<Avatar> {

    private Context context;
    private List<Avatar> avatarList;
    public AvatarSpinnerAdapter(Context context, List<Avatar> avatars){
        super(context, 0, avatars);
        this.context = context;
        this.avatarList = avatars;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }


    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_avatar, parent, false
            );
        }

        ImageView imageViewAvatar = convertView.findViewById(R.id.imageView);
        Avatar currentItem = getItem(position);
        if (currentItem != null) {
            imageViewAvatar.setImageResource(currentItem.getImage());
        }

        return convertView;
    }



}
