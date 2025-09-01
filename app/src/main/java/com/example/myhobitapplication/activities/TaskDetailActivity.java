package com.example.myhobitapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.fragments.tasksFragments.OneTimeTaskDetailsFragment;
import com.example.myhobitapplication.fragments.tasksFragments.RecurringTaskDetailsFragment;

public class TaskDetailActivity extends AppCompatActivity
        {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        int taskId = getIntent().getIntExtra("TASK_ID_EXTRA", -1);
        String taskType = getIntent().getStringExtra("TASK_TYPE_EXTRA");

        if (savedInstanceState == null && taskId != -1 && taskType != null) {

            Fragment fragmentToLoad;

            if ("RECURRING".equals(taskType)) {

                fragmentToLoad = RecurringTaskDetailsFragment.newInstance(taskId);

            } else {

                fragmentToLoad = OneTimeTaskDetailsFragment.newInstance(taskId);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_detail_container, fragmentToLoad)
                    .commit();
        }
    }

}