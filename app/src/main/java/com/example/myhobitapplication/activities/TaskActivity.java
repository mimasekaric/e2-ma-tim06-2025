package com.example.myhobitapplication.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.adapters.TaskPagerAdapter;
import com.example.myhobitapplication.workers.TaskWorker;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.TimeUnit;

public class TaskActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        scheduleDailyTaskStatusUpdate();

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);

        TaskPagerAdapter pagerAdapter = new TaskPagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("OneTimeTask");
                    } else if(position ==1) {
                        tab.setText("RecurringTask");
                    }
                    else {
                        tab.setText("Calendar");
                    }
//                    else
//                    {
//                        tab.setText("Category");
//                    }
                }).attach();



    }


    private void scheduleDailyTaskStatusUpdate() {
        // Kreiraj zahtev koji će se ponavljati
        PeriodicWorkRequest updateRequest =
                new PeriodicWorkRequest.Builder(TaskWorker.class, 24, TimeUnit.HOURS)
                        // Možeš dodati i uslove, npr. da radi samo kad je mreža dostupna
                        // .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                        .build();

        // Zakaži posao. Koristi ENQUEUE_UNIQUE_PERIODIC_WORK da sprečiš
        // dupliranje posla svaki put kad se aplikacija pokrene.
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "updateTaskStatusWork", // Jedinstveno ime za ovaj posao
                ExistingPeriodicWorkPolicy.KEEP, // Ako posao već postoji, ne radi ništa
                updateRequest
        );
    }
}
