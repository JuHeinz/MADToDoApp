package org.julheinz.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Fragment for the dialog that confirms deletion.
 */
public class DeleteDialogFragment extends DialogFragment {

    /**
     * Interface to pass the event of the click back to the hosting activity.
     * Hosting activity must implement this. Methods get called by android after user input.
     */
    public interface DeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    DeleteDialogListener listener;

    /**
     * Instantiate the listener and check if the host activity implements it.
     * Fragment.onAttach() is run when a fragment is created.
     *
     * @param context: host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "activity must implement DeleteDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()); //build a dialog
        alertDialogBuilder.setMessage("Really delete?").setPositiveButton("Yes, delete", (dialog, id) -> {
            listener.onDialogPositiveClick(DeleteDialogFragment.this); // callback to hosting activity
        }).setNegativeButton("No, keep task", (dialog, id) -> listener.onDialogNegativeClick(DeleteDialogFragment.this));
        return alertDialogBuilder.create(); // create and return an AlertDialog
    }
}
