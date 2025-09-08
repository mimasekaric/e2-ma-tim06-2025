package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private final CategoryRepository repository;
    public CategoryService(CategoryRepository repository){
        this.repository = repository;
        this.repository.open();
    }
    public long insertCategory(Category category) throws ValidationException {


        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("category name is required.");
        }

        if (repository.doesCategoryNameExist(category.getName())) {
            throw new ValidationException("Category with name '" + category.getName() + "' already exists.");
        }




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

    public long updateCategory(CategoryDTO categoryDTO){

        Category category = new Category();
        category.setColour(categoryDTO.getColour());
        category.setName(categoryDTO.getName());
        category.setId(categoryDTO.getId());
        return repository.updateCategory(category);
    }
}
