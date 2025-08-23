package com.example.myhobitapplication.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.fragments.TaskSlotCalendarFragment;

import java.time.LocalDate;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.myhobitapplication.R.layout.activity_task_detail);

        LocalDate selectedDate = (LocalDate) getIntent().getSerializableExtra("selected_date");

        if (selectedDate != null) {

            TaskSlotCalendarFragment fragment = TaskSlotCalendarFragment.newInstance(selectedDate);


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_detail_container, fragment)
                    .commit();
        }
    }

}
