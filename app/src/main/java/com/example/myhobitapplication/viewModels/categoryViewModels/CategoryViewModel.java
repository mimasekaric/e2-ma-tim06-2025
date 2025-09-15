package com.example.myhobitapplication.viewModels.categoryViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.CategoryService;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private CategoryService categoryService;

    private final MutableLiveData<String> name = new MutableLiveData<>("");
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    private final MutableLiveData<String> _nameError = new MutableLiveData<>(null);
    public LiveData<String> getNameError() { return _nameError; }

    private final MutableLiveData<Boolean> _isFormValid = new MutableLiveData<>(false);
    public LiveData<Boolean> isFormValid() { return _isFormValid; }


    private final MutableLiveData<String> _submissionError = new MutableLiveData<>();
    public LiveData<String> getSubmissionError() { return _submissionError; }

    private final MutableLiveData<Boolean> _saveSuccessEvent = new MutableLiveData<>();
    public LiveData<Boolean> getSaveSuccessEvent() { return _saveSuccessEvent; }

    private final MutableLiveData<String> colour = new MutableLiveData<>("#000000"); // Počni sa crnom


    public CategoryViewModel(CategoryService categoryService){
        this.categoryService = categoryService;
        loadAllCategories();
    }

    public void loadAllCategories() {
        List<Category> categoriesData = categoryService.getAllCategories();

        categories.setValue(categoriesData);
        validateForm();
    }
    public MutableLiveData<String> getName(){ return name;}
    public MutableLiveData<String> getColour(){ return colour;}

    public void setName(String nameValue) {

        name.setValue(nameValue);
        validateForm();
    }
    public void setColour(String colourValue){ colour.setValue(colourValue); }


    public MutableLiveData<List<Category>> getAllCategories() {
        return categories;
    }

    public void saveCategory() {

        validateForm();
        if (_isFormValid.getValue() != null && !_isFormValid.getValue()) {
            return;
        }

        Category category = new Category(name.getValue(), colour.getValue());

        try {

            categoryService.insertCategory(category);


            _saveSuccessEvent.setValue(true);

        } catch (ValidationException e) {

            _submissionError.setValue(e.getMessage());
        }
    }

    private void validateForm() {
        String currentName = name.getValue();

        if (currentName == null || currentName.trim().isEmpty()) {
            _nameError.setValue("category name is required");
            _isFormValid.setValue(false);
        } else {

            _nameError.setValue(null);
            _isFormValid.setValue(true);
        }
    }

    public void onSaveSuccessEventHandled() {
        _saveSuccessEvent.setValue(false);
    }

}
