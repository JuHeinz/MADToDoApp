package org.julheinz.madtodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import org.julheinz.data.RoomTaskCrudOperations;
import org.julheinz.data.TaskCrudOperations;
import org.julheinz.madtodoapp.databinding.LoginActivityBinding;
import org.julheinz.viewmodel.LoginViewModel;

import java.util.concurrent.Future;

public class LogInActivity extends AppCompatActivity {
    private static final String LOG_TAG = LogInActivity.class.getSimpleName();

    private LoginViewModel viewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        this.viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewmodel(viewModel);


        this.viewModel.getEmailErrorStatus().observe(this, status ->{
            Log.i(LOG_TAG, "Email Status is: " + status);
        });

        this.viewModel.getPasswordErrorStatus().observe(this, status ->{
            Log.i(LOG_TAG, "Password Status is: " + status);
        });

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

    }


}
