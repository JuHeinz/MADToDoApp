package org.julheinz.madtodoapp;

import static org.julheinz.madtodoapp.DetailViewActivity.ARG_TASK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.julheinz.data.TaskCrudManager;
import org.julheinz.entities.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {
    private TaskCrudManager taskCrudManager;

    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private final List<TaskEntity> taskList = new ArrayList<>();
    private ArrayAdapter<TaskEntity> listViewAdapter;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        FloatingActionButton addTaskBtn = findViewById(R.id.saveBtn);
        addTaskBtn.setOnClickListener(this::callDetailViewForCreate);

        ListView listView = findViewById(R.id.listView); //listview element in overview_activity.xml = container for list
        this.listViewAdapter = new TaskListAdapter(this, R.id.listView, taskList, this.getLayoutInflater()); //instantiate adapter
        listView.setAdapter(this.listViewAdapter);

        /// click listener for click on task in list. arguments:
        listView.setOnItemClickListener(clickOnList());

        this.progressBar = findViewById(R.id.progressBar); //show progressbar while loading of data
        this.progressBar.setVisibility(View.VISIBLE);

        taskCrudManager = new TaskCrudManager(this); //manages data base operations

        new Thread(() -> { //new thread for getting tasks from database

            List<TaskEntity> tasksFromDB = taskCrudManager.readAllTasks(); //get tasks from database

            this.runOnUiThread(() -> { //ui elements may only be edited in thread that created them.
                this.listViewAdapter.addAll(tasksFromDB); // show in list
                this.progressBar.setVisibility(View.GONE); //hide progressbar when loading of data done
            });
        }).start();

    }

    /**
     * Creates toast for user feedback
     */
    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Starts DetailActivity for result after click on new task button
     */
    private void callDetailViewForCreate(View view) {
        Intent detailviewIntent = new Intent(this, DetailViewActivity.class);
        //launch a new activity from which we want to get a result
        detailViewForCreateLauncher.launch(detailviewIntent);

    }

    /**
     * Starts DetailActivity for result after click on existing task
     */
    private void callDetailViewForEdit(TaskEntity selectedItem) {
        Intent callDetailViewForEdit = new Intent(this, DetailViewActivity.class);
        callDetailViewForEdit.putExtra(ARG_TASK, selectedItem);
        detailViewForEditLauncher.launch(callDetailViewForEdit);
    }

    /** 1: parent view where click happened
     2: item (view) auf das geklickt wurde
     3: position des elements in ansicht
     4: id für aufruf direkt auf datenbank (hier nicht verwendet)
     */
    private AdapterView.OnItemClickListener clickOnList() {
        return (parent, view, position, id) -> {
            //get the TaskEntity from the data list that corresponds to the view position in the ListView
            TaskEntity selectedTask = listViewAdapter.getItem(position);
            callDetailViewForEdit(selectedTask);
        };
    }

    public ActivityResultLauncher<Intent> detailViewForCreateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
        if (activityResultObject.getResultCode() == Activity.RESULT_OK) {
            Log.i(LOG_TAG, "Successfully received edited task from DetailView");
            TaskEntity receivedTask = (TaskEntity) activityResultObject.getData().getSerializableExtra(ARG_TASK);
            new Thread(() -> { //new thread for database access
                TaskEntity createdTask = taskCrudManager.createTask(receivedTask);

                runOnUiThread(() -> listViewAdapter.add(createdTask)); //go back to ui thread
            }).start();
        }
    });

    public ActivityResultLauncher<Intent> detailViewForEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
        switch (activityResultObject.getResultCode()) {
            case Activity.RESULT_OK:
                Log.i(LOG_TAG, "Successfully received edited task from DetailView");
                TaskEntity editedTask = (TaskEntity) activityResultObject.getData().getSerializableExtra(ARG_TASK);

                new Thread(() -> {
                    taskCrudManager.updateTask(editedTask);
                    //get the index of the editedTask in the task list
                    int index = this.taskList.indexOf(editedTask);
                    //replace TaskEntity at that position in the task list
                    this.taskList.set(index, editedTask);
                    runOnUiThread(() -> listViewAdapter.notifyDataSetChanged());
                }).start();
                toastMsg("Edited " + editedTask.getTitle());
                break;
            case Activity.RESULT_CANCELED:
                toastMsg("Edits were not saved");
                break;
        }
    });

}