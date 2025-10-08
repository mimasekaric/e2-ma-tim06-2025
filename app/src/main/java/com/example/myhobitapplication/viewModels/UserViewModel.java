package com.example.myhobitapplication.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhobitapplication.databases.DataBaseHelper;
import com.example.myhobitapplication.models.UserTest;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private DataBaseHelper databaseHelper;
    private MutableLiveData<List<UserTest>> usersLiveData;

    public UserViewModel(Application application) {
        super(application);

        databaseHelper = new DataBaseHelper(application);

        usersLiveData = new MutableLiveData<>();

        fetchAllUsers();
    }


    public LiveData<List<UserTest>> getUsers() {
        return usersLiveData;
    }


    public void addUser(String name, String surname) {

        boolean success = databaseHelper.addUser(name, surname);
        if (success) {

            fetchAllUsers();
        }

    }


    private void fetchAllUsers() {
        List<UserTest> users = databaseHelper.getAllUsers();
        usersLiveData.setValue(users);
    }
}
