package org.julheinz.madtodoapp;

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
     * Interface to pass the event of the click back to the hosting activity
     */
    public interface DeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    DeleteDialogListener listener;

    /**
     * Instantiate the listener and check if the host activity implements it.
     * Fragment.onAttach() is run when a fragment is created.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("activity must implement DeleteDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Really delete?")
                .setPositiveButton("Yes, delete", (dialog, id) -> {
                    // callback to hosting activity
                    listener.onDialogPositiveClick(DeleteDialogFragment.this);
                })
                .setNegativeButton("No, keep task", (dialog, id) -> listener.onDialogNegativeClick(DeleteDialogFragment.this));
        // create and return an AlertDialog
        return alertDialogBuilder.create();
    }
}
