package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.Equipment;

import java.util.List;

public class EquipmentService {
    private final EquipmentRepository repository;
    public EquipmentService(EquipmentRepository repository){
        this.repository = repository;
        this.repository.open();
    }

    public List<Equipment> getByType(EquipmentTypes type){
        return repository.getEquipmentByType(type);
    }

}
