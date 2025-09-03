package com.example.myhobitapplication.viewModels;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.services.UserService;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class RegistrationViewModel extends ViewModel {

    private UserService registrationService;


    private final MutableLiveData<Boolean> registrationSuccess= new MutableLiveData<>(false);
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> username = new MutableLiveData<>("");

    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<String> confirmPassword = new MutableLiveData<>("");
    private final MutableLiveData<String> avatarName = new MutableLiveData<String>("");

    public RegistrationViewModel(){
        this.registrationService = new UserService();
    }

    private  MutableLiveData<String> response = new MutableLiveData<String>("");
    public MutableLiveData<String> getResponse (){return  response;}
    public MutableLiveData<Boolean> getRegistrationSuccess (){return  registrationSuccess;}
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
        if (!validateFields()) {
            registrationSuccess.setValue(false);
            return;
        }

        if (!password.getValue().equals(confirmPassword.getValue())) {
            registrationSuccess.setValue(false);
            response.setValue("Your passwords must match");
            return;
        }

        Date dateNow = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        registrationService.Register(email.getValue(), username.getValue(), password.getValue(), avatarName.getValue(), dateNow)
                .addOnSuccessListener(documentReference -> {
                    response.setValue("success");
                    registrationSuccess.setValue(true);
                    FirebaseAuth.getInstance().signOut();
                })
                .addOnFailureListener(e -> {
                    response.setValue("Failed signup! " + e.getMessage());
                    registrationSuccess.setValue(false);
                });
    }


    private boolean validateFields(){
        if(!username.getValue().matches("(?=.*[A-Za-z])[A-Za-z0-9._]+")) {
            response.setValue("Username must contain at least one letter and optional numbers or {. _ }");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getValue()).matches()) {
            response.setValue("Please enter correct email address");
            return false;
        }
        if(password.getValue().length()<6){
            response.setValue("Password must be at least 6 character long");
            return false;
        }
        if(!password.getValue().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).*$")) {
            response.setValue("Password must contain at least one uppercase letter , one lowercase letter and one number ");
            return false;
        }
        return true;
    }
}
