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
import com.example.myhobitapplication.models.UserTest;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<UserTest> {
    public UserListAdapter(Context context, List<UserTest> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserTest user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        }

        TextView userNameTextView = convertView.findViewById(R.id.tv_user_name);
        TextView userIdTextView = convertView.findViewById(R.id.tv_user_id);

        if (user != null) {
            userNameTextView.setText("Ime: " + user.getName() + " " + user.getSurname());
            userIdTextView.setText("ID: " + user.getId());
        }

        return convertView;
    }
}
