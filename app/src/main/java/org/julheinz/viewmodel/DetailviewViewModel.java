package org.julheinz.viewmodel;

import android.util.Log;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.time.Month;

public class DetailviewViewModel extends ViewModel {
    private static final String LOG_TAG = DetailviewViewModel.class.getSimpleName();
    private int[] dueDateIntegers;
    private int[] dueTimeIntegers;
    LocalDateTime dueDateTime;

    private TaskEntity taskEntity;
    private MutableLiveData<String> errorStatus = new MutableLiveData<>();

    public TaskEntity getTaskEntity() {
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

    public boolean checkFieldInputValid(int actionId) {
        Log.i(LOG_TAG, "checking input with action id: " + actionId);
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            if (taskEntity.getTitle().isEmpty()) { //TODO: causes error on task creation
                this.errorStatus.setValue("Field may not be empty.");
            }
        }
        return true; // false = focus can skip to next input field even if error, true = focus stays on field if error
    }

    public boolean onFieldInputChanged() {
        Log.i(LOG_TAG, "field input has changed");

        this.errorStatus.setValue(null); // reset the errorStatus so error disappears once validated (e.g. enough letters entered)
        return false; // return false so other listeners can process the event
    }

    public int[] getDueDateIntegers() {
        return dueDateIntegers;
    }

    /**
     * @param dueDateIntegers array with dayOfMonth, month, year
     */
    public void setDueDateIntegers(int[] dueDateIntegers) {
        this.dueDateIntegers = dueDateIntegers;
        this.getDueDateTime();
    }

    public int[] getDueTimeIntegers() {
        return dueTimeIntegers;
    }

    /**
     * @param dueTimeIntegers array with minute and hour
     */
    public void setDueTimeIntegers(int[] dueTimeIntegers) {
        this.dueTimeIntegers = dueTimeIntegers;
        this.getDueDateTime();
    }

    /**
     * Combine time and date from pickers into localDatetime
     */
    public LocalDateTime getDueDateTime() {
        if (getDueTimeIntegers() != null && getDueDateIntegers() != null) {
            int minute = dueTimeIntegers[0];
            int hourOfDay = dueTimeIntegers[1];
            int dayOfMonth = dueDateIntegers[0];
            int month = dueDateIntegers[1] + 1; // +1 because date picker returns month 0 - 11
            int year = dueDateIntegers[2];
            dueDateTime = LocalDateTime.of(year, Month.of(month), dayOfMonth, hourOfDay, minute);
        } else {
            /* because this method is called any time either the time or date picker is finished,
            now() is set as a default value until both pickers have returned values */
            dueDateTime = LocalDateTime.now();
        }
        Log.d(LOG_TAG, "Due DateTime: " + dueDateTime);
        taskEntity.setDueDate(dueDateTime);
        return dueDateTime;
    }

}
