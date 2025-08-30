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
    private final MutableLiveData<String> confirmPassword = new MutableLiveData<>("");
    private final MutableLiveData<String> avatarName = new MutableLiveData<String>("");

    public RegistrationViewModel(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    private  MutableLiveData<Boolean> _registrationSuccess = new MutableLiveData<Boolean>(true);
    public MutableLiveData<Boolean> getRegistrationSuccess (){return  _registrationSuccess;}
    public MutableLiveData<String> getUsername(){ return username;}
    public MutableLiveData<String> getEmail(){ return email;}
    public MutableLiveData<String> getPassword(){ return password;}
    public MutableLiveData<String> getConfirmPassword(){ return confirmPassword;}
    public MutableLiveData<String> getAvatarName(){ return avatarName;}

    public void setUsername(String nameValue) { username.setValue(nameValue); }
    public void setEmail(String emailValue){ email.setValue(emailValue); }
    public void setPassword(String passwordValue){ password.setValue(passwordValue); }
    public void setConfirmPassword(String passwordValue){ confirmPassword.setValue(passwordValue); }

    public void setAvatarName(String avatarValue){avatarName.setValue(avatarValue);}
    public void saveUser() {
        if(password.getValue().equals(confirmPassword.getValue())) {
            registrationService.Register(email.getValue(), username.getValue(), password.getValue(), avatarName.getValue())
                    .addOnSuccessListener(documentReference -> {
                        _registrationSuccess.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        _registrationSuccess.setValue(false);
                    });
        }else{
            _registrationSuccess.setValue(false);
        }
    }



}
