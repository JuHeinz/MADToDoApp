package org.julheinz.viewmodel;

import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.data.RetrofitUserOperations;
import org.julheinz.entities.LoginEntity;
import org.julheinz.entities.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {
    private static final String LOG_TAG = LoginViewModel.class.getSimpleName();

    public MutableLiveData<Boolean> getWaitingForAuthenticate() {
        return waitingForAuthenticate;
    }

    public MutableLiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> waitingForAuthenticate = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> inputsValid = new MutableLiveData<>(false);
    private boolean isEmailInputValid = false;
    private boolean isPasswordInputValid = false;
    private final RetrofitUserOperations userOperations = new RetrofitUserOperations();

    private final ExecutorService operationRunner = Executors.newFixedThreadPool(4); // smart thread management



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
     * check if the entered email is invalid
     * @param actionId the editor action that has triggered this method from the databinding
     * @return true if email is *in*valid (or the editor action was not done or next)
     */
    public boolean checkEmailInputInvalid(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            String enteredEmailAddress = entity.getEnteredEmail();
            if (enteredEmailAddress.isEmpty()) {
                this.emailErrorStatus.setValue("Email may not be empty");
                setPasswordInputValid(false);
                return true;
            }else if(!Patterns.EMAIL_ADDRESS.matcher(enteredEmailAddress).matches()){
                this.emailErrorStatus.setValue("Not a valid email pattern");
                setPasswordInputValid(false);
                return true;
            }
            else {
                Log.i(LOG_TAG, "Valid email entered");
                setEmailInputValid(true);
                getInputsValid();
                return false;
            }
        }
        return true; // false = focus can skip to next input field even if error, true = focus stays on field if error
    }

    /**
     * check if the entered password is invalid
     * @param actionId the editor action that has triggered this method from the databinding
     * @return true if password is *in*valid (or the editor action was not done or next)
     */
    public boolean checkPasswordInputInvalid(int actionId){
        checkEmailInputInvalid(actionId); //check email input again in case user left email input without clicking next
        String regexOnlyNumbers = "^[0-9]*$";
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            if (entity.getEnteredPassword().isEmpty()) {
                this.passwordErrorStatus.setValue("Password may not be empty!");
                setPasswordInputValid(false);
                return true;
            }
            else if(entity.getEnteredPassword().length() != 6){
                this.passwordErrorStatus.setValue("Password must be of length 6!");
                setPasswordInputValid(false);
                return true;
            }
            else if(!entity.getEnteredPassword().matches(regexOnlyNumbers)){
                this.passwordErrorStatus.setValue("Password may only contain numbers!");
                setPasswordInputValid(false);
                return true;
            }
            else{
                Log.i(LOG_TAG, "Valid password entered!");
                setPasswordInputValid(true);
                return false;
            }
        }
        //false = focus can skip to next input field even if error, true = focus stays on field if error
        return true;
    }

    /**
     * Returns a mutable live data of type boolean if both inputs are valid
     */
    public MutableLiveData<Boolean> getInputsValid() {
        boolean result = isEmailInputValid && isPasswordInputValid;
        Log.i(LOG_TAG, "Both inputs valid? " + result);
        inputsValid.setValue(result);
        return inputsValid;
    }

    public void setEmailInputValid(boolean emailInputValid) {
        isEmailInputValid = emailInputValid;
        getInputsValid();
    }


    public void setPasswordInputValid(boolean passwordInputValid) {
        isPasswordInputValid = passwordInputValid;
        getInputsValid();
    }

    public void onLoginButtonClick(){
        //check again if both fields have valid input in case user left fields without clicking next or done
        boolean pwInvalid = checkPasswordInputInvalid(6);
        boolean emailInvalid = checkEmailInputInvalid(6);
        if(emailInvalid || pwInvalid){
            Log.i(LOG_TAG, "One or both inputs invalid");

        }else {
            Log.i(LOG_TAG, "Both inputs valid, trying to log in");
            UserEntity userEntity = new UserEntity(entity.getEnteredPassword(), entity.getEnteredEmail());
            authenticateUser(userEntity);
        }

    }

    public void authenticateUser(UserEntity user) {
        waitingForAuthenticate.setValue(true);
        operationRunner.execute(() -> {
            boolean isAuthenticated = userOperations.authenticate(user);
            Log.i(LOG_TAG, "Trying to authenticate user...");
            try {
                Thread.sleep(2000); //delay for exam purposes
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Log.i(LOG_TAG, "Authentication successful? " + isAuthenticated);
            if(isAuthenticated){
                loginSuccess.postValue(true);
            }else {
                loginSuccess.postValue(false);
            }
            waitingForAuthenticate.postValue(false);
        });
    }
}
