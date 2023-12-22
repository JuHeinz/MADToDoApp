package org.julheinz.madtodoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.julheinz.entities.TaskEntity;
import org.julheinz.madtodoapp.databinding.DetailViewBinding;

public class DetailViewActivity extends AppCompatActivity {
    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private DetailViewBinding itemBinding; //class gets automatically generated by data binding library. named after xml file (detail_view.xml -> DetailViewBinding.java)
    public static final String ARG_TASK = "task";
    private TaskEntity task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        this.itemBinding = DataBindingUtil.setContentView(this, R.layout.detail_view); //bind this activity to detail_view.xml
        this.itemBinding.setActivity(this);

        Intent detailViewIntentFromOverview = getIntent();
        //in case this activity gets called to edit a task, get the TaskEntity
        this.task = (TaskEntity) detailViewIntentFromOverview.getSerializableExtra(ARG_TASK); // get TaskEntity (not same instance because it is serializable)

        if (this.task == null) { //in case this activity gets called to create a task, create a new TaskEntity
            this.task = new TaskEntity();
            Log.i(LOG_TAG, "created new task " + this.task);
        } else {
            Log.i(LOG_TAG, "used existing task " + this.task);
        }
    }

    public void saveTask() {
        Log.i(LOG_TAG, "Task saved:" + task.toString());
        Intent returnToCallerWithValueIntent = new Intent();
        returnToCallerWithValueIntent.putExtra(ARG_TASK, this.task); //return created/updated task to calling activity
        setResult(Activity.RESULT_OK, returnToCallerWithValueIntent);
        finish(); //close activity and return to caller
    }

    public void cancelEdit() {
        Intent returnToCallerWhenCancelled = new Intent();
        setResult(Activity.RESULT_CANCELED, returnToCallerWhenCancelled);
        finish(); //close activity and return to caller
    }

    /**
     * Getter needed for dataBinding
     */
    public TaskEntity getTaskEntity() {
        return task;
    }

    /**
     * Setter needed for dataBinding
     */
    public void setTaskEntity(TaskEntity task) {
        this.task = task;
    }
}
