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

import com.google.android.material.textfield.TextInputLayout;

import org.julheinz.data.RoomTaskCrudOperations;
import org.julheinz.data.TaskCrudOperations;
import org.julheinz.entities.LoginEntity;
import org.julheinz.madtodoapp.databinding.LoginActivityBinding;
import org.julheinz.viewmodel.LoginViewModel;

import java.util.concurrent.Future;

/**
 * Activity for user to log in.
 */
public class LogInActivity extends AppCompatActivity {
    private static final String LOG_TAG = LogInActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private LinearLayout authErrorMsg;
    private TextInputLayout pwField;
    private TextInputLayout emailField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setActivity(this);
        binding.setLifecycleOwner(this); // make it so data binding and view model can communicate

        LoginEntity loginEntity;
        if(viewModel.getEntity() == null){
            loginEntity = new LoginEntity();
            viewModel.setEntity(loginEntity);
        }else{
            Log.d(LOG_TAG, "previous loginEntity onCreate is:" + viewModel.getEntity());
        }

        this.authErrorMsg = findViewById(R.id.authErrorMsg);
        this.progressBar = findViewById(R.id.progressBar);
        this.pwField = findViewById(R.id.pwField);
        this.emailField = findViewById(R.id.emailField);

        //DETERMINE ONLINE STATUS
        Future<TaskCrudOperations> crudOperationsFuture = ((TaskApplication) getApplication()).getCrudOperations(); //at some point a TaskCrudOperations Object can be read from this
        TaskCrudOperations taskCrudOperations;
        try {
            taskCrudOperations = crudOperationsFuture.get(); //get waits until the other thread is done and the future obj has a value
            if(taskCrudOperations instanceof RoomTaskCrudOperations){
                Log.i(LOG_TAG,"Offline! Skipping log in.");
                // If offline, skip login view and go to OverviewActivity
                startActivity(new Intent(this, OverviewActivity.class));
            }else{
                Log.i(LOG_TAG,"Online, please log in.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        viewModel.getInputsValid().observe(this, isValid ->{ // Observe changes to the field in the view model that is true when both inputs are valid
            Log.i(LOG_TAG, "Both inputs valid? " + isValid);
            Button btn = this.findViewById(R.id.logInBtn);
            btn.setEnabled(isValid); // if both inputs are valid, enable the log in button
        });

        viewModel.getEntityLiveData().observe(this, entity -> { // Observe changes on LoginEntity MutableLiveData
            checkEmailErrorState(entity);
            checkPwErrorState(entity);
            checkAuthErrorState(entity);
        });
    }

    /**
     * Get the validation status of the password input and change UI accordingly
     */
    public void checkPwErrorState(LoginEntity entity) {
        switch (entity.getPwErrorState()){
            case EMPTY:
                pwField.setError("Password may not be empty");
                break;
            case NOT_SIX:
                pwField.setError("Password must be of length 6");
                break;
            case NOT_NUM:
                pwField.setError("Password must include numbers only");
                break;
            case VALID:
            case NOT_VALIDATED:
            default:
                pwField.setErrorEnabled(false);
        }
    }

    /**
     * Get the validation status of the email input and change UI accordingly
     */
    private void checkEmailErrorState(LoginEntity entity) {
        switch (entity.getEmailErrorState()){
            case EMPTY:
                emailField.setError("Email may not be empty");
                break;
            case INVALID_PATTERN:
                emailField.setError("Email pattern invalid");
                break;
            case VALID:
            case NOT_VALIDATED:
            default:
                emailField.setErrorEnabled(false);

        }
    }

    /**
     * Get the log in authentication status and change UI accordingly
     */
    public void checkAuthErrorState(LoginEntity entity){
        switch (entity.getAuthErrorState()){
            case WAITING:
                this.authErrorMsg.setVisibility(View.GONE);
                this.progressBar.setVisibility(View.VISIBLE);
                break;
            case FAILURE:
                this.authErrorMsg.setVisibility(View.VISIBLE);
                this.progressBar.setVisibility(View.GONE);
                break;
            case SUCCESS:
                startActivity(new Intent(this, OverviewActivity.class));
                this.authErrorMsg.setVisibility(View.GONE);
                this.progressBar.setVisibility(View.GONE);
                break;
            //reset error on new user input / before login attempt
            case BEFORE_ATTEMPT:
            default:
                this.authErrorMsg.setVisibility(View.GONE);
                this.progressBar.setVisibility(View.GONE);
        }
    }

}
