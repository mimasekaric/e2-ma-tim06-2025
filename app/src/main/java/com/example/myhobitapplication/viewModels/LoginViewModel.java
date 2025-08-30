package com.example.myhobitapplication.viewModels;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.services.RegistrationService;

public class LoginViewModel extends ViewModel {

    private RegistrationService registrationService;

    public   LoginViewModel(RegistrationService registrationService){
        this.registrationService = registrationService;
    }
    private final MutableLiveData<Boolean> loginSuccess= new MutableLiveData<>(false);
    private  MutableLiveData<String> response = new MutableLiveData<String>("");
    public MutableLiveData<String> getResponse (){return  response;}
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<Boolean> getLoginSuccess (){return  loginSuccess;}
    public MutableLiveData<String> getEmail(){ return email;}
    public MutableLiveData<String> getPassword(){ return password;}

    public void setEmail(String emailValue){ email.setValue(emailValue); }
    public void setPassword(String passwordValue){ password.setValue(passwordValue); }

    public void loginUser(){
        registrationService.Login(email.getValue(),password.getValue()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                response.setValue("Succesfully logged in!");
                loginSuccess.setValue(true);
            } else {
                response.setValue("Login failed!");
                loginSuccess.setValue(false);
            }
        });
    }
}
