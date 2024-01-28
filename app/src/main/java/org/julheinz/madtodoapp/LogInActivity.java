package org.julheinz.madtodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;


import org.julheinz.data.RoomTaskCrudOperations;
import org.julheinz.data.TaskCrudOperations;
import org.julheinz.entities.LoginEntity;
import org.julheinz.madtodoapp.databinding.LoginActivityBinding;
import org.julheinz.viewmodel.LoginViewModel;

import java.util.concurrent.Future;

public class LogInActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private LinearLayout authErrorMsg;

    public String getEmailErrorMessage() {
        return emailErrorMessage;
    }

    public String getPwErrorMessage() {
        return pwErrorMessage;
    }

    public String emailErrorMessage ="";
    public String pwErrorMessage ="";
    private static final String LOG_TAG = LogInActivity.class.getSimpleName();
    LoginEntity loginEntity;
    private LoginViewModel viewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        this.viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setActivity(this);
        binding.setLifecycleOwner(this); // make it so databinding and viewmodel can communicate
        if(viewModel.getEntity() == null){
            this.loginEntity = new LoginEntity();
            this.viewModel.setEntity(loginEntity);
        }else{
            this.loginEntity = viewModel.getEntity();
        }

        this.authErrorMsg = findViewById(R.id.authErrorMsg);
        this.progressBar = findViewById(R.id.progressBar);


        //DETERMINE ONLINE STATUS
        Future<TaskCrudOperations> crudOperationsFuture = ((TaskApplication) getApplication()).getCrudOperations(); //at some point a TaskCrudOperations Object can be read from this
        TaskCrudOperations taskCrudOperations;
        try {
            taskCrudOperations = crudOperationsFuture.get(); //get waits until the other thread is done and the future obj has a value
            if(taskCrudOperations instanceof RoomTaskCrudOperations){
                Log.i(LOG_TAG,"Offline! Skipping log in.");
                //If offline, skip login view and go to OverviewActivity
                startActivity(new Intent(this, OverviewActivity.class));
            }else{
                Log.i(LOG_TAG,"Online, please log in.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // INPUT VALIDATION
        viewModel.getInputsValid().observe(this, isValid ->{
            Log.i(LOG_TAG, "Both inputs valid? " + isValid);
            Button btn = this.findViewById(R.id.logInBtn);
            btn.setEnabled(isValid);
        });


        viewModel.getEntityLiveData().observe(this, entity -> { // Observe changes on LoginEntity MutableLiveData, act according to its state
            Log.i(LOG_TAG, "Observed changes in live data");

            checkEmailErrorState(entity);
            checkPwErrorState(entity);
            checkAuthErrorState(entity);
        });
    }

    public void checkPwErrorState(LoginEntity entity) {
        switch (entity.getPwErrorState()){
            case EMPTY:
                this.pwErrorMessage = "Password may not be empty";
                break;
            case NOT_SIX:
                this.pwErrorMessage = "Password must be of length 6";
                break;
            case NOT_NUM:;
                this.pwErrorMessage = "Password include numbers only";
                break;
            case VALID:
            case NOT_VALIDATED:
            default:
                this.pwErrorMessage="";
        }
        Log.i(LOG_TAG, pwErrorMessage);
    }

    private void checkEmailErrorState(LoginEntity entity) {
        switch (entity.getEmailErrorState()){
            case EMPTY:
                this.emailErrorMessage = "Email may not be empty";
                break;
            case INVALID_PATTERN:
                this.emailErrorMessage = "Email pattern invalid";
                break;
            case VALID:
            case NOT_VALIDATED:
            default:
                this.emailErrorMessage="";
        }
        Log.i(LOG_TAG, emailErrorMessage);

    }

    public void checkAuthErrorState(LoginEntity entity){
        if(entity.getAuthErrorState() == LoginEntity.AuthErrorState.WAITING) {
            Log.i(LOG_TAG, "Waiting for authentication");
            progressBar.setVisibility(View.VISIBLE);
        }else if(entity.getAuthErrorState() == LoginEntity.AuthErrorState.FAILURE){
            this.authErrorMsg.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, "Password or username is wrong!");
            this.progressBar.setVisibility(View.GONE);
        }else if(entity.getAuthErrorState() == LoginEntity.AuthErrorState.SUCCESS){
            startActivity(new Intent(this, OverviewActivity.class));
        }
        else {
            this.authErrorMsg.setVisibility(View.GONE);
            Log.i(LOG_TAG, "Before authentication attempt");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "loginEntity is:" + this.viewModel.getEntity());
    }
}
