package org.julheinz.madtodoapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    Button addTaskBtn;

    ListView taskListView;
    List<TaskEntity> taskArray;
    ArrayAdapter<TaskEntity> adapter;
    EditText taskNameInput;
    EditText descriptionInput;

    EditText dueDateInput;

    boolean isFav;
    ImageButton favBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get ui components
        taskNameInput = findViewById(R.id.taskNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        favBtn = findViewById(R.id.favBtn);
        addTaskBtn = findViewById(R.id.addTaskBtn);

        //Click listener for button
        addTaskBtn.setOnClickListener(view -> addTask(view));

        //set up adapter
        taskArray = new ArrayList<>();
        adapter = new TaskAdapter(this, R.layout.list_item, taskArray);
        taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(adapter);
        
        //get previously inputed tasks
        getTasksFromDB();

    }

    /**
     * Adds TaskEntity to to adapter and triggers UI change
     */
    public void addTask(View view) {
        if (!taskNameInput.getText().toString().equals("")) {

            //add task to adapter

            TaskEntity task = createNewTask();
            taskArray.add(task);
            adapter.notifyDataSetChanged();

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
     * Creates an Task Entity with current timestamp as creation date
     */
    public TaskEntity createNewTask() {
        //get user input
        String taskName = taskNameInput.getText().toString();
        String description = descriptionInput.getText().toString();
        LocalDateTime dateCreated = LocalDateTime.now();

        LocalDateTime dueDate;
        if(dueDateInput.getText().toString().isEmpty()){
            try{
                dueDate = LocalDateTime.parse(dueDateInput.getText().toString());
            } catch (DateTimeParseException e){
                dueDate = LocalDateTime.now();
            }
        }else{
            dueDate = LocalDateTime.now();
        }

        return new TaskEntity(taskName, description, dateCreated,dueDate,isFav);
    }

    /**
     * Creates toast for user feedback
     */
    //TODO: Call when task is completed
    public void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Gets previously added tasks from "database" and add them to adapter
     */
    private void getTasksFromDB() {
        taskArray.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(),LocalDateTime.now(),false));
        taskArray.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(),LocalDateTime.now(),true));
        adapter.notifyDataSetChanged();
    }

    /**
     * toogle the icon in the favButton and sets the state
     */
    public void toggleFav(View view){
        isFav = !isFav;
        Log.d("Fav Btn Click:", isFav + "!");

        //change icon of button
        if(!isFav){
            favBtn.setImageResource(R.drawable.baseline_star_border_24);
        }else{
            favBtn.setImageResource(R.drawable.baseline_star_24);
        }
    };

    /**
     * resets icon and state to not starred
     */
    public void resetFav(){
        isFav = false;
        favBtn.setImageResource(R.drawable.baseline_star_border_24);
    }

}
