package org.julheinz.data;

import android.util.Log;

import org.julheinz.entities.TaskEntity;

import java.util.List;

/**
 * CRUD operations for the tasks, synchronized for local and remote databases if remote available.
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

    @Override
    public TaskEntity readTask(long id) {
        return localOperations.readTask(id);
    }

    /**
     * Get list of tasks always from local db because it has priority. Local db gets only modified when it is empty.
     **/
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

    @Override
    public boolean deleteAllTasks(boolean deleteLocalTasks) {
        if (deleteLocalTasks) {
            localOperations.deleteAllTasks(true);
        } else {
            remoteOperations.deleteAllTasks(false);
        }
        return true;
    }

    /**
     * If the local DB is not empty, empty the remote db and populate it with the tasks from the local db
     * If the local DB is empty, get remote tasks and apply to local DB
     */
    @Override
    public List<TaskEntity> syncData() {
        Log.d(LOG_TAG, "Syncing data");
        List<TaskEntity> localTasks = localOperations.readAllTasks();

        if (localTasks.isEmpty()) {
            Log.i(LOG_TAG, "Local database is empty. Attempting to get from remote.");
            populateLocalFromRemote();
        } else {
            Log.i(LOG_TAG, "Overwriting remote db with data from local db");
            overwriteRemoteWithLocal();
        }
        return localOperations.readAllTasks();
    }

    /**
     * add all tasks from the remote database to the local one
     */
    private void populateLocalFromRemote() {
        List<TaskEntity> remoteTasks = remoteOperations.readAllTasks();
        for (TaskEntity remoteTask : remoteTasks) {
            localOperations.createTask(remoteTask);
        }
    }

    /**
     * delete remote database and add all local tasks
     */
    private void overwriteRemoteWithLocal() {
        List<TaskEntity> localTasks = localOperations.readAllTasks();

        remoteOperations.deleteAllTasks(false);
        for (TaskEntity localTask : localTasks) {
            remoteOperations.createTask(localTask);
        }
    }
}
