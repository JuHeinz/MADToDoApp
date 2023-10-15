package org.julheinz.madtodoapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import org.julheinz.entities.TaskEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {


    Button addTaskBtn;

    ListView taskListView;
    List<TaskEntity> taskList;
    ArrayAdapter<TaskEntity> adapter;
    EditText taskNameInput;
    EditText descriptionInput;
    EditText dueDateInput;
    CheckBox favCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get ui components
        taskNameInput = findViewById(R.id.taskNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        favCheckBox = findViewById(R.id.favCheckBox);
        addTaskBtn = findViewById(R.id.addTaskBtn);

        //Click listener for button
        addTaskBtn.setOnClickListener(this::addTask);

        //set up adapter
        taskListView = findViewById(R.id.taskList);
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(this, R.layout.task_item, taskList);
        taskListView.setAdapter(adapter);
        getTasksFromDB();

    }

    /**
     * Adds TaskEntity to to adapter and triggers UI change
     */
    public void addTask(View view) {
        if (!taskNameInput.getText().toString().equals("")) {

            //add task to adapter

            TaskEntity task = createNewTask();
            taskList.add(task);
            adapter.notifyDataSetChanged();

            Log.d("Task Created:", task.toString());

            //success message
            toastMsg("Task " + task.getTaskName() + " added!");

            //empty input field
            taskNameInput.setText("");
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
        Boolean isFav = favCheckBox.isActivated();

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
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(),LocalDateTime.now(),false));
        taskList.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(),LocalDateTime.now(),true));
        adapter.notifyDataSetChanged();
    }

}
