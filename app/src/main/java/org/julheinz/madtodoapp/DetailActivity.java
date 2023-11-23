package org.julheinz.madtodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    EditText taskNameInput;
    EditText descriptionInput;
    Button addTaskBtn;
    Button backBtn;
    EditText dueDateInput;
    ImageButton favBtn;
    boolean isFav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        //get ui components
        taskNameInput = findViewById(R.id.taskNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        favBtn = findViewById(R.id.favBtn);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        backBtn = findViewById(R.id.backBtn);

        addTaskBtn.setOnClickListener(view -> addTask(view));
        backBtn.setOnClickListener(view -> {
            Intent overViewIntent = new Intent(this, OverviewActivity.class);
            startActivity(overViewIntent);
        });
    }


    /**
     * Creates an Task Entity with current timestamp as creation date
     */
    public TaskEntity createNewTask() {
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
    public void addTask(View view) {
        if (!taskNameInput.getText().toString().equals("")) {

            //add task to adapter

            TaskEntity task = createNewTask();

            /*
            taskArray.add(task);
            adapter.notifyDataSetChanged();
            */
            Log.d("Task Created:", task.toString());

            //success message
            toastMsg("Task " + task.getTaskName() + " added!");

            //reset UI
            taskNameInput.setText("");
            descriptionInput.setText("");
            dueDateInput.setText("");
            resetFav();
        }
    }

    /**
     * resets icon and state to not starred
     */
    public void resetFav() {
        isFav = false;
        favBtn.setImageResource(R.drawable.baseline_star_border_24);
    }

    /**
     * toogle the icon in the favButton and sets the state
     */
    public void toggleFav(View view) {
        isFav = !isFav;
        Log.d("Fav Btn Click:", isFav + "!");

        //change icon of button
        if (!isFav) {
            favBtn.setImageResource(R.drawable.baseline_star_border_24);
        } else {
            favBtn.setImageResource(R.drawable.baseline_star_24);
        }
    }

    /**
     * Creates toast for user feedback
     */
    //TODO: Call when task is completed
    public void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
