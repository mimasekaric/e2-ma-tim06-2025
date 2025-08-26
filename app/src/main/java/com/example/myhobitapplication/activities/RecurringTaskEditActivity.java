package com.example.myhobitapplication.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RecurringTaskEditActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int taskId = getIntent().getIntExtra("TASK_ID_TO_EDIT", -1);





    }
}
