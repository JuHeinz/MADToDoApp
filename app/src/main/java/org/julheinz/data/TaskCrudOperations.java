package org.julheinz.data;

import org.julheinz.entities.TaskEntity;

import java.util.List;

public interface TaskCrudOperations {

    TaskEntity createTask(TaskEntity task);

    TaskEntity readTask(long id);

    List<TaskEntity> readAllTasks();

    boolean updateTask(TaskEntity task);

    boolean deleteTask(TaskEntity task);

    boolean deleteAllTasks(boolean deleteLocalTasks);

    List<TaskEntity> syncData();

}
