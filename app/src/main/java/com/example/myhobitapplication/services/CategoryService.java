package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.DataBaseCategoryHelper;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;

import java.util.List;

public class CategoryService {

    private final CategoryRepository repository;
    public CategoryService(CategoryRepository repository){
        this.repository = repository;
        this.repository.open();
    }
    public long insertCategory(Category category){
        return repository.insertCategory(category);
    }
    public List<Category> getAllCategories(){
        return repository.getAllCategories();
    }

    public Category getCategoryById(long id) {
        return repository.getCategoryById(id);
    }
}
