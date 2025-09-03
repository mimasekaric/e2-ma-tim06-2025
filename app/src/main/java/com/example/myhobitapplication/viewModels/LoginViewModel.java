package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.services.UserService;

public class LoginViewModel extends ViewModel {

    private UserService registrationService;
    private  String userId = "";
    public   LoginViewModel(){
        this.registrationService = new UserService();
    }
    private final MutableLiveData<Boolean> loginSuccess= new MutableLiveData<>(false);
    private  MutableLiveData<String> response = new MutableLiveData<String>("");
    private  MutableLiveData<String> passResponse = new MutableLiveData<String>("");
    private  MutableLiveData<Boolean> passSuccess = new MutableLiveData<Boolean>(false);
    public MutableLiveData<String> getResponse (){return  response;}
    public MutableLiveData<String> getPassesponse (){return  passResponse;}
    public MutableLiveData<Boolean> getPassSuccess (){return  passSuccess;}
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<Boolean> getLoginSuccess (){return  loginSuccess;}
    public MutableLiveData<String> getEmail(){ return email;}
    public MutableLiveData<String> getPassword(){ return password;}

    public String getUserId() {
        return userId;
    }

    public void setEmail(String emailValue){ email.setValue(emailValue); }
    public void setPassword(String passwordValue){ password.setValue(passwordValue); }

    public void changePassword(String pass){
        registrationService.updatePass(pass).addOnSuccessListener(v -> {
            passSuccess.setValue(true);
            passResponse.setValue("Success");
        }).addOnFailureListener(e -> {passSuccess.setValue(false);});
    }
    public void loginUser(){
        response.setValue("");
        loginSuccess.setValue(false);
        registrationService.Login(email.getValue(),password.getValue()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                response.setValue("Succesfully logged in!");
                loginSuccess.setValue(true);
                userId = task.getResult().getUser().getUid();
            } else {
                response.setValue(task.getException().getMessage());
                loginSuccess.setValue(false);
            }
        });
    }

    public void logout(){
        registrationService.logout();
    }
}
