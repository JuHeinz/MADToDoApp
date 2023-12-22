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
import androidx.databinding.DataBindingUtil;

import org.julheinz.entities.TaskEntity;
import org.julheinz.madtodoapp.databinding.DetailViewBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DetailviewActivity extends AppCompatActivity {
    private static final String LOG_TAG = OverviewActivity.class.getSimpleName();


    private EditText dueDateInput;

    // Durch Databinding Framework automatisch generierte Klasse.
    // Wird so benannt wie die XML Datei + Binding (detail_view.xml -> DetailViewBinding.java)
    private DetailViewBinding itemBinding;
    private ImageButton favBtn;
    private boolean isFav;

    public static final String ARG_TASK = "task";
    private TaskEntity task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "created!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        this.itemBinding = DataBindingUtil.setContentView(this, R.layout.detail_view);
        this.itemBinding.setActivity(this);

        Intent detailViewIntentFromOverview = getIntent();
        /* Wenn wir durch klicken auf eine bereits erstellte task auf diese view kommen,
         wird der name der bereits erstellten task übergeben
            Neue instanz der klasse, nicht die selbe weil serializable */
        this.task = (TaskEntity) detailViewIntentFromOverview.getSerializableExtra(ARG_TASK);



        CheckBox isDoneCheckBox = findViewById(R.id.isDoneCheckBox);
        Button addTaskBtn = findViewById(R.id.addTaskBtn);
        dueDateInput = findViewById(R.id.dueDateInput);
        favBtn = findViewById(R.id.favBtn);
        TextView createdDateOutput = findViewById(R.id.createdDateOutput);


        //wenn wir durch den "new task" button hier her gekommen sind, dann müssen wir eine neue task erstellen
        if(this.task == null){
            this.task = new TaskEntity();
        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy, H:mm");



            String dueDate = formatter.format(task.getDueDate());
            dueDateInput.setText(dueDate);

            String createdDate = formatter.format(task.getCreatedDate());
            createdDateOutput.setText(createdDate);
            isDoneCheckBox.setChecked(task.isDone());
        }


        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this::cancelEdit);

    }



    /**
     * Adds TaskEntity to to adapter and triggers UI change
     */
    public void saveTask() {

            Log.d("Task Created:", task.toString());

            //Intent um auf die Activity zurückzukehren, die diese augerufen hat und ihr daten mitgeben, die dann zum adapter hinzugefügt werdeb
            Intent returnToCallerWithValueIntent = new Intent();
            returnToCallerWithValueIntent.putExtra(ARG_TASK, this.task);
            setResult(Activity.RESULT_OK, returnToCallerWithValueIntent);

            //close activity and return to caller
            finish();
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

    public TaskEntity getTaskEntity() {
        return task;
    }

    public void setTaskEntity(TaskEntity task) {
        this.task = task;
    }
}
