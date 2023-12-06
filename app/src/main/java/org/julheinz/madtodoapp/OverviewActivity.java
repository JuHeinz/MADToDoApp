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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private static final int CALL_DETAIL_VIEW_FOR_SHOW = 20;
    private static final int CALL_DETAIL_VIEW_FOR_CREATE = 30;

    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();
    private Button addTaskBtn;
    private ListView listView;
    private final List<String> listItems = new ArrayList<>();

    private ArrayAdapter<String> listViewAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "created!");

        this.listItems.addAll(Arrays.asList("Dies", "Ist", "eine", "tolle", "app"));
        this.listView = findViewById(R.id.listView);

        //adapter managed was in der listview angezeigt wird. argumente: 1. von welcher activity wird es aufegrufen, 2: welches layout soll ein einzeles item haben, 3: liste der items
        this.listViewAdapter = new ArrayAdapter<>(this, R.layout.activity_overview_listitem_view, listItems);
        this.listView.setAdapter(this.listViewAdapter);

        //call detailView from click on item
        // parameter: 1: ? 2: item auf das geklickt wurde, 3: position des elements in ansicht = position des elements in List 4: id für aufruf direkt auf datenbank (optional)
        this.listView.setOnItemClickListener((adapterView, view, position, id) -> {
            //Hole das item der liste das der position des geklickten elements entspricht
            String selectedItem = this.listViewAdapter.getItem(position);
            Intent callDetailViewForShow = new Intent(this, DetailActivity.class);
            callDetailViewForShow.putExtra("itemName", selectedItem);
            startActivityForResult(callDetailViewForShow, CALL_DETAIL_VIEW_FOR_SHOW);
        });

        addTaskBtn = findViewById(R.id.addTaskBtn);
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
        if (requestCode == CALL_DETAIL_VIEW_FOR_SHOW) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String receivedItemName = data.getStringExtra("taskName");
                    toastMsg("Added task " + receivedItemName);
                    break;
                case Activity.RESULT_CANCELED:
                    toastMsg("Adding of task cancelled");
                    break;
            }
        } else if (requestCode == CALL_DETAIL_VIEW_FOR_CREATE) {
            if(resultCode == Activity.RESULT_OK){
                //neues item der liste hinzufügen
                String receivedItemName = data.getStringExtra("itemName");
                this.listItems.add(receivedItemName);
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else{
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
     * Starts detailActivity for result
     */
    private void callDetailViewForCreate(View view){
        Intent detailviewIntent = new Intent(this, DetailActivity.class);
        //Started eine neue activity von der wir eine result zurück bekommen wollen
        startActivityForResult(detailviewIntent, CALL_DETAIL_VIEW_FOR_CREATE);
    }


}