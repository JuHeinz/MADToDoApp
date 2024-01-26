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

public class DetailviewViewModel extends ViewModel {

    /**
     * enums that communicate user action to acitvity, so that the layout does not have to reference activity directly
     */
    public enum DetailViewUserEvent {
        SET_DATE, SET_TIME, DELETE, FAVORITE, CANCEL, SAVE
    }

    private final MutableLiveData<DetailViewUserEvent> userEvent = new MutableLiveData<>();
    private static final String LOG_TAG = DetailviewViewModel.class.getSimpleName();

    private MutableLiveData<DateTimeHelper> dateTimeHelper;
    private TaskEntity taskEntity;
    private MutableLiveData<String> errorStatus = new MutableLiveData<>();
    private MutableLiveData<HashSet<String>> contactIdListLiveData = new MutableLiveData<>();

    public TaskEntity getTaskEntity() {
        return this.taskEntity;
    }

    public void addToContactsListOfEntity(long id) {
        taskEntity.getContacts().add(String.valueOf(id));
        setContactIdListLiveData(); //refresh live data by getting contacts from entity again
    }

    public void removeFromContactsListOfEntity(long id) {
        taskEntity.getContacts().remove(String.valueOf(id));
        setContactIdListLiveData(); //refresh live data by getting contacts from entity again
    }

    public void setContactIdListLiveData() {
        contactIdListLiveData.setValue(taskEntity.getContacts()); //the live data is always equal to the tasks' contact list
    }

    public MutableLiveData<HashSet<String>> getContactIdListLiveData() { //getter for the live data that is observed
        return contactIdListLiveData;
    }

    public void setTaskEntity(TaskEntity taskEntity) {
        Log.i(LOG_TAG, "Setting taskEntity: " + taskEntity.toString() + "in view model");
        this.taskEntity = taskEntity;
        contactIdListLiveData = new MutableLiveData<>(taskEntity.getContacts()); //initialize live data with contacts from taskEntity
        dateTimeHelper = new MutableLiveData<>(new DateTimeHelper());
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
        this.errorStatus.setValue(null); // reset the errorStatus so error disappears once validated (e.g. enough letters entered)
        return false; // return false so other listeners can process the event
    }

    public MutableLiveData<DetailViewUserEvent> getUserEvent() {
        return userEvent;
    }

    public MutableLiveData<DateTimeHelper> getDateTimeHelper() {
        return dateTimeHelper;
    }

    public void onSetDueDate() {
        this.userEvent.setValue(DetailViewUserEvent.SET_DATE);
    }

    public void onSetDueTime() {
        this.userEvent.setValue(DetailViewUserEvent.SET_TIME);
    }

    public void onCancel() {
        this.userEvent.setValue(DetailViewUserEvent.CANCEL);
    }

    public void onSave() {
        this.userEvent.setValue(DetailViewUserEvent.SAVE);
    }

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


        public String getDueDateFormatted(){
            String pattern = "dd/MM/yyyy";
            DateFormat df = new SimpleDateFormat(pattern);
            Date dateFormatted = calendar.getTime();
            return df.format(dateFormatted);
        }

        public String getDueTimeFormatted(){
            String pattern = "HH:mm";
            DateFormat df = new SimpleDateFormat(pattern);
            Date dateFormatted = calendar.getTime();
            return df.format(dateFormatted);
        }
    }
}
