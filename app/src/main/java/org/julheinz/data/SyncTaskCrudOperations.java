package org.julheinz.data;

import android.util.Log;

import org.julheinz.entities.TaskEntity;

import java.util.List;


public class SyncTaskCrudOperations implements TaskCrudOperations {
    private static final String LOG_TAG = SyncTaskCrudOperations.class.getSimpleName();
    private final TaskCrudOperations localOperations;
    private final TaskCrudOperations remoteOperations;

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
    public List<TaskEntity> readAllTasks() {
        return localOperations.readAllTasks();
    }

    @Override
    public void updateTask(TaskEntity task) {
        localOperations.updateTask(task);
        remoteOperations.updateTask(task);
    }

    @Override
    public void deleteTask(TaskEntity task) {
        localOperations.deleteTask(task);
        remoteOperations.deleteTask(task);
    }


    @Override
    public void deleteAllTasks(boolean deleteLocalTasks) {
        if (deleteLocalTasks) {
            localOperations.deleteAllTasks(true);
        } else {
            remoteOperations.deleteAllTasks(false);
        }
    }


    @Override
    public List<TaskEntity> syncData() {
        Log.d(LOG_TAG, "Syncing data");
        List<TaskEntity> localTasks = localOperations.readAllTasks();

        if (localTasks.isEmpty()) {
            Log.i(LOG_TAG, "Local database is empty. Attempting to repopulate from remote.");
            populateLocalFromRemote();
        } else {
            Log.i(LOG_TAG, "Overwriting remote db with data from local db");
            overwriteRemoteWithLocal();
        }
        return localOperations.readAllTasks(); //return (potentially refreshed) data in local db
    }


    private void populateLocalFromRemote() {
        List<TaskEntity> remoteTasks = remoteOperations.readAllTasks();
        for (TaskEntity remoteTask : remoteTasks) {
            localOperations.createTask(remoteTask);
        }
    }


    private void overwriteRemoteWithLocal() {
        List<TaskEntity> localTasks = localOperations.readAllTasks();
        remoteOperations.deleteAllTasks(false);
        for (TaskEntity localTask : localTasks) {
            remoteOperations.createTask(localTask);
        }
    }
}
