package org.julheinz.madtodoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.julheinz.entities.TaskEntity;
import org.julheinz.madtodoapp.databinding.ListItemBinding;

import java.util.List;

/**
 *  An adapter that manages what is shown in the list view. This is a custom adapter for my TaskEntity, extending an ArrayAdapter.
 */
public class TaskListAdapter extends ArrayAdapter<TaskEntity> {
    //TODO: replace with recyclerView
    private final LayoutInflater inflater;

    public TaskListAdapter(Context parent, int layoutIdOfListView, List<TaskEntity> taskList, LayoutInflater inflater) {
        super(parent, layoutIdOfListView, taskList);
        this.inflater = inflater;
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

        TaskEntity task = getItem(position); //data to be rendered
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
        ImageView favStar = taskView.findViewById(R.id.favOutput);
        if (!task.isFav()) {
            favStar.setImageResource(R.drawable.baseline_star_border_24);
        } else {
            favStar.setImageResource(R.drawable.baseline_star_24);
        }

        taskBinding.setTask(task);
        return taskView;
    }

}
