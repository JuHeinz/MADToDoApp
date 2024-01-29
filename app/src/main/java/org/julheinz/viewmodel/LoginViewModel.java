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

/**
 * ViewModel for LoginActivity. Validates user inputs and triggers authentication.
 * Exposes validation states via MutableLive data so activity can react.
 */
public class LoginViewModel extends ViewModel {
    private static final String LOG_TAG = LoginViewModel.class.getSimpleName();

    private final MutableLiveData<Boolean> inputsValid = new MutableLiveData<>(false);
    private final RetrofitUserOperations userOperations = new RetrofitUserOperations();
    private final ExecutorService operationRunner = Executors.newFixedThreadPool(4); // smart thread management

    public MutableLiveData<LoginEntity> getEntityLiveData() {
        return entityLiveData;
    }

    private final MutableLiveData<LoginEntity> entityLiveData = new MutableLiveData<>();
    private LoginEntity entity;
    public LoginEntity getEntity() {
        return entity;
    }

    public void setEntity(LoginEntity entity) {
        this.entity = entity;
        entityLiveData.setValue(entity);
    }

    /**
     * Once user types, reset the email error state and authentication error state.
     * @return false so other listeners can process the event
     */
    public boolean onEmailInputChanged() {
        if(entity.getAuthErrorState() == LoginEntity.AuthErrorState.FAILURE){ //if there was previously and authentication error, reset it
            entity.setAuthErrorState(LoginEntity.AuthErrorState.BEFORE_ATTEMPT);
        }
        entity.setEmailErrorState(LoginEntity.EmailErrorState.NOT_VALIDATED);
        entityLiveData.setValue(entity);
        return false;
    }
    /**
     * Once user types, reset the password error state and authentication error state.
     * @return false so other listeners can process the event
     */
    public boolean onPasswordInputChanged() {
        if(entity.getAuthErrorState() == LoginEntity.AuthErrorState.FAILURE){ //if there was previously an authentication error, reset it
            //TODO: this also resets on rotate, making the error disappear on rotate
            entity.setAuthErrorState(LoginEntity.AuthErrorState.BEFORE_ATTEMPT);
        }
        entity.setPwErrorState(LoginEntity.PwErrorState.NOT_VALIDATED);
        entityLiveData.setValue(entity);
        return false;
    }

    /**
     * Check if the entered email is invalid
     *
     * @param actionId the editor action that has triggered this method from the data binding
     * @return true if email is *in*valid (or the editor action was not done or next) so focus can skip to next input field
     */
    public boolean checkEmailInputInvalid(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            String enteredEmailAddress = entity.getEnteredEmail();
            if (enteredEmailAddress.isEmpty()) {
                entity.setEmailErrorState(LoginEntity.EmailErrorState.EMPTY);
                entityLiveData.setValue(entity);
                return true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(enteredEmailAddress).matches()) {
                entity.setEmailErrorState(LoginEntity.EmailErrorState.INVALID_PATTERN);
                entityLiveData.setValue(entity);
                return true;
            } else {
                Log.i(LOG_TAG, "Valid email entered");
                entity.setEmailErrorState(LoginEntity.EmailErrorState.VALID);
                entityLiveData.setValue(entity);
                getInputsValid(); //trigger check if both inputs are valid now
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the entered password is invalid
     *
     * @param actionId the editor action that has triggered this method from the data binding
     * @return true if password is *in*valid (or the editor action was not done or next) so focus can skip to next input field
     */
    public boolean checkPasswordInputInvalid(int actionId) {
        checkEmailInputInvalid(actionId); //check email input again in case user left email input without clicking next
        String regexOnlyNumbers = "^[0-9]*$";
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            if (entity.getEnteredPassword().isEmpty()) {
                entity.setPwErrorState(LoginEntity.PwErrorState.EMPTY);
                entityLiveData.setValue(entity);
                return true;
            } else if (entity.getEnteredPassword().length() != 6) {
                entity.setPwErrorState(LoginEntity.PwErrorState.NOT_SIX);
                entityLiveData.setValue(entity);
                return true;
            } else if (!entity.getEnteredPassword().matches(regexOnlyNumbers)) {
                entity.setPwErrorState(LoginEntity.PwErrorState.NOT_NUM);
                entityLiveData.setValue(entity);
                return true;
            } else {
                entity.setPwErrorState(LoginEntity.PwErrorState.VALID);
                entityLiveData.setValue(entity);
                Log.i(LOG_TAG, "Valid password entered!");
                getInputsValid(); //trigger check if both inputs are valid now
                return false;
            }
        }
        return true;
    }

    /**
     * Check if both inputs are valid.
     * @return true only of both inputs are valid
     */
    public MutableLiveData<Boolean> getInputsValid() {
        if(entity.getPwErrorState() == LoginEntity.PwErrorState.VALID && entity.getEmailErrorState() == LoginEntity.EmailErrorState.VALID){
            inputsValid.setValue(true);
        }else{
            inputsValid.setValue(false);
        }
        return inputsValid;
    }

    /**
     * Trigger after button click authentication if both inputs are valid.
     */
    public void onLoginButtonClick() {
        //check again if both fields have valid input in case user left fields without clicking next or done
        if ( checkEmailInputInvalid(6) ||  checkPasswordInputInvalid(6)) {
            Log.i(LOG_TAG, "One or both inputs invalid");
        } else {
            Log.i(LOG_TAG, "Both inputs valid, trying to log in");
            UserEntity userEntity = new UserEntity(entity.getEnteredPassword(), entity.getEnteredEmail());
            authenticateUser(userEntity);
        }
    }

    /**
     * Call http request to authenticate user to web server and receive the response.
     * Set authentication status accordingly.
     * @param user password and email to be authenticated.
     */
    private void authenticateUser(UserEntity user) {
        entity.setAuthErrorState(LoginEntity.AuthErrorState.WAITING);
        entityLiveData.setValue(entity);
        operationRunner.execute(() -> {
            boolean isAuthenticated = userOperations.authenticate(user);
            Log.i(LOG_TAG, "Trying to authenticate user...");
            try {
                Thread.sleep(2000); //delay for exam purposes
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Log.i(LOG_TAG, "Authentication successful? " + isAuthenticated);
            if (isAuthenticated) {
                entity.setAuthErrorState(LoginEntity.AuthErrorState.SUCCESS);
                entityLiveData.postValue(entity);
            } else {
                entity.setAuthErrorState(LoginEntity.AuthErrorState.FAILURE);
                entityLiveData.postValue(entity);
            }
        });
    }
}
