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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.julheinz.data.TaskCrudManager;
import org.julheinz.entities.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {
    private TaskCrudManager taskCrudManager;
    private static final int CALL_DETAIL_VIEW_FOR_EDIT = 20;
    private static final int CALL_DETAIL_VIEW_FOR_CREATE = 30;
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
     * Happens after the activity that was called with startActivityForResult() finishes.
     *
     * @param requestCode identifies a call with a use case e.g. 20 = CALL_DETAIL_VIEW_FOR_EDIT
     * @param resultCode  state of the result e.g. RESULT_OK
     * @param data        data that was added with putExtra() in the called activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CALL_DETAIL_VIEW_FOR_EDIT) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(LOG_TAG, "Successfully received edited task from DetailView");
                    TaskEntity receivedTask = (TaskEntity)data.getSerializableExtra(ARG_TASK);
                    new Thread(() -> {
                        this.taskCrudManager.updateTask(receivedTask);
                        this.runOnUiThread(() -> listViewAdapter.add(receivedTask)); //go back to ui thread
                    }).start();
                    toastMsg("Edited " + receivedTask.getTitle());
                    break;
                case Activity.RESULT_CANCELED:
                    toastMsg("Edits were not saved");
                    break;
            }
        } else if (requestCode == CALL_DETAIL_VIEW_FOR_CREATE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i(LOG_TAG, "Successfully received edited task from DetailView");
                TaskEntity receivedTask = (TaskEntity)data.getSerializableExtra(ARG_TASK);
                new Thread(() -> { //new thread for database access
                    TaskEntity createdTask = this.taskCrudManager.createTask(receivedTask);

                    this.runOnUiThread(() -> this.listViewAdapter.add(createdTask)); //go back to ui thread
                }).start();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data); //if request code is something else, let method in parent class handle it
        }
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
        //starts a new activity from which we want to get a result
        startActivityForResult(detailviewIntent, CALL_DETAIL_VIEW_FOR_CREATE);
    }

    /**
     * Starts DetailActivity for result after click on existing task
     */
    private void callDetailViewForEdit(TaskEntity selectedItem) {
        Intent callDetailViewForEdit = new Intent(this, DetailViewActivity.class);
        callDetailViewForEdit.putExtra(ARG_TASK, selectedItem);
        startActivityForResult(callDetailViewForEdit, CALL_DETAIL_VIEW_FOR_EDIT);
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

}