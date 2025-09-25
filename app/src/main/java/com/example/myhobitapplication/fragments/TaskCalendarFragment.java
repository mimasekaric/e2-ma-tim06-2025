package com.example.myhobitapplication.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.TaskDetailActivity;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentTaskCalendarBinding;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.TaskCalendarViewModel;
import com.example.myhobitapplication.viewModels.TaskCalendarViewModelFactory;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.core.OutDateStyle;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;


public class TaskCalendarFragment extends Fragment {

    private TaskCalendarViewModel calendarViewModel;

    private FragmentTaskCalendarBinding calendarBinding;

    private CalendarDay selectedDate = null;
    private CalendarView calendarView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calendarBinding = FragmentTaskCalendarBinding.inflate(inflater, container, false);
        return calendarBinding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = calendarBinding.calendarView;



        TaskRepository repository = new TaskRepository(getContext());
        ProfileService profileService =  ProfileService.getInstance();
        BossRepository bossRepository = new BossRepository(getContext());
        BossService bossService = new BossService(bossRepository);
        BattleService battleService = new BattleService(bossService, profileService);
        TaskService taskService = new TaskService(repository, profileService, battleService);
        TextView monthTextView = view.findViewById(R.id.calendarMonthText);
        ImageView prevButton = view.findViewById(R.id.previousMonthButton);
        ImageView nextButton = view.findViewById(R.id.nextMonthButton);

        TaskCalendarViewModelFactory factory = new TaskCalendarViewModelFactory(taskService);


        calendarViewModel = new ViewModelProvider(requireActivity(), factory).get(TaskCalendarViewModel.class);

//        getParentFragmentManager().setFragmentResultListener("taskAddedRequest", getViewLifecycleOwner(), (requestKey, bundle) -> {
//
//            // Signal je primljen!
//            Toast.makeText(getContext(), "Kalendar se osvežava...", Toast.LENGTH_SHORT).show();
//
//            // 1. Naredi ViewModel-u da osveži svoje interne podatke iz baze.
//            //    Ovo je važno da oba fragmenta rade sa istim, svežim podacima.
//            calendarViewModel.refreshScheduledTasks();
//
//            // 2. Naredi UI-ju (samom kalendaru) da se ponovo iscrta.
//            //    Ovo će naterati biblioteku da ponovo pozove `bind` metodu za sve vidljive dane.
//            if (calendarView != null) {
//                calendarView.notifyCalendarChanged();
//            }
//        });

        requireActivity().getSupportFragmentManager().setFragmentResultListener("taskAddedRequest", getViewLifecycleOwner(), (requestKey, bundle) -> {
            Toast.makeText(getContext(), "Kalendar je primio signal!", Toast.LENGTH_SHORT).show();
            calendarViewModel.refreshScheduledTasks();
            if (calendarView != null) {
                calendarView.notifyCalendarChanged();
            }
        });

//        getParentFragmentManager().setFragmentResultListener("taskAddedRequest", getViewLifecycleOwner(), (requestKey, bundle) -> {
//            // Ovaj kod će se izvršiti KADA RecurringTaskFragment pošalje signal
//
//
//
//            // 2. KORAK: Naredi UI-ju (kalendaru) da se ponovo iscrta sa svežim podacima
//            if (calendarView != null) {
//                calendarView.notifyCalendarChanged();
//            }
//
//            Toast.makeText(getContext(), "Kalendar osvežen!", Toast.LENGTH_SHORT).show();
//        });



