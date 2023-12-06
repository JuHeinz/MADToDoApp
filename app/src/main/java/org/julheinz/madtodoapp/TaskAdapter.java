package org.julheinz.madtodoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;

import org.julheinz.entities.TaskEntity;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<TaskEntity> {

    private Context context;
    private int layoutResourceId;
    private List<TaskEntity> taskList;

    public TaskAdapter(Context context, int layoutResourceId, List<TaskEntity> taskList) {
        super(context, layoutResourceId, taskList);
        this.context = context;
        this.layoutResourceId = R.layout.list_item;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
        ListItem holder;

        if (listItemView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            listItemView = inflater.inflate(layoutResourceId, parent, false);

            //Set attributes of view
            holder = new ListItem();
            holder.taskName = listItemView.findViewById(R.id.taskNameOutput);
            holder.taskDescription = listItemView.findViewById(R.id.descriptionOutput);
            holder.taskCheckBox = listItemView.findViewById(R.id.taskCheckBox);
            holder.creationDate = listItemView.findViewById(R.id.creationDateOutput);
            holder.dueDate = listItemView.findViewById(R.id.dueDateOutput);
            holder.favOutput = listItemView.findViewById(R.id.favOutput);
            holder.openBtn = listItemView.findViewById(R.id.openBtn);
            listItemView.setTag(holder);
        } else {
            holder = (ListItem) listItemView.getTag();
        }

        TaskEntity task = taskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.taskCheckBox.setChecked(task.isDone());
        holder.taskDescription.setText(task.getTaskDescription());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy, H:mm");
        holder.creationDate.setText(task.getCreatedDate().format(formatter));
        holder.dueDate.setText(task.getDueDate().format(formatter));

        //Set star according to fav status
        holder.favOutput.setImageResource(task.isFav() ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);

        // set the value of isDone according to the state of the checkbox
        holder.taskCheckBox.setOnClickListener(v -> {
            task.setDone(holder.taskCheckBox.isChecked());
        });

        return listItemView;
    }




}
