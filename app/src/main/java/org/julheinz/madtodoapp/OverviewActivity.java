package org.julheinz.madtodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private static String LOG_TAG = OverviewActivity.class.getSimpleName();


    ListView taskListView;
    List<TaskEntity> taskArray;
    ArrayAdapter<TaskEntity> adapter;

    Button addTaskBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "created!");

        //set up adapter
        taskArray = new ArrayList<>();
        adapter = new TaskAdapter(this, R.layout.list_item, taskArray);
        taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(adapter);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        //get previously inputed tasks
        getTasksFromDB();

        addTaskBtn.setOnClickListener(view -> {
            Intent detailviewIntent = new Intent(this, DetailActivity.class);
            startActivity(detailviewIntent);
        });

    }


    /**
     * Gets previously added tasks from "database" and add them to adapter
     */
    private void getTasksFromDB() {
        taskArray.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskArray.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        adapter.notifyDataSetChanged();
    }


}
