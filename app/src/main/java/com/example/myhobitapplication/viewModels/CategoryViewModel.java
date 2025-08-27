package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.CloudStoreUtil;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.CategoryService;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private CategoryService categoryService;

    private final MutableLiveData<String> name = new MutableLiveData<>("");
    private final MutableLiveData<String> colour = new MutableLiveData<>("");
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public CategoryViewModel(CategoryService categoryService){
        this.categoryService = categoryService;
        loadAllCategories();
    }

    public void loadAllCategories() {
        List<Category> categoriesData = categoryService.getAllCategories();

        categories.setValue(categoriesData);
    }
    public MutableLiveData<String> getName(){ return name;}
    public MutableLiveData<String> getColour(){ return colour;}

    public void setName(String nameValue) { name.setValue(nameValue); }
    public void setColour(String colourValue){ colour.setValue(colourValue); }

    public void inDB (){
        CloudStoreUtil.initDB();
    }

    public MutableLiveData<List<Category>> getAllCategories() {
        return categories;
    }

    public void saveCategory() {

        Category category = new Category(
               name.getValue(),
                colour.getValue()
        );
        categoryService.insertCategory(category);
        loadAllCategories();
    }

}
