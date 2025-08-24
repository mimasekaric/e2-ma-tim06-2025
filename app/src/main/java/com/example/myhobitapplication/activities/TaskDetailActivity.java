package com.example.myhobitapplication.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.fragments.TaskDetailsFragment;
import com.example.myhobitapplication.fragments.TaskSlotCalendarFragment;

import java.time.LocalDate;

public class TaskDetailActivity extends AppCompatActivity
        implements TaskSlotCalendarFragment.OnTaskSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        LocalDate selectedDate = (LocalDate) getIntent().getSerializableExtra("selected_date");

        if (selectedDate != null) {
            TaskSlotCalendarFragment fragment = TaskSlotCalendarFragment.newInstance(selectedDate);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onTaskSelected(int taskId) {

        TaskDetailsFragment taskFragment = TaskDetailsFragment.newInstance(taskId);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.task_detail_container, taskFragment)
                .addToBackStack(null) // Dodavanje u "back stack" za povratak pritiskom na dugme
                .commit();
    }
}