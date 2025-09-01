package com.example.myhobitapplication.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.fragments.tasksFragments.OneTimeTaskDetailsFragment;
import com.example.myhobitapplication.fragments.tasksFragments.OneTimeTaskEditFragment;
import com.example.myhobitapplication.fragments.tasksFragments.RecurringTaskDetailsFragment;
import com.example.myhobitapplication.fragments.tasksFragments.RecurringTaskEditFragment;

public class TaskEditActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_recurring_task_edit);
        int taskId = getIntent().getIntExtra("TASK_ID_TO_EDIT", -1);
        String taskType = getIntent().getStringExtra("TASK_TYPE_EXTRA");

        if (savedInstanceState == null && taskId != -1 && taskType != null) {

            Fragment fragmentToLoad;

            if ("RECURRING".equals(taskType)) {

                fragmentToLoad = RecurringTaskEditFragment.newInstance(taskId);

            } else {

                fragmentToLoad = OneTimeTaskEditFragment.newInstance(taskId);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rctask_edit_container, fragmentToLoad)
                    .commit();
        }




    }
}