        final YearMonth currentMonth = YearMonth.now();
        calendarView.setup(currentMonth.minusMonths(12), currentMonth.plusMonths(12), DayOfWeek.MONDAY);
        calendarView.scrollToMonth(currentMonth);


        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {

                container.getCalendarDayText().setText(String.valueOf(day.getDate().getDayOfMonth()));
                container.getCalendarDayText().setTextColor(Color.argb(255, 253, 221, 230));

                if (day.getPosition() == DayPosition.MonthDate) {
                    container.getView().setVisibility(View.VISIBLE);

                    container.getCalendarDayText().setTextColor(Color.parseColor("#fca103"));

                    if (day.equals(selectedDate)) {
                        container.getCalendarDayText().setBackgroundColor(Color.WHITE);
                    } else {
                        container.getCalendarDayText().setBackgroundColor(Color.TRANSPARENT);
                    }

                    // 4. (Opciono) Tvoja logika za bojenje dana ako ima zadataka
                    // List<Task> tasksForDay = calendarViewModel.getTasksForDate(day.getDate());
                    // if (!tasksForDay.isEmpty()) {
                    //     // Oboj pozadinu ili dodaj tačkicu ispod broja
                    // }

                } else {
                    container.getView().setVisibility(View.INVISIBLE);
                }
                container.getView().setOnClickListener(v -> {
                    CalendarDay oldDate = selectedDate;
                    selectedDate = day;
                    calendarView.notifyDateChanged(day.getDate());

                    if (oldDate != null) {
                        calendarView.notifyDateChanged(oldDate.getDate());
                    }


                    calendarViewModel.selectDate(day.getDate());

                    getParentFragmentManager().beginTransaction()

                            .replace(R.id.fragment_container, new TaskSlotCalendarFragment())

                            .setCustomAnimations(
                                    android.R.anim.slide_in_left,
                                    android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.slide_out_right
                            )
                            .addToBackStack(null)
                            .commit();
                });


                if (day.equals(selectedDate)) {
                    container.getCalendarDayText().setBackgroundColor(Color.WHITE);
                } else {
                    container.getCalendarDayText().setBackgroundColor(Color.TRANSPARENT);
                }




            }
        });

//        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
//            @NonNull
//            @Override
//            public MonthViewContainer create(@NonNull View view) {
//                return new MonthViewContainer(view);
//            }
//
//            @Override
//            public void bind(@NonNull MonthViewContainer container, @NonNull CalendarMonth month) {
//                String header = month.getYearMonth().getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()) + " " + month.getYearMonth();
//                container.getCalendarMonthText().setText(header);
//
//
//                container.previousButton.setOnClickListener(v -> {
//                    calendarView.smoothScrollToMonth(month.getYearMonth().minusMonths(1));
//                });
//
//
//                container.nextButton.setOnClickListener(v -> {
//                    calendarView.smoothScrollToMonth(month.getYearMonth().plusMonths(1));
//                });
//            }
//        });

        calendarView.setMonthScrollListener(calendarMonth -> {

                String monthName = calendarMonth.getYearMonth().getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

                              String title = monthName.toUpperCase()
                        + " " + calendarMonth.getYearMonth().getYear();

                monthTextView.setText(title);
                monthTextView.setTextColor(Color.parseColor("#fca103"));

                return kotlin.Unit.INSTANCE;
        });


        prevButton.setOnClickListener(v -> {

            YearMonth current = findFirstVisibleMonth();
            if (current != null) {
                calendarView.smoothScrollToMonth(current.minusMonths(1));
            }
        });


        nextButton.setOnClickListener(v -> {
            YearMonth current = findFirstVisibleMonth();
            if (current != null) {
                calendarView.smoothScrollToMonth(current.plusMonths(1));
            }
        });





    }

    private YearMonth findFirstVisibleMonth() {
        if (calendarView.findFirstVisibleMonth() != null) {
            return calendarView.findFirstVisibleMonth().getYearMonth();
        }
        return null;
    }


    private void showTaskSlots(CalendarDay day) {

        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra("selected_date", day.getDate());
        startActivity(intent);
    }

    private static class DayViewContainer extends ViewContainer {
        private final TextView calendarDayText;

        public DayViewContainer(@NonNull View view) {
            super(view);
            calendarDayText = view.findViewById(com.example.myhobitapplication.R.id.calendarDayText);
        }

        public TextView getCalendarDayText() {
            return calendarDayText;
        }
    }

    private static class MonthViewContainer extends ViewContainer {
        private final TextView calendarMonthText;
        final ImageView previousButton;
        final ImageView nextButton;

        public MonthViewContainer(@NonNull View view) {
            super(view);
            calendarMonthText = view.findViewById(R.id.calendarMonthText);
            previousButton = view.findViewById(R.id.previousMonthButton);
            nextButton = view.findViewById(R.id.nextMonthButton);
        }

        public TextView getCalendarMonthText() {
            return calendarMonthText;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        calendarBinding = null;
    }
}
