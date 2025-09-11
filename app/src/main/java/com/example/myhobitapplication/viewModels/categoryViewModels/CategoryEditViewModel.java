package com.example.myhobitapplication.viewModels.categoryViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.services.CategoryService;

public class CategoryEditViewModel extends ViewModel {


    private final CategoryService categoryService;
    private final MutableLiveData<CategoryDTO> _categoryDetails = new MutableLiveData<>();
    public LiveData<CategoryDTO> getCategoryDetails() { return _categoryDetails; }
    private final MutableLiveData<String> _name = new MutableLiveData<>();
    private final MutableLiveData<String> _colour = new MutableLiveData<>();

    public CategoryEditViewModel(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    public void loadCategoryDetails(int categoryId) {
        CategoryDTO dto = categoryService.getByid(categoryId);
        if (dto != null) {
            _categoryDetails.setValue(dto);
            _name.setValue(dto.getName());
            _colour.setValue(dto.getColour());
        }
    }

    public void setName(String name) { _name.setValue(name); }
    public void setColour(String colour) { _colour.setValue(colour); }
    public MutableLiveData<String> getColour() { return _colour; }

    public void updateCategory() {
        CategoryDTO originalDto = _categoryDetails.getValue();
        if (originalDto == null) return;

        originalDto.setName(_name.getValue());
        originalDto.setColour(_colour.getValue());

        categoryService.updateCategory(originalDto);
    }




}
