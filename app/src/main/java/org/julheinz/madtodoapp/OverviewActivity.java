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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.julheinz.data.TaskCrudManager;
import org.julheinz.entities.TaskEntity;
import org.julheinz.viewmodel.OverviewViewModel;

public class OverviewActivity extends AppCompatActivity {

    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();

    private ArrayAdapter<TaskEntity> listViewAdapter;
    private ProgressBar progressBar;

    private OverviewViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        FloatingActionButton addTaskBtn = findViewById(R.id.saveBtn);
        addTaskBtn.setOnClickListener(this::callDetailViewForCreate);

        //instantiate the view model or reuse if already exists
        viewModel = new ViewModelProvider(this).get(OverviewViewModel.class);

        ListView listView = findViewById(R.id.listView); //listview element in overview_activity.xml = container for list
        this.listViewAdapter = new TaskListAdapter(this, R.id.listView, viewModel.getTasks(), this.getLayoutInflater()); //instantiate adapter
        listView.setAdapter(this.listViewAdapter);

        /// click listener for click on task in list. arguments:
        listView.setOnItemClickListener(clickOnList());

        this.progressBar = findViewById(R.id.progressBar); //show progressbar while loading of data

        TaskCrudManager taskCrudManager = new TaskCrudManager(this); //manages data base operations
        viewModel.setCrudOperations(taskCrudManager);

        //änderungen auf mutablelivedata beobachten diese klasse ist der observer vom wert von processingstate
        viewModel.getProcessingState().observe(this, processingState -> {
            //das hier wird auf dem ui thread gemacht
            switch (processingState) {
                case RUNNING:
                    break;
                case RUNNING_LONG:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case DONE:
                    this.progressBar.setVisibility(View.GONE);
                    this.listViewAdapter.notifyDataSetChanged();
                    break;
            }
        });

        if (this.viewModel.isInitial()) { //if there isn't already a view model, the data has to be loaded from here
            this.progressBar.setVisibility(View.VISIBLE);
            this.viewModel.readAllTasks();
            this.viewModel.setInitial(false);
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
            viewModel.createTask(receivedTask);

        }
    });

    public ActivityResultLauncher<Intent> detailViewForEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
        switch (activityResultObject.getResultCode()) {
            case Activity.RESULT_OK:
                Log.i(LOG_TAG, "Successfully received edited task from DetailView");
                TaskEntity editedTask = (TaskEntity) activityResultObject.getData().getSerializableExtra(ARG_TASK);
                this.viewModel.updateTask(editedTask);
                toastMsg("Edited " + editedTask.getTitle());
                break;
            case Activity.RESULT_CANCELED:
                toastMsg("Edits were not saved");
                break;
        }
    });

}