package com.example.myhobitapplication.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.TaskDetailActivity;
import com.example.myhobitapplication.adapters.TaskAdapter;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Task;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.TaskCalendarViewModel;
import com.example.myhobitapplication.viewModels.TaskCalendarViewModelFactory;

import java.time.LocalDate;
import java.util.List;

public class TaskSlotCalendarFragment extends Fragment {

    private static final String ARG_DATE = "selected_date";
    private TaskRepository repository;

    CategoryService categoryService;
    CategoryRepository categoryRepository;


    private TaskAdapter adapter;
    private List<Task> tasks;


    private TaskCalendarViewModel taskCalendarViewModel;

//    public static TaskSlotCalendarFragment newInstance(LocalDate date) {
//        TaskSlotCalendarFragment fragment = new TaskSlotCalendarFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_DATE, date);
//        fragment.setArguments(args);
//        return fragment;
//    }

    // REGISTRUJ LAUNCHER za detalje zadatka
    private final ActivityResultLauncher<Intent> taskDetailsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Ovaj kod se izvršava kada se vratimo iz TaskDetailActivity
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Stigao je signal da su se podaci promenili!
                    Toast.makeText(getContext(), "Lista se osvjezavaaa nakon edita...", Toast.LENGTH_SHORT).show();

                    // Pokreni mehanizam osvežavanja koji već imaš
                    taskCalendarViewModel.refreshScheduledTasks();
                    LocalDate currentDate = taskCalendarViewModel.getSelectedDate().getValue();
                    if (currentDate != null) {
                        taskCalendarViewModel.selectDate(currentDate);
                    }
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new TaskRepository(getContext());
        ProfileService profileService =  ProfileService.getInstance();
        BossRepository bossRepository = new BossRepository(getContext());
        BossService bossService = new BossService(bossRepository);
        BattleService battleService = new BattleService(bossService, profileService);

        TaskService taskService = new TaskService(repository, profileService,battleService);

        categoryRepository = new CategoryRepository(getContext());
        categoryService = new CategoryService(categoryRepository,repository);



        TaskCalendarViewModelFactory factory = new TaskCalendarViewModelFactory(taskService);


        taskCalendarViewModel = new ViewModelProvider(requireActivity(), factory).get(TaskCalendarViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_slot_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        LocalDate selectedDate = null;
//        if (getArguments() != null) {
//            selectedDate = (LocalDate) getArguments().getSerializable(ARG_DATE);
//        }

        ListView taskListView = view.findViewById(R.id.task_list_view);

        tasks = new java.util.ArrayList<>();
        adapter = new TaskAdapter(getContext(), tasks);
        taskListView.setAdapter(adapter);


        taskListView.setOnItemClickListener((parent, view1, position, id) -> {

            Task selectedTask = tasks.get(position);

            if (selectedTask == null) {
                return;
            }



            if (selectedTask instanceof RecurringTask) {

                RecurringTask recurringTask = (RecurringTask) selectedTask;

                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);

                intent.putExtra("TASK_ID_EXTRA", recurringTask.getId());
                intent.putExtra("TASK_TYPE_EXTRA", "RECURRING");

                taskDetailsLauncher.launch(intent);
                Toast.makeText(getContext(), "Recurring task opening...", Toast.LENGTH_SHORT).show();

            } else if (selectedTask instanceof OneTimeTask) {

                OneTimeTask oneTimeTask = (OneTimeTask) selectedTask;

                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);

                intent.putExtra("TASK_ID_EXTRA", oneTimeTask.getId());
                intent.putExtra("TASK_TYPE_EXTRA", "ONE_TIME");

                taskDetailsLauncher.launch(intent);

                Toast.makeText(getContext(), "One-Time task opening...", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "UNKNOWN TASK TYPE", Toast.LENGTH_SHORT).show();
            }
        });






        getParentFragmentManager().setFragmentResultListener("taskAddedRequest", getViewLifecycleOwner(), (requestKey, bundle) -> {


            taskCalendarViewModel.refreshScheduledTasks();

            // 3. NATERAJ POSTOJEĆI OBSERVER DA SE PONOVO AKTIVIRA!
            //    Kako? Tako što ćemo ponovo "gurnuti" poslednju poznatu vrednost datuma u LiveData.
            //    Ovo će pokrenuti onChanged metodu i osvežiti listu.
            LocalDate currentDate = taskCalendarViewModel.getSelectedDate().getValue();
            if (currentDate != null) {
                taskCalendarViewModel.selectDate(currentDate); // Trik: ponovo postavi istu vrednost!
            }
        });





        taskCalendarViewModel.getSelectedDate().observe(getViewLifecycleOwner(), new Observer<LocalDate>() {
            @Override
            public void onChanged(LocalDate selectedDate) {
                // Ova metoda će se automatski pozvati svaki put kada se datum u ViewModelu promeni.
                if (selectedDate != null) {
                    // 1. Dobavi nove zadatke za taj datum
                    List<Task> newTasks = taskCalendarViewModel.getTasksForDate(selectedDate);

                    // 2. Očisti staru listu i dodaj nove podatke
                    tasks.clear();
                    tasks.addAll(newTasks);

                    // 3. Obavesti adapter da su se podaci promenili kako bi osvežio prikaz
                    adapter.notifyDataSetChanged();

                }
            }
        });


//        taskListView.setOnItemClickListener((parent, view1, position, id) -> {
//               RecurringTask selectedTask = (RecurringTask) parent.getItemAtPosition(position);
//
//               if (selectedTask != null) {
//
//                   int taskId = selectedTask.getId();
//                   Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
//
//                   intent.putExtra("TASK_ID_EXTRA", taskId);
//
//
//                   startActivity(intent);
//               }
//            });



    }




//        if (selectedDate != null) {
//
//            TextView dateTextView = view.findViewById(R.id.task_slot_time);
//
//            List<RecurringTask> tasks = taskCalendarViewModel.getTasksForDate(selectedDate);
//
//            ListView taskListView = view.findViewById(R.id.task_list_view);
//            TaskAdapter adapter = new TaskAdapter(getContext(), tasks);
//            taskListView.setAdapter(adapter);
//
//            taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    RecurringTask selectedTask = tasks.get(position);
//
//
//                    if (listener != null) {
//                        listener.onTaskSelected(selectedTask.getId());
//                    }
//                }
//            });
//
////            taskListView.setOnItemClickListener((parent, view1, position, id) -> {
////                RecurringTask selectedTask = (RecurringTask) parent.getItemAtPosition(position);
////
////                if (selectedTask != null) {
////
////                    long taskId = selectedTask.getId();
////                    //String categoryColour = categoryService.getCategoryById(selectedTask.getCategoryId()).getColour();
////                    Bundle bundle = new Bundle();
////                    bundle.putLong("taskId", taskId);
////                   // bundle.putString("taskColour", categoryColour);
////
////                    TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();
////                    taskDetailsFragment.setArguments(bundle);
////
////                    getParentFragmentManager().beginTransaction()
////                            .replace(R.id.fragment_container, taskDetailsFragment)
////                            .addToBackStack(null)
////                            .commit();
////                }
////            });
//        }



    }

//    public interface OnTaskSelectedListener {
//        void onTaskSelected(int taskId);
//    }

//    OnTaskSelectedListener listener;
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof OnTaskSelectedListener) {
//            listener = (OnTaskSelectedListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " mora implementirati OnTaskSelectedListener");
//        }
//    }


