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
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.UserTest;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<RecurringTask> {

    public TaskAdapter(Context context, List<RecurringTask> taskSlots) {
        super(context, 0, taskSlots);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RecurringTask recurringTask = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        TextView taskName = convertView.findViewById(R.id.task_slot_name);
        TextView taskTime = convertView.findViewById(R.id.task_slot_time);

        if (recurringTask != null) {
            taskName.setText(recurringTask.getName());
            taskTime.setText(recurringTask.getExecutionTime().toString());

            View taskContainer = convertView.findViewById(R.id.task_container); // Pretpostavimo da imate taj ID


            String taskColor = recurringTask.getCategoryColour(); // Ovo je samo primer

            try {
                int color = Color.parseColor(taskColor);

                convertView.setBackgroundColor(color);

            } catch (IllegalArgumentException e) {
                convertView.setBackgroundColor(Color.GRAY);
            }
        }

        return convertView;
    }
}
