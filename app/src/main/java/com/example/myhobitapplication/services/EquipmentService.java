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

    public List<Equipment> getEquipmentByType(EquipmentTypes type){
        return repository.getEquipmentByType(type);
    }

    public Equipment getEquipmentById(String id){
        return repository.getEquipmentById(id);
    }

    public int updateEquipment(Equipment eq){
        return   repository.updateEquipment(eq);
    }

}
