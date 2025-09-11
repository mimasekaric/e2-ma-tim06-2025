package com.example.myhobitapplication.viewModels.categoryViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.services.CategoryService;

import java.util.List;

public class CategoryListViewModel extends ViewModel {


    private final MutableLiveData<CategoryDTO> category = new MutableLiveData<>();

    private final MutableLiveData<List<CategoryDTO>> categories = new MutableLiveData<>();
    private final CategoryService categoryService;

    public CategoryListViewModel(CategoryService categoryService){

        this.categoryService = categoryService;
        loadCategory();
    }

    public MutableLiveData<CategoryDTO> getCategory() { return this.category;}

    public MutableLiveData<List<CategoryDTO>> getAllCategories() { return categories;}

    public void setCategory(CategoryDTO categoryData){category.setValue(categoryData);}

    public void loadCategory() {
        List<CategoryDTO> allCategories = categoryService.getAll();
        categories.setValue(allCategories);
    }

    public void refreshScheduledTasks() {
        loadCategory();
    }












}
