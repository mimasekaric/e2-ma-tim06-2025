package com.example.myhobitapplication.services;

import android.database.sqlite.SQLiteDatabase;

import com.example.myhobitapplication.databases.AppDataBaseHelper;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private final CategoryRepository repository;
    private final TaskRepository taskRepository;

    public CategoryService(CategoryRepository repository, TaskRepository taskRepository){
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.repository.open();
    }
    public long insertCategory(Category category) throws ValidationException {


        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("category name is required.");
        }

        if (repository.doesCategoryNameExist(category.getName())) {
            throw new ValidationException("Category with name '" + category.getName() + "' already exists.");
        }

        if (repository.doesCategoryColourExists(category.getColour())) {
            throw new ValidationException("Category with colour '" + category.getColour() + "' already exists.");
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

    public CategoryDTO getByColour(String colour) {
        Category category =  repository.getCategoryByColour(colour);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(category.getName());
        categoryDTO.setColour(category.getColour());
        categoryDTO.setId(category.getId());
        return categoryDTO;
    }

    public long updateCategory(CategoryDTO categoryDTO) throws ValidationException {

        Category category = new Category();
        category.setColour(categoryDTO.getColour());
        category.setName(categoryDTO.getName());
        category.setId(categoryDTO.getId());

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("category name is required.");
        }

        if (repository.doesUpdateCategoryNameExist(category.getName(),category.getId())) {
            throw new ValidationException("Category with name '" + category.getName() + "' already exists.");
        }

        if (repository.doesUpdateCategoryColourExist(category.getColour(), category.getId())) {
            throw new ValidationException("Category with colour '" + category.getColour() + "' already exists.");
        }

        return repository.updateCategory(category);
    }
    public void updateCategoryTransactional(CategoryDTO categoryDto) throws ValidationException {
        if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            throw new ValidationException("category name is required.");
        }

        if (repository.doesUpdateCategoryNameExist(categoryDto.getName(),categoryDto.getId())) {
            throw new ValidationException("Category with name '" + categoryDto.getName() + "' already exists.");
        }

        if (repository.doesUpdateCategoryColourExist(categoryDto.getColour(), categoryDto.getId())) {
            throw new ValidationException("Category with colour '" + categoryDto.getColour() + "' already exists.");
        }



        Category oldCategory = repository.getCategoryById(categoryDto.getId());
        if (oldCategory == null) {
            throw new ValidationException("Category doesn't exists.");
        }

        Category category = new Category();
        category.setColour(categoryDto.getColour());
        category.setName(categoryDto.getName());
        category.setId(categoryDto.getId());

        repository.updateCategoryAndTasksTransactional(
                category,
                oldCategory.getColour(),
                taskRepository
        );
    }

    public long deleteCategory(CategoryDTO categoryDto){
        Category category = new Category();
        category.setColour(categoryDto.getColour());
        category.setName(categoryDto.getName());
        category.setId(categoryDto.getId());

        return repository.deleteCategory(category);
    }
}
