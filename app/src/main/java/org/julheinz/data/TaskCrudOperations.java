package org.julheinz.data;

import org.julheinz.entities.TaskEntity;

import java.util.List;


public interface TaskCrudOperations {

    TaskEntity createTask(TaskEntity task);

    List<TaskEntity> readAllTasks();

    void updateTask(TaskEntity task);

    void deleteTask(TaskEntity task);

    void deleteAllTasks(boolean deleteLocalTasks);

    List<TaskEntity> syncData();
}
