package com.example.myhobitapplication.viewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airbnb.lottie.L;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databases.UserEquipmentRepository;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.dto.UserEquipmentDTO;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserEquipmentService;
import com.example.myhobitapplication.services.UserService;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class UserEquipmentViewModel extends ViewModel {

    private UserEquipmentService service;

    private final MutableLiveData<List<Equipment>> notActivatedEquipment = new MutableLiveData<>();

    private  String userId = "";
    public   UserEquipmentViewModel(Context context , BossService bossService, EquipmentService equipmentService, ProfileService profileService){
        this.service = new UserEquipmentService(context, profileService,  bossService, equipmentService);
    }

    public List<Equipment> getEquipmentByType(EquipmentTypes type){ return service.getByType(type);}
    public List<Equipment> getEquipmentForUser(String id){return  service.getUserEquipment(id);}
    public List<UserEquipmentDTO> getNotActivatedEquipmentForUser(String id){
        List<UserEquipmentDTO> list = service.getUserNotActivatedEquipment(id);
        return list;
    }
    public List<EquipmentWithPriceDTO> getEquipmentByTypeWithPrice(EquipmentTypes type, Profile profile){
        List<Equipment> equipment = getEquipmentByType(type);
        List<EquipmentWithPriceDTO> equipmentWithPriceDTOS = new ArrayList<>();
        for(Equipment e : equipment){
            EquipmentWithPriceDTO ep = new EquipmentWithPriceDTO(e, service.countPrice(profile, e));
            equipmentWithPriceDTOS.add(ep);
        }
        return equipmentWithPriceDTOS;
    }

    public void activateEquipment(UserEquipmentDTO equipment, Profile profile){
        service.activateEquipment(equipment, profile);
    }

    public boolean buyEquipment(Profile profile, Equipment equipment){
        boolean response = service.buyEquipment(profile,equipment);
        if (response){

        return true;
        }
        else{
            return false;
        }
        }


}
