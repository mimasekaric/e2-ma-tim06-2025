package com.example.myhobitapplication.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.fragments.RecurringTaskDetailsFragment;
import com.example.myhobitapplication.fragments.RecurringTaskEditFragment;

public class RecurringTaskEditActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int taskId = getIntent().getIntExtra("TASK_ID_TO_EDIT", -1);
        setContentView(R.layout.activity_recurring_task_edit);

        if (savedInstanceState == null && taskId != -1) {

            RecurringTaskEditFragment recurringTaskEditFragment = RecurringTaskEditFragment.newInstance(taskId);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rctask_edit_container, recurringTaskEditFragment)
                    .commit();
        }




    }
}
