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
        contactIds = new MutableLiveData<>(taskEntity.getContacts());
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


    public boolean onFieldInputChanged() {
        this.errorStatus.setValue(null);
        return false;
    }


    public void addToContactsOfEntity(long id) {
        taskEntity.getContacts().add(String.valueOf(id));
        setContactIds();
    }


    public void removeFromContactsOfEntity(long id) {
        taskEntity.getContacts().remove(String.valueOf(id));
        setContactIds();
    }

    public void setContactIds() {
        contactIds.setValue(taskEntity.getContacts());
    }


    public MutableLiveData<HashSet<String>> getContactIds() {
        return contactIds;
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


    public void onSave() {
        this.userEvent.setValue(DetailViewUserEvent.SAVE);
    }


    public enum DetailViewUserEvent {
        SET_DATE, SET_TIME, SAVE
    }


    public class DateTimeHelper {
        private final GregorianCalendar calendar;

        public DateTimeHelper() {
            calendar = new GregorianCalendar();
            calendar.setTime(new Date(getTaskEntity().getDueDate()));
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
