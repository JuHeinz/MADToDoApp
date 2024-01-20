package org.julheinz.data;

import org.julheinz.entities.TaskEntity;

import java.util.List;

public class SyncTaskCrudOperations implements TaskCrudOperations {

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
        List<TaskEntity> taskList;

        List<TaskEntity> remoteTasks = remoteOperations.readAllTasks();
        List<TaskEntity> localTasks = localOperations.readAllTasks();

        //  liegen lokale Todos vor, dann werden alle Todos auf Seiten der Web Applikation gelöscht und die lokalen Todos an die Web Applikation übertragen.
        if (!localTasks.isEmpty()) {
            remoteOperations.deleteAllTasks(false);

            for (TaskEntity localTask : localTasks) {
                remoteOperations.createTask(localTask);
            }
        } else { //liegen keine lokalen Todos vor, dann werden alle Todos von der Web Applikation auf die lokale Datenbank übertragen.
            for (TaskEntity remoteTask : remoteTasks) {

                localOperations.createTask(remoteTask);
                //TODO: only render once this has been done, else list will be empty cause repopulating local DB takes a second
            }
        }
        return localTasks;
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
        if(deleteLocalTasks){
            localOperations.deleteAllTasks(deleteLocalTasks);
        }else{
            remoteOperations.deleteAllTasks(deleteLocalTasks);
        }
        return true;
    }

}
