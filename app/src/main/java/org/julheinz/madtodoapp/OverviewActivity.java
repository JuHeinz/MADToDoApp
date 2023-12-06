package org.julheinz.madtodoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();

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

        addTaskBtn.setOnClickListener(this::startTaskCreation);

    }

    /**
     * Wird aufgerufen wenn die actvitiy die mit startActivityForResult() gestartet wurde finished
     * @param requestCode identifiziert einen aufruf/ use case z.B. 20 = von DetailActivity wenn task successfully added
     * @param resultCode z.B. RESULT_OK
     * @param data das was bei putExtra von der augerufenen activity mitgegeben wurde
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 20){
            switch (resultCode){
                case Activity.RESULT_OK :
                    String receivedItemName = data.getStringExtra("taskName");
                    toastMsg("Added task " + receivedItemName);
                    break;
                case Activity.RESULT_CANCELED:
                    toastMsg("Adding of task cancelled");
                    break;
            }
        }else{
            //??
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Creates toast for user feedback
     */
    public void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Gets previously added tasks from "database" and add them to adapter
     */
    private void getTasksFromDB() {
        taskArray.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskArray.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        adapter.notifyDataSetChanged();
    }

    /**
     * Starts detailActivity for result
     */
    private void startTaskCreation(View view){
        Intent detailviewIntent = new Intent(this, DetailActivity.class);
        //Started eine neue activity von der wir eine result zurück bekommen wollen
        startActivityForResult(detailviewIntent, 20);
    }

}
