package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.CategoryStatsDTO;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsViewModel extends ViewModel {

    public MutableLiveData<Integer> longestActiveStr = new MutableLiveData<>(0);
    public MutableLiveData<Integer> longestSuccessStr = new MutableLiveData<>(0);
    public MutableLiveData<int[]> specialMissionsStats = new MutableLiveData<>(new int[]{0, 0});

    public MutableLiveData<Integer> tasksCreated = new MutableLiveData<>(0);
    public MutableLiveData<List<Float>> avgDifficultyCompletedTasks = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<CategoryStatsDTO>> completedTasksByCategory = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Map<String, Integer>> taskStatusCounts = new MutableLiveData<>(new HashMap<>());

    public MutableLiveData<Map<LocalDate, Integer>> xpLast7Days = new MutableLiveData<>(new HashMap<>());

    public void loadXpLast7Days(String userUid) {
        xpLast7Days.setValue(taskService.getXpLast7Days(userUid));
    }
    private  TaskService taskService;
   private CategoryService categoryService;
    private  AllianceMissionService allianceMissionService;
    public StatisticsViewModel(TaskService taskService, CategoryService categoryService, AllianceMissionService allianceMissionService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.allianceMissionService = allianceMissionService;
    }

    public MutableLiveData<int[]> getSpecialMissionsStats() {
        return specialMissionsStats;
    }

    public MutableLiveData<List<Float>> getAvgDifficultyCompletedTasks() {
        return avgDifficultyCompletedTasks;
    }

    public MutableLiveData<Integer> getTasksCreated() {
        return tasksCreated;
    }

    public MutableLiveData<Integer> getLongestActiveStr() {
        return longestActiveStr;
    }
    public MutableLiveData<List<CategoryStatsDTO>> getCompletedTasksByCategory() {
        return completedTasksByCategory;
    }
    public MutableLiveData<Integer> getLongestSuccessStr() {
        return longestSuccessStr;
    }
    public MutableLiveData<Map<String, Integer>> getTaskStatusCounts() {
        return taskStatusCounts;
    }

    public void loadData(String userUid){
        longestActiveStr.setValue(taskService.getLongestActiveStreak(userUid));
        longestSuccessStr.setValue(taskService.calculateLongestSuccessfulStreak(userUid));

        Map<String, Integer> completedByColour = taskService.getCompletedTasksByCategory(userUid);
        List<CategoryStatsDTO> stats = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : completedByColour.entrySet()) {
            String colour = entry.getKey();
            String name = categoryService.getByColour(colour).getName();
            stats.add(new CategoryStatsDTO(name, colour, entry.getValue()));
        }

        completedTasksByCategory.setValue(stats);
        taskStatusCounts.setValue(taskService.getTaskStatusCounts(userUid));
        tasksCreated.setValue(taskService.getAllTasks(userUid).size());

        avgDifficultyCompletedTasks.setValue(taskService.getAverageXpOfCompletedTasks(userUid));

        loadXpLast7Days(userUid);

        allianceMissionService.getStartedAndCompletedMissions(userUid)
                .addOnSuccessListener(stats2 -> specialMissionsStats.setValue(stats2))
                .addOnFailureListener(e -> {
                    specialMissionsStats.setValue(new int[]{0, 0});
                });

    }

}
