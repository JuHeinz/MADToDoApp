package org.julheinz.viewmodel;

import android.util.Log;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.entities.TaskEntity;

public class DetailviewViewModel extends ViewModel {
    private static final String LOG_TAG = DetailviewViewModel.class.getSimpleName();

    private TaskEntity taskEntity;
    private MutableLiveData<String> errorStatus = new MutableLiveData<>();

    public TaskEntity getTaskEntity() {
        //Log.d(LOG_TAG, "someone requested task: " + taskEntity.toString() + "from viewmodel");
        return this.taskEntity;
    }

    public void setTaskEntity(TaskEntity taskEntity) {
        Log.d(LOG_TAG, "setting taskEntity: " + taskEntity.toString() + "in viewmodel");

        this.taskEntity = taskEntity;
    }

    public MutableLiveData<String> getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(MutableLiveData<String> errorStatus) {
        this.errorStatus = errorStatus;
    }

    public boolean checkFieldInputValid(int actionId){
        Log.i(LOG_TAG, "checking input with action id: " + actionId);
        if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ){
            if(taskEntity.getTitle().isEmpty()){ //TODO: causes error on task creation
                this.errorStatus.setValue("Field may not be empty.");
            }
        }
        return true; // false = focus can skip to next input field even if error, true = focus stays on field if error
    }

    public boolean onFieldInputChanged(){
        Log.i(LOG_TAG, "field input has changed");

        this.errorStatus.setValue(null); // reset the errorStatus so error disappears once validated (e.g. enough letters entered)
        return false; // return false so other listeners can process the event
    }

}
