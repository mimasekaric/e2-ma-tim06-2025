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
        // Inicijalizacija baze pomoću konteksta aplikacije
        databaseHelper = new DataBaseHelper(application);
        // Inicijalizacija LiveData objekta
        usersLiveData = new MutableLiveData<>();
        // Odmah dohvatamo podatke pri kreiranju
        fetchAllUsers();
    }

    // Metoda koju UI može posmatrati
    public LiveData<List<UserTest>> getUsers() {
        return usersLiveData;
    }

    // Metoda za dodavanje korisnika
    public void addUser(String name, String surname) {
        // Zovemo helper klasu za unos
        boolean success = databaseHelper.addUser(name, surname);
        if (success) {
            // Ako je unos uspešan, ažuriramo LiveData
            // Ovo će automatski obavestiti UI da su podaci promenjeni
            fetchAllUsers();
        }
        // Možete dodati i neku logiku za grešku
    }

    // Privatna metoda za dohvaćanje podataka iz baze i ažuriranje LiveData-e
    private void fetchAllUsers() {
        List<UserTest> users = databaseHelper.getAllUsers();
        usersLiveData.setValue(users);
    }
}
