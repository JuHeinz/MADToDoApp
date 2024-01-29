package org.julheinz.madtodoapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.julheinz.entities.ContactEntity;
import org.julheinz.entities.TaskEntity;
import org.julheinz.fragments.DatePickerFragment;
import org.julheinz.fragments.DeleteDialogFragment;
import org.julheinz.fragments.TimePickerFragment;
import org.julheinz.listadapters.ContactListAdapter;
import org.julheinz.madtodoapp.databinding.DetailViewBinding;
import org.julheinz.viewmodel.DetailviewViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class DetailViewActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String LOG_TAG = DetailViewActivity.class.getSimpleName();

    public static final String ARG_TASK = "task";
    private DetailviewViewModel viewModel;

    private final List<ContactEntity> localContactsList = new ArrayList<>();
    private ArrayAdapter<ContactEntity> listViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DetailViewBinding itemBinding = DataBindingUtil.setContentView(this, R.layout.detail_view);
        Intent detailViewIntentFromOverview = getIntent();
        String action = detailViewIntentFromOverview.getAction();

        this.viewModel = new ViewModelProvider(this).get(DetailviewViewModel.class);

        if (this.viewModel.getTaskEntity() == null) {
            TaskEntity taskFromIntent = (TaskEntity) detailViewIntentFromOverview.getSerializableExtra(ARG_TASK);
            TaskEntity task = taskFromIntent;
            if (taskFromIntent == null) {
                task = new TaskEntity();
                Log.i(LOG_TAG, "created new empty task: " + task);
            } else {
                Log.i(LOG_TAG, "got task from overview " + task);
            }
            this.viewModel.setTaskEntity(task);
        }

        itemBinding.setViewmodel(this.viewModel);
        itemBinding.setLifecycleOwner(this);
        this.viewModel.getContactIds().observe(this, listFromLiveData -> {
            for (String contactId : listFromLiveData) {
                addToLocalContactEntityList(Long.parseLong(contactId));
            }
            listViewAdapter.notifyDataSetChanged();
        });

        this.viewModel.getUserEvent().observe(this, event -> {
            switch (event) {
                case SET_DATE:
                    showDatePickerDialog();
                    break;
                case SET_TIME:
                    showTimePickerDialog();
                    break;
                case SAVE:
                    saveTask();
                    break;
            }
        });

        Toolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        topAppBar.setNavigationOnClickListener(v -> cancelEdit());

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.addContact) {
                lauchContactPickerIntent();
                return true;
            } else if (item.getItemId() == R.id.deleteTask) {
                confirmDeletionViaDialog();
                return true;
            } else {
                return false;
            }
        });

        ListView listView = findViewById(R.id.contactListView);
        listViewAdapter = new ContactListAdapter(this, R.id.contactListView, localContactsList, this.getLayoutInflater());
        listView.setAdapter(listViewAdapter);

        if (Objects.equals(action, "android.intent.action.INSERT")) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("New task");
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(viewModel.getTaskEntity().getTitle());
        }
    }


    private void saveTask() {
        Log.i(LOG_TAG, "Task saved:" + viewModel.getTaskEntity().toString());
        Intent returnToCallerWithValueIntent = new Intent(Intent.ACTION_EDIT);
        returnToCallerWithValueIntent.putExtra(ARG_TASK, viewModel.getTaskEntity());
        setResult(Activity.RESULT_OK, returnToCallerWithValueIntent);
        finish();
    }


    private void cancelEdit() {
        Intent returnToCallerWhenCancelled = new Intent();
        setResult(Activity.RESULT_CANCELED, returnToCallerWhenCancelled);
        finish();
    }


    private void deleteTask() {
        Intent returnToCallerWithValueIntent = new Intent(Intent.ACTION_DELETE);
        returnToCallerWithValueIntent.putExtra(ARG_TASK, viewModel.getTaskEntity());
        setResult(Activity.RESULT_OK, returnToCallerWithValueIntent);
        finish();
    }


    private void confirmDeletionViaDialog() {
        DialogFragment dialogFragment = new DeleteDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "DeleteDialogFragment");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        deleteTask();
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    private void showTimePickerDialog() {
        TimePickerFragment timePickerFragment = new TimePickerFragment(viewModel.getTaskEntity().getDueDate());
        timePickerFragment.show(getSupportFragmentManager(), "timePickerDueDate");
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(viewModel.getTaskEntity().getDueDate());
        datePickerFragment.show(getSupportFragmentManager(), "datePickerDueDate");
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.viewModel.getDateTimeHelper().getValue().setDueTime(hourOfDay, minute);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i(LOG_TAG, "Set due date to: " + year + "/" + month + "/" + dayOfMonth);
        this.viewModel.getDateTimeHelper().getValue().setDueDate(year, month, dayOfMonth);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detailview_menu, menu);
        return true;
    }


    private void lauchContactPickerIntent() {
        Intent selectContactOIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        selectContactLauncher.launch(selectContactOIntent);
    }


    private final ActivityResultLauncher<Intent> selectContactLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && (result.getData() != null)) {
            onContactSelected(result.getData());
        } else {
            String message = "Something went wrong with getting the contact";
            showSnackbar(message);
            Log.i(LOG_TAG, message);
        }
    });


    private void onContactSelected(Intent returnIntentFromContactsApp) {
        Uri contactUri = returnIntentFromContactsApp.getData();
        if (contactUri != null) {

            try (Cursor cursor = getContentResolver().query(contactUri, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String contactName = cursor.getString(displayNameColumnIndex);
                    int contactIdColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    long contactID = cursor.getLong(contactIdColumnIndex);
                    Log.i(LOG_TAG, "Contact selected by user: " + contactID + " " + contactName);
                    int hasReadContactPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
                    if (hasReadContactPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 10);
                    } else {
                        this.viewModel.addToContactsOfEntity(contactID);
                    }
                }
            }
        }
    }

    private String getContactName(long id) {
        String contactName = "";

        String[] contactIDs = new String[]{String.valueOf((id))};
        try (Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, "contact_id=?", contactIDs, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int displayNameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    contactName = cursor.getString(displayNameColumnIndex);
                }
            }
            return contactName;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "onRequestPermissionsResult: " + Arrays.asList(permissions) + ": " + Arrays.toString(grantResults));
    }


    private String getMobileNr(long id) {
        String currentPhoneNumber = "";
        int currentNumberType = 0;


        String[] contactIDs = new String[]{String.valueOf((id))};
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id=?", contactIDs, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int phoneNumberTypeColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    currentPhoneNumber = cursor.getString(phoneNumberColumnIndex);
                    currentNumberType = cursor.getInt(phoneNumberTypeColumnIndex);
                }
            }
        }
        if (currentNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
            return currentPhoneNumber;
        } else {
            return "";
        }
    }

    private String getEmail(long id) {
        String[] contactIDs = new String[]{String.valueOf(id)};
        String email = "";
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, "contact_id=?", contactIDs, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int emailColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                    email = cursor.getString(emailColumnIndex);
                }
            }
        }
        return email;
    }

    private void showSnackbar(String msg) {
        Snackbar.make(findViewById(R.id.DetailView), msg, Snackbar.LENGTH_SHORT).show();
    }


    private void addToLocalContactEntityList(long contactID) {
        if (!localContactsList.contains(new ContactEntity(contactID, null, null, null))) {
            String phone = getMobileNr(contactID);
            String email = getEmail(contactID);
            String contactName = getContactName(contactID);
            ContactEntity contactEntity = new ContactEntity(contactID, contactName, email, phone);
            localContactsList.add(contactEntity);
            Log.i(LOG_TAG, "Following contact has been added to local contacts list" + contactEntity);
            listViewAdapter.notifyDataSetChanged();
        } else {
            Log.i(LOG_TAG, "Contact already added");
        }
    }


    public void deleteContact(long contactID) {
        Log.i(LOG_TAG, "Attempting to remove contact with id" + contactID);
        ContactEntity contactToBeRemoved = new ContactEntity(contactID, null, null, null);
        localContactsList.remove(contactToBeRemoved);
        Log.i(LOG_TAG, "Local contact list after deleting of contact:" + localContactsList);
        viewModel.removeFromContactsOfEntity(contactID);
        listViewAdapter.notifyDataSetChanged();
    }


    public void sendEmailToContact(String address) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, "New task!");
        intent.putExtra(Intent.EXTRA_TEXT, getMessageForContact());
        startActivity(intent);
    }


    public void sendSMSToContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", getMessageForContact());
        startActivity(intent);
    }


    private String getMessageForContact() {
        Log.i(LOG_TAG, "Attempting to send message for task " + viewModel.getTaskEntity());

        String dueDate = viewModel.getTaskEntity().getFullDueDateFormatted();
        String taskTitle = viewModel.getTaskEntity().getTitle();
        String taskDescription = viewModel.getTaskEntity().getDescription();
        String messageBody = "Here is a new task for you! ";
        if (taskTitle != null) {
            messageBody = messageBody + "Title: " + taskTitle;
        } else {
            messageBody = messageBody + "Title: Untitled";
        }
        if (taskDescription != null) {
            messageBody = messageBody + " | Description: " + taskDescription;
        } else {
            messageBody = messageBody + " | Description: none";
        }
        messageBody = messageBody + " | Due: " + dueDate;

        return messageBody;
    }
}



