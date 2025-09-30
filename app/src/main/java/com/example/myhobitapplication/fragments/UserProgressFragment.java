package com.example.myhobitapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentCategoryBinding;
import com.example.myhobitapplication.databinding.FragmentUserProgressBinding;
import com.example.myhobitapplication.services.AllianceService;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserService;
import com.example.myhobitapplication.viewModels.UserProgressViewModel;
import com.example.myhobitapplication.viewModels.categoryViewModels.CategoryViewModel;

public class UserProgressFragment extends Fragment {


    public UserProgressViewModel userProgressViewModel;
    public FragmentUserProgressBinding userProgressBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserService userService = new UserService();
        ProfileService profileService = ProfileService.getInstance();
        userProgressViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserProgressViewModel(profileService);
            }
        }).get(UserProgressViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        userProgressBinding = FragmentUserProgressBinding.inflate(inflater, container, false);
        return userProgressBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
