package org.julheinz.madtodoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;

public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private EditText taskNameInput;
    private EditText descriptionInput;
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

        taskNameInput = findViewById(R.id.taskNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        CheckBox isDoneCheckBox = findViewById(R.id.isDoneCheckBox);
        Button addTaskBtn = findViewById(R.id.addTaskBtn);
        dueDateInput = findViewById(R.id.dueDateInput);
        favBtn = findViewById(R.id.favBtn);
        TextView createdDateOutput = findViewById(R.id.createdDateOutput);


        //wenn wir durch den "new task" button hier her gekommen sind, dann müssen wir eine neue task erstellen
        if(this.task == null){
            this.task = new TaskEntity("", "", LocalDateTime.now(), LocalDateTime.now(), false);
        }else{
            taskNameInput.setText(task.getTaskName());
            descriptionInput.setText(task.getDescription());
            dueDateInput.setText(task.getDueDate());
            createdDateOutput.setText(task.getCreatedDate());
            isDoneCheckBox.setChecked(task.isDone());
        }

        //get ui components



        addTaskBtn.setOnClickListener(this::saveTask);

        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this::cancelEdit);







    }


    /**
     * Creates an Task Entity with current timestamp as creation date
     */
    private TaskEntity createTaskFromUserInput() {
        //get user input
        String taskName = taskNameInput.getText().toString();
        String description = descriptionInput.getText().toString();
        LocalDateTime dateCreated = LocalDateTime.now();
        //TODO: Get from user input
        LocalDateTime dueDate = LocalDateTime.now();
        return new TaskEntity(taskName, description, dateCreated, dueDate, isFav);
    }

    /**
     * Adds TaskEntity to to adapter and triggers UI change
     */
    private void saveTask(View view) {
        if (!taskNameInput.getText().toString().isEmpty()) {

            this.task = createTaskFromUserInput();

            Log.d("Task Created:", task.toString());

            //Intent um auf die Activity zurückzukehren, die diese augerufen hat und ihr daten mitgeben, die dann zum adapter hinzugefügt werdeb
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
