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

    private final MutableLiveData<Boolean> inputsValid = new MutableLiveData<>(false);
    private final RetrofitUserOperations userOperations = new RetrofitUserOperations();
    private final ExecutorService operationRunner = Executors.newFixedThreadPool(4);

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


    public boolean onEmailInputChanged() {
        if(entity.getAuthErrorState() == LoginEntity.AuthErrorState.FAILURE){
            entity.setAuthErrorState(LoginEntity.AuthErrorState.BEFORE_ATTEMPT);
        }
        entity.setEmailErrorState(LoginEntity.EmailErrorState.NOT_VALIDATED);
        entityLiveData.setValue(entity);
        return false;
    }

    public boolean onPasswordInputChanged() {
        if(entity.getAuthErrorState() == LoginEntity.AuthErrorState.FAILURE){
            entity.setAuthErrorState(LoginEntity.AuthErrorState.BEFORE_ATTEMPT);
        }
        entity.setPwErrorState(LoginEntity.PwErrorState.NOT_VALIDATED);
        entityLiveData.setValue(entity);
        return false;
    }


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
                getInputsValid();
                return false;
            }
        }
        return true;
    }


    public boolean checkPasswordInputInvalid(int actionId) {
        checkEmailInputInvalid(actionId);
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
                getInputsValid();
                return false;
            }
        }
        return true;
    }


    public MutableLiveData<Boolean> getInputsValid() {
        if(entity.getPwErrorState() == LoginEntity.PwErrorState.VALID && entity.getEmailErrorState() == LoginEntity.EmailErrorState.VALID){
            inputsValid.setValue(true);
        }else{
            inputsValid.setValue(false);
        }
        return inputsValid;
    }


    public void onLoginButtonClick() {
        if ( checkEmailInputInvalid(6) ||  checkPasswordInputInvalid(6)) {
            Log.i(LOG_TAG, "One or both inputs invalid");
        } else {
            Log.i(LOG_TAG, "Both inputs valid, trying to log in");
            UserEntity userEntity = new UserEntity(entity.getEnteredPassword(), entity.getEnteredEmail());
            authenticateUser(userEntity);
        }
    }


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
