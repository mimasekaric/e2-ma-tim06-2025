package com.example.myhobitapplication.viewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.UserEquipmentRepository;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.services.UserEquipmentService;
import com.example.myhobitapplication.services.UserService;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class UserEquipmentViewModel extends ViewModel {

    private UserEquipmentService service;

    private ProfileViewModel profileViewModel;
    private  String userId = "";
    public   UserEquipmentViewModel(Context context, ProfileViewModel pv){
        this.service = new UserEquipmentService(context);
        this.profileViewModel = pv;
    }

    public List<Equipment> getEquipmentByType(EquipmentTypes type){ return service.getByType(type);}

    public List<EquipmentWithPriceDTO> getEquipmentByTypeWithPrice(EquipmentTypes type, Profile profile){
        List<Equipment> equipment = getEquipmentByType(type);
        List<EquipmentWithPriceDTO> equipmentWithPriceDTOS = new ArrayList<>();
        for(Equipment e : equipment){
            EquipmentWithPriceDTO ep = new EquipmentWithPriceDTO(e, service.countPrice(profile, e));
            equipmentWithPriceDTOS.add(ep);
        }
        return equipmentWithPriceDTOS;
    }

    public boolean buyEquipment(Profile profile, Equipment equipment){
        boolean response = service.buyEquipment(profile,equipment);
        if (response){
        profileViewModel.loadProfile(profile.getuserUid());
        return true;
        }
        else{
            return false;
        }
        }


}
