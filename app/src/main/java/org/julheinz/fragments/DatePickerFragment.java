package org.julheinz.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private static final String LOG_TAG = DatePickerFragment.class.getSimpleName();

    DatePickerDialog.OnDateSetListener listener;
    private final long taskDate;
    public DatePickerFragment(long taskDate){
        this.taskDate = taskDate;
    }
    /**
     * Get the listener from the host activity if it implements the DatePickerDialog.OnDateSetListener.
     *
     * @param context: host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            //get the listener from the host activity
            listener = (DatePickerDialog.OnDateSetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "activity must implement DatePickerDialog.OnDateSetListener.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(taskDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Log.d(LOG_TAG, "Creating time picker with default value: " + year +"/" + month + "/" + day) ;

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), listener, year, month, day);
    }
}
