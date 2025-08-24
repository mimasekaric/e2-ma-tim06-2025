package com.example.myhobitapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.adapters.TaskAdapter;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.TaskCalendarViewModel;

import java.time.LocalDate;
import java.util.List;

public class TaskSlotCalendarFragment extends Fragment {

    private static final String ARG_DATE = "selected_date";
    private TaskRepository repository;

    CategoryService categoryService;
    CategoryRepository categoryRepository;



    private TaskCalendarViewModel taskCalendarViewModel;

    public static TaskSlotCalendarFragment newInstance(LocalDate date) {
        TaskSlotCalendarFragment fragment = new TaskSlotCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new TaskRepository(getContext());

        TaskService taskService = new TaskService(repository);
        categoryRepository = new CategoryRepository(getContext());
        categoryService = new CategoryService(categoryRepository);



        taskCalendarViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new TaskCalendarViewModel(taskService);
            }
        }).get(TaskCalendarViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_slot_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalDate selectedDate = null;
        if (getArguments() != null) {
            selectedDate = (LocalDate) getArguments().getSerializable(ARG_DATE);
        }

        if (selectedDate != null) {

            TextView dateTextView = view.findViewById(R.id.task_slot_time);

            List<RecurringTask> tasks = taskCalendarViewModel.getTasksForDate(selectedDate);

            ListView taskListView = view.findViewById(R.id.task_list_view);
            TaskAdapter adapter = new TaskAdapter(getContext(), tasks);
            taskListView.setAdapter(adapter);

            taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    RecurringTask selectedTask = tasks.get(position);


                    if (listener != null) {
                        listener.onTaskSelected(selectedTask.getId());
                    }
                }
            });

//            taskListView.setOnItemClickListener((parent, view1, position, id) -> {
//                RecurringTask selectedTask = (RecurringTask) parent.getItemAtPosition(position);
//
//                if (selectedTask != null) {
//
//                    long taskId = selectedTask.getId();
//                    //String categoryColour = categoryService.getCategoryById(selectedTask.getCategoryId()).getColour();
//                    Bundle bundle = new Bundle();
//                    bundle.putLong("taskId", taskId);
//                   // bundle.putString("taskColour", categoryColour);
//
//                    TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();
//                    taskDetailsFragment.setArguments(bundle);
//
//                    getParentFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, taskDetailsFragment)
//                            .addToBackStack(null)
//                            .commit();
//                }
//            });
        }



    }

    public interface OnTaskSelectedListener {
        void onTaskSelected(int taskId);
    }

    OnTaskSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskSelectedListener) {
            listener = (OnTaskSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " mora implementirati OnTaskSelectedListener");
        }
    }

}
