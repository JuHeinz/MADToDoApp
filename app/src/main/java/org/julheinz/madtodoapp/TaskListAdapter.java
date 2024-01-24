package org.julheinz.madtodoapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.julheinz.entities.TaskEntity;
import org.julheinz.madtodoapp.databinding.ListItemBinding;
import org.julheinz.viewmodel.OverviewViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  An adapter that manages what is shown in the list view. This is a custom adapter for my TaskEntity, extending an ArrayAdapter.
 */
public class TaskListAdapter extends ArrayAdapter<TaskEntity> {
    private static final String LOG_TAG = TaskListAdapter.class.getSimpleName();

    private final LayoutInflater inflater;
    TaskEntity task;
    final OverviewViewModel viewModel;
    public TaskListAdapter(Context parent, int layoutIdOfListView, List<TaskEntity> taskList, LayoutInflater inflater, OverviewViewModel viewModel) {
        super(parent, layoutIdOfListView, taskList);
        this.inflater = inflater;
        this.viewModel = viewModel;
    }

    /**
     * Get the view for a single item by inflating a layout and creating a new view or using a recycled view.
     * Overrides method of ArrayAdapter produce custom view, not just TextView.
     * @param position the position of the task in the list, used to get the TaskEntity object.
     * @param existingViewToBeRecycled view for a task that can be recycled
     * @param parent view that calls this method
     * @return The view for a task in the list, either created anew or recycled from earlier, layout defined by list_item.xml
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View existingViewToBeRecycled, @NonNull ViewGroup parent) {

        task = getItem(position); //data to be rendered
        View taskView; //view that displays the data
        ListItemBinding taskBinding; //data binder

        if (existingViewToBeRecycled != null) {
            taskView = existingViewToBeRecycled; //if there is a view to be recycled, use it.
            //there will always be an already inflated view because of the else statement.
            taskBinding = (ListItemBinding) taskView.getTag(); //get data binder from recycled view
        } else {
            taskBinding = DataBindingUtil.inflate(inflater, R.layout.list_item, null, false); //if there is no view to be recycled, inflate a new one
            taskView = taskBinding.getRoot(); //set task view to the view created by inflating
            taskView.setTag(taskBinding); //set data binding for view
        }

        //Due date output
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        Date dateFormatted = new Date(task.getDueDate());
        TextView dueDateOutput = taskView.findViewById(R.id.dueDateOutput);
        //change color red if task is overdue
        if(System.currentTimeMillis() > task.getDueDate()){
            dueDateOutput.setTextColor(Color.RED);
        }
        dueDateOutput.setText(df.format(dateFormatted));
        taskBinding.setTask(task);
        taskBinding.setActivityViewModel(viewModel); // make it so that the databinding class for the listitem has access to the viewmodel of Overview Activity
        return taskView;
    }


}
