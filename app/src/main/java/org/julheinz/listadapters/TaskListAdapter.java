package org.julheinz.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.julheinz.entities.TaskEntity;
import org.julheinz.madtodoapp.OverviewActivity;
import org.julheinz.madtodoapp.R;
import org.julheinz.madtodoapp.databinding.ListItemBinding;
import org.julheinz.viewmodel.OverviewViewModel;

import java.util.List;


public class TaskListAdapter extends ArrayAdapter<TaskEntity> {

    private final LayoutInflater inflater;
    TaskEntity task;
    final OverviewViewModel viewModel;

    final OverviewActivity activity;

    public TaskListAdapter(Context parent, int layoutIdOfListView, List<TaskEntity> taskList, LayoutInflater inflater, OverviewViewModel viewModel) {
        super(parent, layoutIdOfListView, taskList);
        this.inflater = inflater;
        this.viewModel = viewModel;
        this.activity = (OverviewActivity) parent;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View existingViewToBeRecycled, @NonNull ViewGroup parent) {

        task = getItem(position);
        View taskView;
        ListItemBinding taskBinding;

        if (existingViewToBeRecycled != null) {
            taskView = existingViewToBeRecycled;
            taskBinding = (ListItemBinding) taskView.getTag();
        } else {
            taskBinding = DataBindingUtil.inflate(inflater, R.layout.list_item, null, false);
            taskView = taskBinding.getRoot();
            taskView.setTag(taskBinding);
        }

        taskBinding.setTask(task);
        taskBinding.setViewModel(viewModel);
        taskBinding.setActivity(activity);
        return taskView;
    }
}
