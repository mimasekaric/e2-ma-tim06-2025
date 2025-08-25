package com.example.myhobitapplication.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.fragments.TaskDetailsFragment;
import com.example.myhobitapplication.fragments.TaskSlotCalendarFragment;

import java.time.LocalDate;

public class TaskDetailActivity extends AppCompatActivity
        {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        int taskId = getIntent().getIntExtra("TASK_ID_EXTRA", -1);

        if (savedInstanceState == null && taskId != -1L) {

            // 3. Kreiraj instancu TaskDetailsFragment-a koristeći ID.
            //    Ovo pretpostavlja da TaskDetailsFragment ima newInstance metodu.
            TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance(taskId);

            // 4. Prikaži fragment unutar kontejnera ove aktivnosti.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_detail_container, taskDetailsFragment) // task_detail_container je ID FrameLayout-a u tvom XML-u
                    .commit();
        }
    }

//    @Override
//    public void onTaskSelected(int taskId) {
//
//        TaskDetailsFragment taskFragment = TaskDetailsFragment.newInstance(taskId);
//
//
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.task_detail_container, taskFragment)
//                .addToBackStack(null) // Dodavanje u "back stack" za povratak pritiskom na dugme
//                .commit();
//    }
}