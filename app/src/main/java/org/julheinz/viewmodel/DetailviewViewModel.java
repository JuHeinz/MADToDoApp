package org.julheinz.viewmodel;

import android.util.Log;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.julheinz.entities.TaskEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

/**
 * Saves data and calls business logic for detail view
 */

public class DetailviewViewModel extends ViewModel {

    private final MutableLiveData<DetailViewUserEvent> userEvent = new MutableLiveData<>();
    private static final String LOG_TAG = DetailviewViewModel.class.getSimpleName();

    private MutableLiveData<DateTimeHelper> dateTimeHelper;
    private TaskEntity taskEntity;
    private MutableLiveData<String> errorStatus = new MutableLiveData<>();
    private MutableLiveData<HashSet<String>> contactIds = new MutableLiveData<>();

    public TaskEntity getTaskEntity() {
        return this.taskEntity;
    }

    public void setTaskEntity(TaskEntity taskEntity) {
        Log.i(LOG_TAG, "Setting taskEntity: " + taskEntity.toString() + "in view model");
        this.taskEntity = taskEntity;
        contactIds = new MutableLiveData<>(taskEntity.getContacts()); //initialize live data with contacts from taskEntity
        dateTimeHelper = new MutableLiveData<>(new DateTimeHelper());
    }

    public MutableLiveData<String> getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(MutableLiveData<String> errorStatus) {
        this.errorStatus = errorStatus;
    }

    /**
     * Check if input value is not empty
     *
     * @param actionId the action in the editor.
     * @return true if input is invalid so focus stays on field. false if input is invalid or editor action not relevant, so focus can skip to next field.
     */
    public boolean checkFieldInputValid(int actionId) {
        Log.i(LOG_TAG, "checking input with action id: " + actionId);
        if ((actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) && taskEntity.getTitle() != null) {
            if (taskEntity.getTitle().isEmpty()) {
                this.errorStatus.setValue("Field may not be empty.");
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Reset the error status on the input field, so that that the error disappears as soon as user types again.
     *
     * @return false so other listeners can process the event
     */
    public boolean onFieldInputChanged() {
        this.errorStatus.setValue(null);
        return false;
    }

    /**
     * Update the task entity with a new contact
     */
    public void addToContactsOfEntity(long id) {
        taskEntity.getContacts().add(String.valueOf(id));
        setContactIds(); //refresh live data by getting contacts from entity again
    }

    /**
     * Update the task entity by removing a contact
     */
    public void removeFromContactsOfEntity(long id) {
        taskEntity.getContacts().remove(String.valueOf(id));
        setContactIds(); //refresh live data by getting contacts from entity again
    }

    public void setContactIds() {
        contactIds.setValue(taskEntity.getContacts()); //the live data is always equal to the tasks' contact list
    }

    /**
     * Getter for contact list live data
     */
    public MutableLiveData<HashSet<String>> getContactIds() {
        return contactIds;
    }

    /**
     * Getter for user event live data
     */
    public MutableLiveData<DetailViewUserEvent> getUserEvent() {
        return userEvent;
    }

    /**
     * Getter for date time helper live data
     */
    public MutableLiveData<DateTimeHelper> getDateTimeHelper() {
        return dateTimeHelper;
    }

    /**
     * Inform activity that user clicked set date button
     */
    public void onSetDueDate() {
        this.userEvent.setValue(DetailViewUserEvent.SET_DATE);
    }

    /**
     * Inform activity that user clicked set time button
     */
    public void onSetDueTime() {
        this.userEvent.setValue(DetailViewUserEvent.SET_TIME);
    }

    /**
     * Inform activity that user clicked save button
     */
    public void onSave() {
        this.userEvent.setValue(DetailViewUserEvent.SAVE);
    }

    /**
     * Represents user actions. Click listeners are bound to the view model, not the activity. These events will be communicated to the activity,
     * so that the layout does not have to reference activity directly.
     */
    public enum DetailViewUserEvent {
        SET_DATE, SET_TIME, SAVE
    }

    /**
     * Holds and translates time stamps for task.
     */
    public class DateTimeHelper {
        private final GregorianCalendar calendar;

        public DateTimeHelper() {
            calendar = new GregorianCalendar();
            calendar.setTime(new Date(getTaskEntity().getDueDate())); //set the calendar to the due date, due date is never null because a default value is used for new tasks
        }

        public long getDateTimeAsLongValue() {
            long dateTime = calendar.getTime().getTime();
            Log.i(LOG_TAG, "Date and time are: " + dateTime);
            return dateTime;
        }

        public void setDueDate(int year, int month, int dayOfMonth) {
            calendar.set(GregorianCalendar.YEAR, year);
            calendar.set(GregorianCalendar.MONTH, month);
            calendar.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
            taskEntity.setDueDate(this.getDateTimeAsLongValue());
            dateTimeHelper.setValue(this);
            // set the dateTimeHelper of the view model to this instance of the helper,
            // because only if a field in view model changes is the mutable live data changed and the ui is changed

        }

        public void setDueTime(int hour, int minute) {
            calendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
            calendar.set(GregorianCalendar.MINUTE, minute);
            taskEntity.setDueDate(this.getDateTimeAsLongValue());
            dateTimeHelper.setValue(this);
        }

        public String getDueDateFormatted() {
            String pattern = "dd/MM/yyyy";
            DateFormat df = new SimpleDateFormat(pattern);
            Date dateFormatted = calendar.getTime();
            return df.format(dateFormatted);
        }

        public String getDueTimeFormatted() {
            String pattern = "HH:mm";
            DateFormat df = new SimpleDateFormat(pattern);
            Date dateFormatted = calendar.getTime();
            return df.format(dateFormatted);
        }
    }
}
