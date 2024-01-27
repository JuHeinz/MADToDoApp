package org.julheinz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> emailErrorStatus = new MutableLiveData<>();
    private MutableLiveData<String> passwordErrorStatus = new MutableLiveData<>();

    public MutableLiveData<String> getEmailErrorStatus() {
        return emailErrorStatus;
    }

    public MutableLiveData<String> getPasswordErrorStatus() {
        return passwordErrorStatus;
    }

    public void onEmailInputChanged(){
       emailErrorStatus.setValue("Email Error!");
   }
   public void onPasswordInputChanged(){
       passwordErrorStatus.setValue("Password Error!");
   }



}
