package com.example.myhobitapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myhobitapplication.fragments.CategoryFragment;
import com.example.myhobitapplication.fragments.tasksFragments.OneTimeTaskFragment;
import com.example.myhobitapplication.fragments.tasksFragments.OneTimeTaskViewFragment;
import com.example.myhobitapplication.fragments.tasksFragments.RecurringTaskFragment;
import com.example.myhobitapplication.fragments.TaskCalendarFragment;
import com.example.myhobitapplication.fragments.tasksFragments.RecurringTaskViewFragment;

public class TaskPagerAdapter extends FragmentStateAdapter {

    public TaskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new OneTimeTaskViewFragment();
        } else if (position == 1) {
            return new RecurringTaskViewFragment();
        } else {
            return new TaskCalendarFragment();
        }
//        else
//        {
//            return new RecurringTaskViewFragment();
//        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
