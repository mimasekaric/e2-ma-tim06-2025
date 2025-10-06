package com.example.myhobitapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.CategoryStatsDTO;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.StatisticsViewModel;
import com.example.myhobitapplication.viewModels.UserEquipmentViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private PieChart tasksPieChart;
    private BarChart categoryBarChart;
    private LineChart xpLineChart;
    private LineChart difficultyLineChart;
    private TextView activeDays;
    private TextView longestStreakText;
    private TextView totalTasks;
    private TextView specialMissions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        ProfileService profileService = ProfileService.getInstance();
        TaskService taskService = TaskService.getInstance(new TaskRepository(requireContext()),profileService,new BattleService(new BossService(new BossRepository(requireContext())),profileService), new AllianceMissionService(profileService));
        statisticsViewModel =  new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new StatisticsViewModel(taskService,new CategoryService(new CategoryRepository(requireContext()),new TaskRepository(requireContext())), new AllianceMissionService(profileService));
            }
        }).get(StatisticsViewModel.class);
        statisticsViewModel.loadData(FirebaseAuth.getInstance().getCurrentUser().getUid());

        activeDays = view.findViewById(R.id.activeDays);
        longestStreakText = view.findViewById(R.id.longestStreak);
        totalTasks = view.findViewById(R.id.totalTasks);
        specialMissions = view.findViewById(R.id.specialMissions);


        statisticsViewModel.getLongestActiveStr().observe(getViewLifecycleOwner(),longestStreak->{
            activeDays.setText("Active app usage streak: " + longestStreak +  " days");
        });

        statisticsViewModel.getLongestSuccessStr().observe(getViewLifecycleOwner(),longestStreak->{
            longestStreakText.setText("Longest streak of success: " + longestStreak +  " days");
        });
        statisticsViewModel.getTasksCreated().observe(getViewLifecycleOwner(),tasksCreated->{
            totalTasks.setText("Total tasks created: " + tasksCreated);
        });
        tasksPieChart = view.findViewById(R.id.tasksPieChart);
        categoryBarChart = view.findViewById(R.id.categoryBarChart);
        xpLineChart = view.findViewById(R.id.xpLineChart);
        difficultyLineChart = view.findViewById(R.id.difficultyLineChart);

        statisticsViewModel.specialMissionsStats.observe(getViewLifecycleOwner(), stats -> {
            int started = stats[0];
            int completed = stats[1];
            specialMissions.setText("Special missions: " + started + " started / " + completed + " completed");
        });


        setupPieChart();
        setupBarChart();
        setupXPLineChart();
        setupDifficultyLineChart();



        return view;
    }

    private void setupPieChart() {
        statisticsViewModel.getTaskStatusCounts().observe(getViewLifecycleOwner(), counts -> {
            if (counts == null || counts.isEmpty()) return;

            ArrayList<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(counts.getOrDefault("Finished", 0), "Done"));
            entries.add(new PieEntry(counts.getOrDefault("NotDone", 0), "Not done"));
            entries.add(new PieEntry(counts.getOrDefault("Canceled", 0), "Canceled"));

            PieDataSet dataSet = new PieDataSet(entries, "Tasks");
            dataSet.setColors(
                    Color.parseColor("#00FF7F"),   // Done
                    Color.parseColor("#B4C424"),   // Not done
                    Color.parseColor("#9FE2BF")    // Canceled
            );
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(12f);

            PieData data = new PieData(dataSet);

            // umesto procenta, prikazujemo broj
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getPieLabel(float value, PieEntry pieEntry) {
                    return String.valueOf((int) value); // samo broj
                }
            });

            tasksPieChart.setData(data);
            tasksPieChart.setUsePercentValues(false); // iskljuci procente

            // donut stil
            tasksPieChart.setDrawHoleEnabled(true);
            tasksPieChart.setHoleRadius(40f);
            tasksPieChart.setTransparentCircleRadius(45f);

            tasksPieChart.invalidate();
        });
    }



    private void setupBarChart() {
        statisticsViewModel.getCompletedTasksByCategory().observe(getViewLifecycleOwner(), stats -> {
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();

            int index = 0;
            for (CategoryStatsDTO stat : stats) {
                entries.add(new BarEntry(index, stat.getCount()));
                colors.add(Color.parseColor(stat.getColour())); // uzimamo boju kategorije
                labels.add(stat.getName());
                index++;
            }

            BarDataSet dataSet = new BarDataSet(entries, "Tasks by category");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.WHITE);

            BarData data = new BarData(dataSet);
            categoryBarChart.setData(data);


            XAxis xAxis = categoryBarChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

            categoryBarChart.invalidate();
        });
    }


    private void setupXPLineChart() {
        statisticsViewModel.xpLast7Days.observe(getViewLifecycleOwner(), xpMap -> {
            ArrayList<Entry> entries = new ArrayList<>();
            List<LocalDate> sortedDates = xpMap.keySet().stream().sorted().collect(Collectors.toList());
            for (int i = 0; i < sortedDates.size(); i++) {
                LocalDate date = sortedDates.get(i);
                entries.add(new Entry(i + 1, xpMap.get(date))); // i+1 da zadržiš 1-7
            }

            LineDataSet dataSet = new LineDataSet(entries, "XP last 7 days");
            dataSet.setColor(Color.GREEN);
            dataSet.setCircleColor(Color.WHITE);

            LineData data = new LineData(dataSet);
            xpLineChart.setData(data);
            xpLineChart.invalidate();
        });

    }

    private void setupDifficultyLineChart() {
        statisticsViewModel.getAvgDifficultyCompletedTasks().observe(getViewLifecycleOwner(), avgList -> {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < avgList.size(); i++) {
                entries.add(new Entry(i + 1, avgList.get(i)));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Average XP of completed tasks");
            dataSet.setColor(Color.parseColor("#00FF7F"));
            dataSet.setCircleColor(Color.WHITE);

            LineData data = new LineData(dataSet);
            difficultyLineChart.setData(data);
            difficultyLineChart.invalidate();
        });

    }

}

