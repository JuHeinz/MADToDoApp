package org.julheinz.data;

import android.util.Log;

import org.julheinz.entities.TaskEntity;

import java.util.List;

/**
 * Synchronize task CRUD on local and remote databases.
 * Only instantiated if database server available.
 */
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

    /**
     * Local db has priority, app should display always the data in the local db.
     * If the local DB is empty, it gets automatically repopulated from the remote DB on app restart,
     * therefore it is okay that we don't refer to the remote DB here.
     **/
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

    /**
     * Empty either remote or local database.
     * @param deleteLocalTasks if true, delete local DB, if false, remote DB.
     */
    @Override
    public void deleteAllTasks(boolean deleteLocalTasks) {
        if (deleteLocalTasks) {
            localOperations.deleteAllTasks(true);
        } else {
            remoteOperations.deleteAllTasks(false);
        }
    }

    /**
     * Sync between local and remote database.
     * If the local DB is not empty, empty the remote db and populate it with the tasks from the local db
     * If the local DB is empty, get remote tasks and apply to local DB
     */
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

    /**
     * Add all tasks from the remote database to the local one.
     */
    private void populateLocalFromRemote() {
        List<TaskEntity> remoteTasks = remoteOperations.readAllTasks();
        for (TaskEntity remoteTask : remoteTasks) {
            localOperations.createTask(remoteTask);
        }
    }

    /**
     * Empty remote database and add all local tasks.
     */
    private void overwriteRemoteWithLocal() {
        List<TaskEntity> localTasks = localOperations.readAllTasks();
        remoteOperations.deleteAllTasks(false);
        for (TaskEntity localTask : localTasks) {
            remoteOperations.createTask(localTask);
        }
    }
}
