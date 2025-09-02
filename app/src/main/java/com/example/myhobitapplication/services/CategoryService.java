package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.DataBaseCategoryHelper;
import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;

import java.util.ArrayList;
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

    public List<CategoryDTO> getAll() {

        List<CategoryDTO> categoryDtos = new ArrayList<>();
        List<Category> allCategoriesFromDb = getAllCategories(); // Dobavi listu jednom da bude efikasnije

        for (Category category : allCategoriesFromDb) {


            CategoryDTO categoryDTO = new CategoryDTO();

            categoryDTO.setName(category.getName());
            categoryDTO.setColour(category.getColour());
            categoryDTO.setId(category.getId());

            categoryDtos.add(categoryDTO);
        }

        return categoryDtos;
    }

    public Category getCategoryById(long id) {
        return repository.getCategoryById(id);
    }
    public CategoryDTO getByid(long id) {
        Category category =  repository.getCategoryById(id);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(category.getName());
        categoryDTO.setColour(category.getColour());
        categoryDTO.setId(category.getId());
        return categoryDTO;
    }
}
