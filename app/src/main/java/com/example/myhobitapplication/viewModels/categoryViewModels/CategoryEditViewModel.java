package com.example.myhobitapplication.viewModels.categoryViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.services.CategoryService;

public class CategoryEditViewModel extends ViewModel {


    private final CategoryService categoryService;
    private final MutableLiveData<CategoryDTO> _categoryDetails = new MutableLiveData<>();
    public LiveData<CategoryDTO> getCategoryDetails() { return _categoryDetails; }
    private final MutableLiveData<String> _name = new MutableLiveData<>();
    private final MutableLiveData<String> _colour = new MutableLiveData<>();

    private final MutableLiveData<String> _nameError = new MutableLiveData<>(null);
    public LiveData<String> getNameError() { return _nameError; }

    private final MutableLiveData<Boolean> _isFormValid = new MutableLiveData<>(false);
    public LiveData<Boolean> isFormValid() { return _isFormValid; }


    private final MutableLiveData<String> _submissionError = new MutableLiveData<>();
    public LiveData<String> getSubmissionError() { return _submissionError; }

    private final MutableLiveData<Boolean> _saveSuccessEvent = new MutableLiveData<>();
    public LiveData<Boolean> getSaveSuccessEvent() { return _saveSuccessEvent; }

    private final MutableLiveData<Boolean> _categoryDeletedEvent = new MutableLiveData<>();

    public LiveData<Boolean> getCategoryDeletedEvent() {
        return _categoryDeletedEvent;
    }
    public CategoryEditViewModel(CategoryService categoryService) {
        this.categoryService = categoryService;
        validateForm();
    }


    public void loadCategoryDetails(int categoryId) {
        CategoryDTO dto = categoryService.getByid(categoryId);
        if (dto != null) {
            _categoryDetails.setValue(dto);
            _name.setValue(dto.getName());
            _colour.setValue(dto.getColour());
        }
    }

    public void setName(String name) { _name.setValue(name); validateForm();}
    public void setColour(String colour) { _colour.setValue(colour); }
    public MutableLiveData<String> getColour() { return _colour; }

    public void updateCategory() {


        validateForm();
        if (_isFormValid.getValue() != null && !_isFormValid.getValue()) {
            return;
        }

        CategoryDTO originalDto = _categoryDetails.getValue();
        if (originalDto == null) return;

        originalDto.setName(_name.getValue());
        originalDto.setColour(_colour.getValue());

        try {
            categoryService.updateCategoryTransactional(originalDto);
            _saveSuccessEvent.setValue(true);
        }catch (ValidationException e){
            _submissionError.setValue(e.getMessage());
        }

    }

    public void deleteCategory() {
        CategoryDTO categoryDTO = _categoryDetails.getValue();
        if(categoryDTO!=null){
            categoryService.deleteCategory(categoryDTO);
        }
    }

    private void validateForm() {
        String currentName = _name.getValue();

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

    public void onTaskDeletedEventHandled() {
        _categoryDeletedEvent.setValue(false);
    }

}
