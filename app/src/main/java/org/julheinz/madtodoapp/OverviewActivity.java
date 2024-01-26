package org.julheinz.madtodoapp;

import static org.julheinz.madtodoapp.DetailViewActivity.ARG_TASK;
import static org.julheinz.viewmodel.OverviewViewModel.SORT_BY_DONE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.julheinz.data.TaskCrudOperations;
import org.julheinz.entities.TaskEntity;
import org.julheinz.viewmodel.OverviewViewModel;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Activity for overview over tasks. Data is not directly handled here but delegated to OverviewViewModel.
 * It observes the view model and controls the UI.
 */
public class OverviewActivity extends AppCompatActivity {

    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private ArrayAdapter<TaskEntity> listViewAdapter;
    private ProgressBar progressBar;
    private OverviewViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        org.julheinz.madtodoapp.databinding.OverviewActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.overview_activity);
        binding.setActivity(this);

        viewModel = new ViewModelProvider(this).get(OverviewViewModel.class); // instantiate the view model or reuse if already exists

        ListView listView = findViewById(R.id.listView); // listview element in overview_activity.xml = container for list
        this.listViewAdapter = new TaskListAdapter(this, R.id.listView, viewModel.getTasks(), this.getLayoutInflater(), viewModel); //instantiate adapter
        listView.setAdapter(this.listViewAdapter);
        listView.setOnItemClickListener(clickOnList()); // click listener for tasks in list

        this.progressBar = findViewById(R.id.progressBar); // show progressbar while loading of data

        Future<TaskCrudOperations> crudOperationsFuture = ((TaskApplication) getApplication()).getCrudOperations(); //at some point a TaskCrudOperations Object can be read from this
        TaskCrudOperations taskCrudOperations;
        try {
            taskCrudOperations = crudOperationsFuture.get(); //get waits until the other thread is done and the future obj has a value

            viewModel.setCrudOperations(taskCrudOperations); //connect view model to crudOperations

            viewModel.getProcessingState().observe(this, processingState -> { // Observe changes on MutableLiveData, act according to its processing state
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

            if (this.viewModel.isInitial()) { //if there isn't already a view model, get data from db
                this.progressBar.setVisibility(View.VISIBLE);
                if(((TaskApplication) getApplication()).isInOfflineMode()){
                    showSnackbar("Offline! Changes will not be synced to server.");

                    //TODO: Put this in log in view
                    //Skip login view and go to OverviewActivity
                    //startActivity(new Intent(this, OverviewActivity.class));

                }
                this.viewModel.readAllTasks();
                this.viewModel.syncDatabases();
                this.viewModel.setInitial(false);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Toolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.sortItems) {
                if (viewModel.getCurrentSortMode() == OverviewViewModel.SORT_BY_DONE) {
                    showSnackbar("Sorting items by favorite then date");
                    viewModel.setCurrentSortMode(OverviewViewModel.SORT_FAV_DUE);
                } else {
                    showSnackbar("Sorting items by done status");
                    viewModel.setCurrentSortMode(SORT_BY_DONE);
                }
                viewModel.sortTasksAfterUserInput();
                return true;
            }else if(item.getItemId() == R.id.addTask){
                callDetailViewForCreate();
                return true;
            }
            else if (item.getItemId() == R.id.deleteAllLocal) {
                showSnackbar("Local data was deleted.");
                viewModel.deleteAllLocalTasks();
                return true;
            } else if (item.getItemId() == R.id.deleteAllRemote) {
                String message = viewModel.deleteAllRemoteTasks();
                showSnackbar(message);
                return true;
            } else if (item.getItemId() == R.id.syncDB) {
                String message =  viewModel.syncDatabases();
                showSnackbar(message);
                return true;
            } else {
                return false;
            }
        });
    }


    private void showSnackbar(String msg) {
        Snackbar.make(findViewById(R.id.listView), msg, Snackbar.LENGTH_SHORT).show();
    }



    /**
     * Starts DetailActivity for result after click on new task button
     */
    public void callDetailViewForCreate() {
        Intent detailviewIntent = new Intent(Intent.ACTION_INSERT, null, this, DetailViewActivity.class);
        detailViewForCreateLauncher.launch(detailviewIntent); //launch a new activity from which we want to get a result
    }

    /**
     * Starts DetailActivity for result after click on existing task
     */
    private void callDetailViewForEdit(TaskEntity selectedItem) {
        Intent callDetailViewForEdit = new Intent(Intent.ACTION_EDIT, null, this, DetailViewActivity.class);
        callDetailViewForEdit.putExtra(ARG_TASK, selectedItem);
        detailViewForEditLauncher.launch(callDetailViewForEdit);
    }

    /**
     * returns click listener for list item which gets the taskEntity for the selected item and triggers an edit on it.
     */
    private AdapterView.OnItemClickListener clickOnList() {
        return (parent, view, position, id) -> {
            //get the TaskEntity from the data list that corresponds to the view position in the ListView
            TaskEntity selectedTask = listViewAdapter.getItem(position);
            callDetailViewForEdit(selectedTask);
        };
    }

    public final ActivityResultLauncher<Intent> detailViewForCreateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
        if (activityResultObject.getResultCode() == Activity.RESULT_OK) {
            Log.i(LOG_TAG, "Successfully received edited task from DetailView");
            TaskEntity receivedTask = (TaskEntity) activityResultObject.getData().getSerializableExtra(ARG_TASK);
            viewModel.createTask(receivedTask);
        }
    });

    public ActivityResultLauncher<Intent> detailViewForEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
        String action = activityResultObject.getData().getAction();
        switch (activityResultObject.getResultCode()) {
            case Activity.RESULT_OK:
                TaskEntity returnedTask = (TaskEntity) activityResultObject.getData().getSerializableExtra(ARG_TASK);
                // differenciate if detailview activity finished because the task was deleted or if it was edited
                if (Objects.equals(action, "android.intent.action.DELETE")) {
                    this.viewModel.deleteTask(returnedTask);
                    showSnackbar("Deleted " + returnedTask.getTitle());
                } else if (Objects.equals(action, "android.intent.action.EDIT")) {
                    this.viewModel.updateTask(returnedTask);
                    showSnackbar("Edited " + returnedTask.getTitle());
                }
                break;
            case Activity.RESULT_CANCELED:
                showSnackbar("Edits were not saved");
                break;
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_overview_menu, menu);
        return true;
    }

}