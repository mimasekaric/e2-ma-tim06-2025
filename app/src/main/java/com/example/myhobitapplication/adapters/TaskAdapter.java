package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Task;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private final Typeface cursiveFont;

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
        cursiveFont = ResourcesCompat.getFont(context, R.font.educursivesemibold);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        TextView taskName = convertView.findViewById(R.id.task_slot_name);
        TextView taskTime = convertView.findViewById(R.id.task_slot_time);

        if (task != null) {

            taskName.setText(task.getName());

            taskName.setTypeface(cursiveFont);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            taskTime.setText(task.getExecutionTime().format(timeFormatter));

            taskTime.setTextColor(Color.parseColor("#FFD700"));

            taskTime.setTypeface(null, Typeface.BOLD);

            try {
                int color = Color.parseColor(task.getCategoryColour());
                convertView.setBackgroundColor(color);
            } catch (Exception e) {
                convertView.setBackgroundColor(Color.DKGRAY);
            }
        }

        return convertView;
    }
}
