package org.julheinz.madtodoapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {
    private static final String LOG_TAG = TimePickerFragment.class.getSimpleName();

    TimePickerDialog.OnTimeSetListener listener;
    long taskDate;
    public TimePickerFragment(long taskDate){
        this.taskDate = taskDate;
    }

    /**
     * Get the listener from the host activity if it implements the TimePickerDialog.OnTimeSetListener.
     *
     * @param context: host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            //get the listener from the host activity
            listener = (TimePickerDialog.OnTimeSetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "activity must implement OnTimePickedListener.");
        }
    }

    /**
     * Return a TimePickerDialog with
     * default values,
     * the listener from the host activity that should be called when the user is done,
     * and a time format.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(taskDate);
        Log.d(LOG_TAG, "date:" + calendar.getTime());
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        Log.d(LOG_TAG, "Creating time picker with default value: " + hour +":" + minute);
        // give the listener from the host activity to the dialog so it can call the listener when the user is finished
        return new TimePickerDialog(getActivity(), listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}
