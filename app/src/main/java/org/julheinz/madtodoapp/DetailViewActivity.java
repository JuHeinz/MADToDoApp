package org.julheinz.madtodoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.julheinz.entities.TaskEntity;
import org.julheinz.madtodoapp.databinding.DetailViewBinding;
import org.julheinz.viewmodel.DetailviewViewModel;

import java.util.Objects;

public class DetailViewActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener {
    private static final String LOG_TAG = DetailViewActivity.class.getSimpleName();
    private DetailViewBinding itemBinding; //class gets automatically generated by data binding library. named after xml file (detail_view.xml -> DetailViewBinding.java)
    public static final String ARG_TASK = "task";
    private DetailviewViewModel viewModel;
    public int doneCheckboxVisibility;
    public int deleteButtonVisibility;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.itemBinding = DataBindingUtil.setContentView(this, R.layout.detail_view); //bind this activity to detail_view.xml

        Intent detailViewIntentFromOverview = getIntent();
        String action = detailViewIntentFromOverview.getAction();

        // if action is called for creating a task, change layout
        if (Objects.equals(action, "android.intent.action.INSERT")) {
            prepareLayoutForCreate();
        }

        this.itemBinding.setLifecycleOwner(this); // enable observing of view model
        this.viewModel = new ViewModelProvider(this).get(DetailviewViewModel.class);
        this.itemBinding.setViewmodel(this.viewModel);
        this.itemBinding.setActivity(this);

        if (this.viewModel.getTaskEntity() == null) {
            //in case this activity gets called to edit a task, get the TaskEntity from intent
            TaskEntity taskFromIntent = (TaskEntity) detailViewIntentFromOverview.getSerializableExtra(ARG_TASK); // get TaskEntity (not same instance because it is serializable)
            TaskEntity task = taskFromIntent;
            if (taskFromIntent == null) { //in case this activity gets called to create a task instead of edit one, create a new TaskEntity
                task = new TaskEntity();
                Log.i(LOG_TAG, "created new task " + task);
            }
            this.viewModel.setTaskEntity(task);
            Log.i(LOG_TAG, "Giving task to viewmodel:" + viewModel.getTaskEntity().toString());
        }
    }

    public void saveTask() {
        Log.i(LOG_TAG, "Task saved:" + viewModel.getTaskEntity().toString());
        Intent returnToCallerWithValueIntent = new Intent(Intent.ACTION_EDIT);
        returnToCallerWithValueIntent.putExtra(ARG_TASK, viewModel.getTaskEntity()); //return created/updated task to calling activity
        setResult(Activity.RESULT_OK, returnToCallerWithValueIntent);
        finish(); //close activity and return to caller
    }

    public void cancelEdit() {
        Intent returnToCallerWhenCancelled = new Intent();
        setResult(Activity.RESULT_CANCELED, returnToCallerWhenCancelled);
        finish(); //close activity and return to caller
    }

    public void deleteTask() {
        Log.i(LOG_TAG, "Task deleted:" + viewModel.getTaskEntity().toString());
        Intent returnToCallerWithValueIntent = new Intent(Intent.ACTION_DELETE);
        returnToCallerWithValueIntent.putExtra(ARG_TASK, viewModel.getTaskEntity());
        setResult(Activity.RESULT_OK, returnToCallerWithValueIntent);
        finish(); //close activity and return to caller
    }

    /**
     * Show a dialog to confirm before deleting
     */
    public void confirmDeletionViaDialog() {
        DialogFragment dialogFragment = new DeleteDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "DeleteDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        deleteTask();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    /**
     * Show different elements if activity was called for task creation
     */
    public void prepareLayoutForCreate() {
        setDeleteButtonVisibility(View.GONE);
        setDoneCheckboxVisibility(View.GONE);
    }

    public int getDeleteButtonVisibility() {
        return deleteButtonVisibility;
    }

    public void setDeleteButtonVisibility(int deleteButtonVisibility) {
        this.deleteButtonVisibility = deleteButtonVisibility;
    }

    public int getDoneCheckboxVisibility() {
        return doneCheckboxVisibility;
    }

    public void setDoneCheckboxVisibility(int doneCheckboxVisibility) {
        this.doneCheckboxVisibility = doneCheckboxVisibility;
    }
}
