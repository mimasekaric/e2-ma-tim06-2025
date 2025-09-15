package com.example.myhobitapplication.viewModels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    private final Context appContext;
    private final BossService bossService;
    private final EquipmentService equipmentService;
    public ProfileViewModelFactory(Context context, BossService bossService, EquipmentService equipmentService) {
        this.appContext = context.getApplicationContext();
        this.bossService = bossService;
        this.equipmentService = equipmentService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProfileViewModel(appContext,bossService,equipmentService);
    }



}
