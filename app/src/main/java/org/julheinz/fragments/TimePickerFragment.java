package org.julheinz.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    private static final String LOG_TAG = TimePickerFragment.class.getSimpleName();

    TimePickerDialog.OnTimeSetListener listener;
    private final long taskDate;

    public TimePickerFragment(long taskDate) {
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
            listener = (TimePickerDialog.OnTimeSetListener) context; //get the listener from the host activity
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "activity must implement OnTimePickedListener.");
        }
    }

    /**
     * Return a TimePickerDialog with default values, the listener from the host activity that should be called when the user is done and a time format.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(taskDate);
        Log.d(LOG_TAG, "date:" + calendar.getTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        Log.d(LOG_TAG, "Creating time picker with default value: " + hour + ":" + minute);
        // give the listener from the host activity to the dialog so it can call the listener when the user is finished
        return new TimePickerDialog(getActivity(), listener, hour, minute, true);
    }
}
