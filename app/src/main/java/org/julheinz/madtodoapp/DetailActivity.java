package org.julheinz.madtodoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private EditText taskNameInput;
    private EditText descriptionInput;
    private Button addTaskBtn;
    private Button backBtn;
    private EditText dueDateInput;
    private ImageButton favBtn;
    private boolean isFav;

    public static final String ARG_TASK = "task";
    private TaskEntity task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "created!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        Intent detailViewIntentFromOverview = getIntent();
        //Wenn wir durch klicken auf eine bereits erstellte task auf diese view kommen, wird der name der bereits erstellten task übergeben
        //Neue instanz der klasse, nicht die selbe weil serializable
        this.task = (TaskEntity) detailViewIntentFromOverview.getSerializableExtra(ARG_TASK);
        if(this.task == null){
            this.task = new TaskEntity("", "", null, null, false);
        }

        //get ui components
        taskNameInput = findViewById(R.id.taskNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);

        favBtn = findViewById(R.id.favBtn);

        addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(this::addTask);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this::cancelEdit);

        taskNameInput.setText(task.getTaskName());
    }


    /**
     * Creates an Task Entity with current timestamp as creation date
     */
    private TaskEntity createNewTask() {
        //get user input
        String taskName = taskNameInput.getText().toString();
        String description = descriptionInput.getText().toString();
        LocalDateTime dateCreated = LocalDateTime.now();

        LocalDateTime dueDate;
        if (dueDateInput.getText().toString().isEmpty()) {
            try {
                dueDate = LocalDateTime.parse(dueDateInput.getText().toString());
            } catch (DateTimeParseException e) {
                dueDate = LocalDateTime.now();
            }
        } else {
            dueDate = LocalDateTime.now();
        }

        return new TaskEntity(taskName, description, dateCreated, dueDate, isFav);
    }

    /**
     * Adds TaskEntity to to adapter and triggers UI change
     */
    private void addTask(View view) {
        if (!taskNameInput.getText().toString().isEmpty()) {

            //TODO: add task to adapter
            this.task = createNewTask();

            Log.d("Task Created:", task.toString());

            //Intent um auf die Activity zurückzukehren, die diese augerufen hat und ihr daten mitgeben
            Intent returnToCallerWithValueIntent = new Intent();
            returnToCallerWithValueIntent.putExtra(ARG_TASK, task);
            setResult(Activity.RESULT_OK, returnToCallerWithValueIntent);

            //close activity and return to caller
            finish();
        }
    }

    private void cancelEdit(View view) {
        Intent returnToCallerWhenCancelled = new Intent();
        setResult(Activity.RESULT_CANCELED, returnToCallerWhenCancelled);
        //close activity and return to caller
        finish();
    }

    /**
     * toogle the icon in the favButton and sets the state
     */
    private void toggleFav(View view) {
        isFav = !isFav;
        Log.d("Fav Btn Click:", isFav + "!");

        //change icon of button
        if (!isFav) {
            favBtn.setImageResource(R.drawable.baseline_star_border_24);
        } else {
            favBtn.setImageResource(R.drawable.baseline_star_24);
        }
    }


}
