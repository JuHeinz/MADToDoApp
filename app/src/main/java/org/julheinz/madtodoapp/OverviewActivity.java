package org.julheinz.madtodoapp;

import static org.julheinz.madtodoapp.DetailActivity.ARG_TASK;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private static final int CALL_DETAIL_VIEW_FOR_EDIT = 20;
    private static final int CALL_DETAIL_VIEW_FOR_CREATE = 30;

    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private final List<TaskEntity> taskList = new ArrayList<>();

    private ArrayAdapter<TaskEntity> listViewAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "created!");

        ListView listView = findViewById(R.id.listView);

        /* adapter managed was in der listview angezeigt wird. arguments:
         1. von welcher activity wird es aufegrufen,
         2: welches layout soll ein einzeles item haben,
         3: textfeld wo das resultat rein soll
         3: liste der items */
        this.listViewAdapter = new TaskListAdapter(this, R.layout.list_item, taskList);
        listView.setAdapter(this.listViewAdapter);


        /* click listener for click on list item. arguments:
        1: parent view where click happened
        2: item (view) auf das geklickt wurde
        3: position des elements in ansicht
        4: id für aufruf direkt auf datenbank (hier nicht verwendet) */
        listView.setOnItemClickListener((parentView, view, position, id) -> {
            //Hole das item der liste das der position des geklickten elements entspricht
            TaskEntity selectedTask = this.listViewAdapter.getItem(position);
            callDetailViewForEdit(selectedTask);
        });

        getTasksFromDB();

        FloatingActionButton addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(this::callDetailViewForCreate);

    }

    /**
     * Wird aufgerufen wenn die actvitiy die mit startActivityForResult() gestartet wurde finished
     *
     * @param requestCode identifiziert einen aufruf / use case z.B. 20 = von DetailActivity wenn task successfully added
     * @param resultCode  status des resultats z.B. RESULT_OK
     * @param data        das was bei putExtra von der augerufenen activity mitgegeben wurde
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CALL_DETAIL_VIEW_FOR_EDIT) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    TaskEntity receivedTask = (TaskEntity) data.getSerializableExtra(ARG_TASK);
                    toastMsg("Added task " + receivedTask.getTaskName());
                    listViewAdapter.add(receivedTask);
                    break;
                case Activity.RESULT_CANCELED:
                    toastMsg("Adding of task cancelled");
                    break;
            }
        } else if (requestCode == CALL_DETAIL_VIEW_FOR_CREATE) {
            if (resultCode == Activity.RESULT_OK) {
                //neues item der liste hinzufügen
                TaskEntity receivedTask = (TaskEntity) data.getSerializableExtra(ARG_TASK);
                this.taskList.add(receivedTask);
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else {
            //??
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Creates toast for user feedback
     */
    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    /**
     * Starts detailActivity for result after click on new task button
     */
    private void callDetailViewForCreate(View view) {
        Intent detailviewIntent = new Intent(this, DetailActivity.class);
        //Started eine neue activity von der wir ein result zurück bekommen wollen
        startActivityForResult(detailviewIntent, CALL_DETAIL_VIEW_FOR_CREATE);
    }

    /**
     * Starts detailActivity for result after click on existing task
     */
    private void callDetailViewForEdit(TaskEntity selectedItem) {
        Intent callDetailViewForEdit = new Intent(this, DetailActivity.class);
        callDetailViewForEdit.putExtra(ARG_TASK, selectedItem);
        startActivityForResult(callDetailViewForEdit, CALL_DETAIL_VIEW_FOR_EDIT);
    }

    /**
     * Gets previously added tasks from "database" and add them to adapter
     */
    private void getTasksFromDB() {
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        taskList.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        taskList.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        taskList.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        taskList.add(new TaskEntity("Be happy", "Just enjoy lifeJust enjoy lifeJust enjoy lifeJust enjoy lifeJust enjoy lifeJust enjoy lifeJust enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        taskList.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        taskList.add(new TaskEntity("Be happy", "Just enjoy life", LocalDateTime.now(), LocalDateTime.now(), true));
        listViewAdapter.notifyDataSetChanged();
    }
}