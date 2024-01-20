package org.julheinz.data;

import org.julheinz.entities.TaskEntity;

import java.util.List;

public class SyncTaskCrudOperations implements TaskCrudOperations{

    private TaskCrudOperations localOperations;
    private TaskCrudOperations remoteOperations;

    public SyncTaskCrudOperations(TaskCrudOperations localOperations, TaskCrudOperations remoteOperations) {
        this.localOperations = localOperations;
        this.remoteOperations = remoteOperations;
    }

    @Override
    public TaskEntity createTask(TaskEntity task) {
        task = localOperations.createTask(task); //first create in local DB, return a new task bc local DB sets id
        remoteOperations.createTask(task); //then add to remote DB
        return task;
    }

    @Override
    public TaskEntity readTask(long id) {
        return localOperations.readTask(id);
    }

    @Override
    public List<TaskEntity> readAllTasks() {
        return localOperations.readAllTasks();
    }

    @Override
    public boolean updateTask(TaskEntity task) {
        localOperations.updateTask(task);
        remoteOperations.updateTask(task);
        return false;
    }

    @Override
    public boolean deleteTask(TaskEntity task) {
        localOperations.deleteTask(task);
        remoteOperations.deleteTask(task);
        return true;
    }
}
