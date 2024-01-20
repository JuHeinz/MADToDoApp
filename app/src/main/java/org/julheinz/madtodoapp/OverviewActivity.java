package org.julheinz.madtodoapp;

import static org.julheinz.madtodoapp.DetailViewActivity.ARG_TASK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.julheinz.data.TaskCrudOperations;
import org.julheinz.entities.TaskEntity;
import org.julheinz.viewmodel.OverviewViewModel;

import java.util.Objects;

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


        TaskCrudOperations taskCrudOperations = ((TaskApplication)getApplication()).getCrudOperations(); //connect view model to crudOperations
        viewModel.setCrudOperations(taskCrudOperations);

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
            this.viewModel.readAllTasks();
            this.viewModel.setInitial(false);
        }
    }

    /**
     * Creates toast for user feedback
     */
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
        Intent callDetailViewForEdit = new Intent(Intent.ACTION_EDIT, null,this, DetailViewActivity.class);
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

    public ActivityResultLauncher<Intent> detailViewForCreateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
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
                if(Objects.equals(action, "android.intent.action.DELETE")){
                    this.viewModel.deleteTask(returnedTask);
                    showSnackbar("Deleted " + returnedTask.getTitle());
                }else if(Objects.equals(action, "android.intent.action.EDIT")){
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.sortItems){
            showSnackbar("Sorting items selected");
            return true;
        } else if (item.getItemId() == R.id.deleteAllLocal){
            showSnackbar("Delete local items selected");
            return true;
        } else if (item.getItemId() == R.id.deleteAllRemote){
            showSnackbar("Delete remote items selected");
            return true;
        } else {
            showSnackbar("No valid item in menu selected");
            return super.onOptionsItemSelected(item);
        }
    }
}