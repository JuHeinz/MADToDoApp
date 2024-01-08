package org.julheinz.madtodoapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    TimePickerDialog.OnTimeSetListener listener;

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
        // get current time as default values
        final Calendar calendar = Calendar.getInstance();
        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteNow = calendar.get(Calendar.MINUTE);
        // give the listener from the host activity to the dialog so it can call the listener when the user is finished
        return new TimePickerDialog(getActivity(), listener, hourNow, minuteNow, DateFormat.is24HourFormat(getActivity()));
    }
}
