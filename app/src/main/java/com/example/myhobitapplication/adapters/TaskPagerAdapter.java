package com.example.myhobitapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myhobitapplication.fragments.OneTimeTaskFragment;
import com.example.myhobitapplication.fragments.RecurringTaskFragment;

public class TaskPagerAdapter extends FragmentStateAdapter {

    public TaskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new OneTimeTaskFragment();
        } else {
            return new RecurringTaskFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
