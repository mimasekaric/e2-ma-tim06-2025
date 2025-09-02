package com.example.myhobitapplication.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.adapters.CategoryListAdapter;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databinding.ActivityCategoryViewBinding;
import com.example.myhobitapplication.dto.CategoryDTO;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Task;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.viewModels.categoryViewModels.CategoryListViewModel;
import com.example.myhobitapplication.viewModels.categoryViewModels.CategoryeEditViewModel;

import java.util.ArrayList;

public class CategoryViewActivity extends AppCompatActivity {

    private ListView categoryListView;
    private CategoryListAdapter adapter;
    private CategoryListViewModel categoryViewModel;
    private ActivityCategoryViewBinding binding;

    private final ActivityResultLauncher<Intent> categoryEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Toast.makeText(this, "Lista kategorija osvježena.", Toast.LENGTH_SHORT).show();
                    if (categoryViewModel != null) {
                        categoryViewModel.loadCategory();
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCategoryViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CategoryRepository repository = new CategoryRepository(this);
        CategoryService service = new CategoryService(repository);

        categoryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CategoryListViewModel(service);
            }
        }).get(CategoryListViewModel.class);


        CategoryListAdapter adapter = new CategoryListAdapter(this, new ArrayList<>());
        binding.categoryListView.setAdapter(adapter);

        categoryViewModel.getAllCategories().observe(this, newCategories -> {

            adapter.updateData(newCategories);
        });


        binding.categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            CategoryDTO clickedCategory = (CategoryDTO) adapter.getItem(position);

            if (clickedCategory == null) return;

            Intent intent = new Intent(this, CategoryEditActivity.class);

            intent.putExtra("CATEGORY_ID_EXTRA", clickedCategory.getId());

            categoryEditLauncher.launch(intent);
        });

        // Listener za dugme "Dodaj novu kategoriju" - koristeći binding
//        binding.btnAddCategory.setOnClickListener(v -> {
//            // Pokreni aktivnost za kreiranje nove kategorije
//            // Intent intent = new Intent(this, CategoryCreateActivity.class);
//            // startActivity(intent);
//        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (categoryViewModel != null) {
            categoryViewModel.loadCategory();
        }
    }






}
