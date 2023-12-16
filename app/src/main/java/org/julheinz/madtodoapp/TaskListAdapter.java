package org.julheinz.madtodoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.julheinz.entities.TaskEntity;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskListAdapter extends ArrayAdapter<TaskEntity> {

    private final Context context;
    private final int layoutResourceId;

    public TaskListAdapter(Context context, int layoutResourceId, List<TaskEntity> taskList) {
        super(context, layoutResourceId, taskList);
        this.context = context;
        this.layoutResourceId = R.layout.list_item;
    }

    /**
     * create/get the view for a single item by inflating the layout
     * @param position the position of the task in the list
     * @param convertView
     * @param parent
     * @return The view for a task in the list
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TaskEntity task = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup itemView = (ViewGroup) inflater.inflate(layoutResourceId, parent, false);

        TextView taskName = itemView.findViewById(R.id.taskNameOutput);
        taskName.setText(task.getTitle());

        CheckBox taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
        taskCheckBox.setChecked(task.isDone());

        TextView taskDescription = itemView.findViewById(R.id.descriptionOutput);
        taskDescription.setText(task.getDescription());

        TextView dueDateOutput = itemView.findViewById(R.id.dueDateOutput);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy, H:mm");
        String dueDate = formatter.format(task.getDueDate());
        dueDateOutput.setText(dueDate);

        ImageView favOutput = itemView.findViewById(R.id.favOutput);
        //Set star according to fav status
        favOutput.setImageResource(task.isFav() ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);

        // set the value of isDone according to the state of the checkbox
        taskCheckBox.setOnClickListener(v -> {
            task.setDone(taskCheckBox.isChecked());
        });

        return itemView;
    }




}
