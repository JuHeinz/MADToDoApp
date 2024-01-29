package org.julheinz.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.data.SyncTaskCrudOperations;
import org.julheinz.data.TaskCrudOperations;
import org.julheinz.entities.TaskEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OverviewViewModel extends ViewModel {
    private static final String LOG_TAG = OverviewViewModel.class.getSimpleName();

    public enum ProcessingState {RUNNING, RUNNING_LONG, DONE}

    private final MutableLiveData<ProcessingState> processingState = new MutableLiveData<>();

    private final ExecutorService operationRunner = Executors.newFixedThreadPool(4);
    private TaskCrudOperations crudOperations;
    private boolean initial = true;
    private final List<TaskEntity> taskList = new ArrayList<>();

    private Comparator<TaskEntity> currentSortMode;

    public static final Comparator<TaskEntity> SORT_FAV_DUE = Comparator.comparing(TaskEntity::isFav).reversed().thenComparing(TaskEntity::getDueDate);
    public static final Comparator<TaskEntity> SORT_DUE_FAV = Comparator.comparing(TaskEntity::getDueDate).thenComparing(TaskEntity::isFav);

    public OverviewViewModel() {
        this.currentSortMode = SORT_DUE_FAV;
    }

    public List<TaskEntity> getTasks() {
        return taskList;
    }

    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public void createTask(TaskEntity taskEntity) {
        processingState.setValue(ProcessingState.RUNNING);
        operationRunner.execute(() -> {
            TaskEntity createdTask = crudOperations.createTask(taskEntity);
            this.taskList.add(createdTask);
            applyTaskSorting();
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void readAllTasks() {
        processingState.setValue(ProcessingState.RUNNING_LONG);
        operationRunner.execute(() -> {
            List<TaskEntity> tasksFromCrud = crudOperations.readAllTasks();
            taskList.addAll(tasksFromCrud);
            applyTaskSorting();
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void updateTask(TaskEntity editedTask) {
        processingState.setValue(ProcessingState.RUNNING);
        operationRunner.execute(() -> {
            crudOperations.updateTask(editedTask);
            int index = taskList.indexOf(editedTask);
            this.taskList.set(index, editedTask);
            applyTaskSorting();
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void deleteTask(TaskEntity taskToBeDeleted) {
        processingState.setValue(ProcessingState.RUNNING);
        operationRunner.execute(() -> {
            crudOperations.deleteTask(taskToBeDeleted);
            this.taskList.remove(taskToBeDeleted);
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public String syncDatabases() {
        String message;
        processingState.setValue(ProcessingState.RUNNING_LONG);
        if (this.crudOperations instanceof SyncTaskCrudOperations) {
            Log.d(LOG_TAG, "Syncing databases");
            operationRunner.execute(() -> {
                List<TaskEntity> newLocalData = crudOperations.syncData();
                taskList.clear();
                taskList.addAll(newLocalData);
                applyTaskSorting();
                processingState.postValue(ProcessingState.DONE);
            });
            message = "Databases synced";
        } else {
            message = "Can't sync databases because we are offline.";
            Log.d(LOG_TAG, message);
            processingState.postValue(ProcessingState.DONE);
        }
        return message;
    }

    public void setCrudOperations(TaskCrudOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    public MutableLiveData<ProcessingState> getProcessingState() {
        return processingState;
    }

    public Comparator<TaskEntity> getCurrentSortMode() {
        return currentSortMode;
    }

    public void setCurrentSortMode(Comparator<TaskEntity> currentSortMode) {
        this.currentSortMode = currentSortMode;
    }

    public void sortTasksAfterUserInput() {
        processingState.setValue(ProcessingState.RUNNING);
        applyTaskSorting();
        processingState.setValue(ProcessingState.DONE);
    }


    private void applyTaskSorting() {
        this.taskList.sort(currentSortMode);
        this.taskList.sort(Comparator.comparing(TaskEntity::isDone));
    }


    public String deleteAllRemoteTasks() {
        String message;
        processingState.setValue(ProcessingState.RUNNING);
        if (this.crudOperations instanceof SyncTaskCrudOperations) {
            operationRunner.execute(() -> {
                crudOperations.deleteAllTasks(false);
                processingState.postValue(ProcessingState.DONE);
            });
            message = "Emptied remote database";
        } else {
            processingState.postValue(ProcessingState.DONE);
            message = "Can't empty remote database because we are offline.";
        }
        return message;
    }


    public void deleteAllLocalTasks() {
        processingState.setValue(ProcessingState.RUNNING);
        operationRunner.execute(() -> {
            crudOperations.deleteAllTasks(true);
            this.taskList.clear();
            processingState.postValue(ProcessingState.DONE);
        });
    }
}
