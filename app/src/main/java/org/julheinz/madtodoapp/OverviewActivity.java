package org.julheinz.madtodoapp;

import static org.julheinz.madtodoapp.DetailViewActivity.ARG_TASK;

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
import org.julheinz.listadapters.TaskListAdapter;
import org.julheinz.madtodoapp.databinding.OverviewActivityBinding;
import org.julheinz.viewmodel.OverviewViewModel;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class OverviewActivity extends AppCompatActivity {

    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private ArrayAdapter<TaskEntity> listViewAdapter;
    private ProgressBar progressBar;
    private OverviewViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OverviewActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.overview_activity);
        binding.setActivity(this);

        viewModel = new ViewModelProvider(this).get(OverviewViewModel.class);

        ListView listView = findViewById(R.id.listView);
        this.progressBar = findViewById(R.id.progressBar);

        this.listViewAdapter = new TaskListAdapter(this, R.id.listView, viewModel.getTasks(), this.getLayoutInflater(), viewModel);
        listView.setAdapter(this.listViewAdapter);
        listView.setOnItemClickListener(onTaskClickListener());

        Future<TaskCrudOperations> crudOperationsFuture = ((TaskApplication) getApplication()).getCrudOperations();
        TaskCrudOperations taskCrudOperations;
        try {
            taskCrudOperations = crudOperationsFuture.get();

            viewModel.setCrudOperations(taskCrudOperations);

            viewModel.getProcessingState().observe(this, processingState -> {
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

            if (this.viewModel.isInitial()) {
                this.progressBar.setVisibility(View.VISIBLE);
                if (((TaskApplication) getApplication()).isInOfflineMode()) {
                    showSnackbar("Offline! Changes will not be synced to server.");
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
                if (viewModel.getCurrentSortMode() == OverviewViewModel.SORT_DUE_FAV) {
                    showSnackbar("Sorting items by favorite then date");
                    viewModel.setCurrentSortMode(OverviewViewModel.SORT_FAV_DUE);
                } else {
                    showSnackbar("Sorting date, then favorite");
                    viewModel.setCurrentSortMode(OverviewViewModel.SORT_DUE_FAV);
                }
                viewModel.sortTasksAfterUserInput();
                return true;
            } else if (item.getItemId() == R.id.addTask) {
                callDetailViewForCreate();
                return true;
            } else if (item.getItemId() == R.id.deleteAllLocal) {
                showSnackbar("Local data was deleted.");
                viewModel.deleteAllLocalTasks();
                return true;
            } else if (item.getItemId() == R.id.deleteAllRemote) {
                String message = viewModel.deleteAllRemoteTasks();
                showSnackbar(message);
                return true;
            } else if (item.getItemId() == R.id.syncDB) {
                String message = viewModel.syncDatabases();
                showSnackbar(message);
                return true;
            } else {
                return false;
            }
        });
    }


    private AdapterView.OnItemClickListener onTaskClickListener() {
        return (parent, view, position, id) -> {
            TaskEntity selectedTask = listViewAdapter.getItem(position);
            callDetailViewForEdit(selectedTask);
        };
    }


    private void callDetailViewForEdit(TaskEntity selectedItem) {
        Intent callDetailViewForEdit = new Intent(Intent.ACTION_EDIT, null, this, DetailViewActivity.class);
        callDetailViewForEdit.putExtra(ARG_TASK, selectedItem);
        detailViewForEditLauncher.launch(callDetailViewForEdit);
    }


    public final ActivityResultLauncher<Intent> detailViewForEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
        if (activityResultObject.getData() != null) {
            String action = activityResultObject.getData().getAction();
            if (activityResultObject.getResultCode() == RESULT_OK) {
                TaskEntity returnedTask = (TaskEntity) activityResultObject.getData().getSerializableExtra(ARG_TASK);
                if (Objects.equals(action, "android.intent.action.DELETE")) {
                    this.viewModel.deleteTask(returnedTask);
                    assert returnedTask != null;
                    showSnackbar("Deleted " + returnedTask.getTitle());
                } else if (Objects.equals(action, "android.intent.action.EDIT")) {
                    this.viewModel.updateTask(returnedTask);
                    assert returnedTask != null;
                    showSnackbar("Edited " + returnedTask.getTitle());
                }
            } else {
                showSnackbar("Edits were not saved");
            }
        }
    });


    private void callDetailViewForCreate() {
        Intent detailviewIntent = new Intent(Intent.ACTION_INSERT, null, this, DetailViewActivity.class);
        detailViewForCreateLauncher.launch(detailviewIntent);
    }


    public final ActivityResultLauncher<Intent> detailViewForCreateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultObject -> {
        if (activityResultObject.getResultCode() == Activity.RESULT_OK) {
            Log.i(LOG_TAG, "Successfully received edited task from DetailView");
            assert activityResultObject.getData() != null;
            TaskEntity receivedTask = (TaskEntity) activityResultObject.getData().getSerializableExtra(ARG_TASK);
            viewModel.createTask(receivedTask);
        }
    });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_overview_menu, menu);
        return true;
    }


    public int calculateTaskDue(TaskEntity taskToBeCompared) {
        if (taskToBeCompared.getDueDate() > System.currentTimeMillis()) {
            return View.GONE;
        } else {
            return View.VISIBLE;
        }
    }

    private void showSnackbar(String msg) {
        Snackbar.make(findViewById(R.id.listView), msg, Snackbar.LENGTH_SHORT).show();
    }
}
