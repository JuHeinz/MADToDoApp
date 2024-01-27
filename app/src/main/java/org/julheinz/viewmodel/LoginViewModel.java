package org.julheinz.viewmodel;

import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.entities.LoginEntity;

public class LoginViewModel extends ViewModel {
    private static final String LOG_TAG = LoginViewModel.class.getSimpleName();

    public LoginEntity getEntity() {
        return entity;
    }

    public void setEntity(LoginEntity entity) {
        this.entity = entity;
    }

    LoginEntity entity;

    private MutableLiveData<String> emailErrorStatus = new MutableLiveData<>();

    public void setEmailErrorStatus(MutableLiveData<String> emailErrorStatus) {
        this.emailErrorStatus = emailErrorStatus;
    }

    public MutableLiveData<String> getEmailErrorStatus() {
        return emailErrorStatus;
    }

    public void setPasswordErrorStatus(MutableLiveData<String> passwordErrorStatus) {
        this.passwordErrorStatus = passwordErrorStatus;
    }

    private MutableLiveData<String> passwordErrorStatus = new MutableLiveData<>();

    public MutableLiveData<String> getPasswordErrorStatus() {
        return passwordErrorStatus;
    }

    public boolean onEmailInputChanged() {
        this.emailErrorStatus.setValue(null); // reset the errorStatus so error disappears once validated (e.g. enough letters entered)
        return false; // return false so other listeners can process the event
    }

    public boolean onPasswordInputChanged() {
        this.passwordErrorStatus.setValue(null);
        return false;
    }

    /**
     * Check input in email field after user has clicked on next (not every input)
     *
     * @param actionId the editor action that has triggered this method from the databinding
     * @return true, so that the
     */
    public boolean isEmailInputValid(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            String enteredEmailAddress = entity.getEnteredEmail();
            if (enteredEmailAddress.isEmpty()) {
                this.emailErrorStatus.setValue("Email may not be empty");
                return true;
            }else if(!Patterns.EMAIL_ADDRESS.matcher(enteredEmailAddress).matches()){
                this.emailErrorStatus.setValue("Not a valid email pattern");
                return true;
            }
            else {
                Log.i(LOG_TAG, "Valid email entered");
                return false;
            }
        }
        return true; // false = focus can skip to next input field even if error, true = focus stays on field if error
    }

    public boolean isPasswordInputValid(int actionId){
        String regexOnlyNumbers = "^[0-9]*$";
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            if (entity.getEnteredPassword().isEmpty()) {
                this.passwordErrorStatus.setValue("Password may not be empty!");
                return true;
            }
            else if(entity.getEnteredPassword().length() != 6){
                this.passwordErrorStatus.setValue("Password must be of length 6!");
                return true;
            }
            else if(!entity.getEnteredPassword().matches(regexOnlyNumbers)){
                this.passwordErrorStatus.setValue("Password may only contain numbers!");
                return true;
            }
            else{
                Log.i(LOG_TAG, "Valid password entered!");
                return false;
            }
        }
        //false = focus can skip to next input field even if error, true = focus stays on field if error
        return true;
    }
}
