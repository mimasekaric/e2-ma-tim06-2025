package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.RegistrationService;

import java.util.List;

public class RegistrationViewModel extends ViewModel {

    private RegistrationService registrationService;

    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> username = new MutableLiveData<>("");

    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<Integer> avatarId = new MutableLiveData<Integer>(0);

    public RegistrationViewModel(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    public MutableLiveData<String> getUsername(){ return username;}
    public MutableLiveData<String> getEmail(){ return email;}
    public MutableLiveData<String> getPassword(){ return password;}
    public MutableLiveData<Integer> getAvatarId(){ return avatarId;}

    public void setUsername(String nameValue) { username.setValue(nameValue); }
    public void setEmail(String emailValue){ email.setValue(emailValue); }
    public void setPassword(String passwordValue){ password.setValue(passwordValue); }

    public void saveUser() {

        registrationService.Register( email.getValue(),username.getValue(), password.getValue());
    }



}
