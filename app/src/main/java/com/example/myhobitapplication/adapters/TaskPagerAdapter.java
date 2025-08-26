package com.example.myhobitapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myhobitapplication.fragments.CategoryFragment;
import com.example.myhobitapplication.fragments.OneTimeTaskFragment;
import com.example.myhobitapplication.fragments.RecurringTaskFragment;
import com.example.myhobitapplication.fragments.TaskCalendarFragment;

public class TaskPagerAdapter extends FragmentStateAdapter {

    public TaskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new OneTimeTaskFragment();
        } else if (position == 1) {
            return new RecurringTaskFragment();
        } else if (position == 2){
            return new TaskCalendarFragment();
        }
        else
        {
            return new CategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
