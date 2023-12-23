package org.julheinz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.data.TaskCrudOperations;
import org.julheinz.entities.TaskEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Saves the data for the OverviewActivity. sorgt dafür dass man nicht ständig einen neuen thread aufmachen muss und dann wieder runOnUiThread rufen muss
 * Sonst müsste man so sachen machen wie :
 *   new Thread(() -> { //new thread for database access
 *      askEntity createdTask = taskCrudManager.createTask(receivedTask);
 *      //go back to ui thread
 *      runOnUiThread(() -> listViewAdapter.add(createdTask));}).start();
 *      Enthält die Business Logik
 */
public class OverviewViewModel extends ViewModel {

    public enum ProcessingState {RUNNING, RUNNING_LONG, DONE}

    private MutableLiveData<ProcessingState> processingState = new MutableLiveData<>();

    //magisches threadmanagement von android
    private final ExecutorService operationRunner = Executors.newFixedThreadPool(4);
    private TaskCrudOperations crudOperations;
    private boolean initial = true;
    private List<TaskEntity> taskList = new ArrayList<>();

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
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void readTask(long id) {

    }

    public void readAllTasks() {
        processingState.setValue(ProcessingState.RUNNING_LONG);

        operationRunner.execute(() -> {
            List<TaskEntity> tasksFromCrud = crudOperations.readAllTasks();
            taskList.addAll(tasksFromCrud);
            //aus hintergrundthread dem processingstate einen neuen wert liefern
            //im ui thread observed jemand diesen processingstate (mutable live data)
            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void updateTask(TaskEntity editedTask) {
        processingState.setValue(ProcessingState.RUNNING);
        operationRunner.execute(() -> {
            crudOperations.updateTask(editedTask);
            int index = taskList.indexOf(editedTask);
            //replace TaskEntity at that position in the task list
            this.taskList.set(index, editedTask);
            processingState.postValue(ProcessingState.DONE);
        });
    }

    void deleteTask(long id) {

    }

    public void setCrudOperations(TaskCrudOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    public MutableLiveData<ProcessingState> getProcessingState() {
        return processingState;
    }
}
