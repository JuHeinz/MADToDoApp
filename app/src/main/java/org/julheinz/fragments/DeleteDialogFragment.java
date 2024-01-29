package org.julheinz.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


public class DeleteDialogFragment extends DialogFragment {


    public interface DeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    DeleteDialogListener listener;


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
            listener.onDialogPositiveClick(DeleteDialogFragment.this);
        }).setNegativeButton("No, keep task", (dialog, id) -> listener.onDialogNegativeClick(DeleteDialogFragment.this));
        return alertDialogBuilder.create();
    }
}
