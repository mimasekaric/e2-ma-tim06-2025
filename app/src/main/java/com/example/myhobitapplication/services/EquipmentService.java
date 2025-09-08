package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.models.Category;

public class EquipmentService {
    private final EquipmentRepository repository;
    public EquipmentService(EquipmentRepository repository){
        this.repository = repository;
        this.repository.open();
    }

    public void buyEquipment(){}
    public void activateEquipement(){}
}
