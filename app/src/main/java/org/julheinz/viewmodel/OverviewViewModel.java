package org.julheinz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.data.TaskCrudOperations;
import org.julheinz.entities.TaskEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for OverViewActivity: Calls business logic and manages data for the activity.
 * Business logic: calling database operations.
 * Data: list of taskEntities
 * Should not hold any references to the activity or its UI, is observed by the OverviewActivity.
 */
public class OverviewViewModel extends ViewModel {

    public enum ProcessingState {RUNNING, RUNNING_LONG, DONE}

    private MutableLiveData<ProcessingState> processingState = new MutableLiveData<>();

    private final ExecutorService operationRunner = Executors.newFixedThreadPool(4); // smart thread management
    private TaskCrudOperations crudOperations;
    private boolean initial = true;
    private final List<TaskEntity> taskList = new ArrayList<>();

    private Comparator<TaskEntity> currentSortMode;

    public OverviewViewModel() {
        this.currentSortMode = SORT_BY_DONE;
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
        operationRunner.execute(() -> { // execute assigns a new or existing thread to this task
            TaskEntity createdTask = crudOperations.createTask(taskEntity);
            this.taskList.add(createdTask);
            applyTaskSorting();
            processingState.postValue(ProcessingState.DONE); //change live data
        });
    }

    public void readTask(long id) {

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
            this.taskList.set(index, editedTask); //replace TaskEntity at that position in the task list
            applyTaskSorting(); //call sorting here sorting is updated after done or fav press in overview
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void deleteTask(TaskEntity taskToBeDeleted) {
        processingState.setValue(ProcessingState.RUNNING);
        operationRunner.execute(() -> {
            crudOperations.deleteTask(taskToBeDeleted); // remove from database
            this.taskList.remove(taskToBeDeleted); // remove from list view adapter
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void setCrudOperations(TaskCrudOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    public MutableLiveData<ProcessingState> getProcessingState() {
        return processingState;
    }

    public static Comparator<TaskEntity> SORT_BY_DONE = Comparator.comparing(TaskEntity::isDone);
    //TODO: check if data is sorted correctly by date
    public static Comparator<TaskEntity> SORT_FAV_DUE = Comparator.comparing(TaskEntity::isFav).reversed().thenComparing(TaskEntity::getDueDate);

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

    public void applyTaskSorting() {
        this.taskList.sort(currentSortMode);
    }

    public void deleteAllRemoteTasks() {
        processingState.setValue(ProcessingState.RUNNING);
        operationRunner.execute(() -> {
            crudOperations.deleteAllTasks(false);
            this.taskList.clear();
            processingState.postValue(ProcessingState.DONE);
        });
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
