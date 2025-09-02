package com.example.myhobitapplication.viewModels.categoryViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.CategoryService;

import java.util.List;

public class CategoryeEditViewModel extends ViewModel {


    private final CategoryService categoryService;
    private final MutableLiveData<CategoryDTO> _categoryDetails = new MutableLiveData<>();
    public LiveData<CategoryDTO> getCategoryDetails() { return _categoryDetails; }
    private final MutableLiveData<String> _name = new MutableLiveData<>();
    private final MutableLiveData<String> _colour = new MutableLiveData<>();

    public CategoryeEditViewModel(CategoryService categoryService) {
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

    // Metoda za ažuriranje
//    public void updateCategory() {
//        CategoryDTO originalDto = _categoryDetails.getValue();
//        if (originalDto == null) return;
//
//        // Ažuriraj DTO sa novim podacima iz forme
//        originalDto.setName(_name.getValue());
//        originalDto.setColour(_colour.getValue());
//
//        // Prosledi ažurirani DTO u servis
//        categoryService.updateCategory(originalDto); // Moraš imati ovu metodu u servisu
//    }




}
