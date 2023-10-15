package org.julheinz.madtodoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

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
        this.layoutResourceId = layoutResourceId;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TaskHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            //Set attributes of TaskHolder
            holder = new TaskHolder();
            holder.taskName = convertView.findViewById(R.id.taskNameOutput);
            holder.taskDescription = convertView.findViewById(R.id.descriptionOutput);
            holder.taskCheckBox = convertView.findViewById(R.id.taskCheckBox);
            holder.creationDate = convertView.findViewById(R.id.creationDateOutput);
            holder.dueDate = convertView.findViewById(R.id.dueDateOutput);
            holder.favCheckBox = convertView.findViewById(R.id.favOutput);

            convertView.setTag(holder);
        } else {
            holder = (TaskHolder) convertView.getTag();
        }

        TaskEntity task = taskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.taskCheckBox.setChecked(task.isDone());
        holder.taskDescription.setText(task.getTaskDescription());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy, H:mm");
        holder.creationDate.setText(task.getCreatedDate().format(formatter));
        holder.dueDate.setText(task.getDueDate().format(formatter));
        holder.favCheckBox.setChecked(task.isFav());


        // set the value of isDone according to the state of the checkbox
        holder.taskCheckBox.setOnClickListener(v -> {
            task.setDone(holder.taskCheckBox.isChecked());
        });

        return convertView;
    }

    static class TaskHolder {
        TextView taskName;
        TextView creationDate;
        TextView taskDescription;
        TextView dueDate;
        CheckBox taskCheckBox;
        CheckBox favCheckBox;
    }

}
