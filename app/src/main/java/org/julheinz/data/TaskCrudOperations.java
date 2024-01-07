package org.julheinz.data;

import org.julheinz.entities.TaskEntity;

import java.util.List;

public interface TaskCrudOperations {

    TaskEntity createTask(TaskEntity taskEntity);

    TaskEntity readTask(long id);

    List<TaskEntity> readAllTasks();

    void updateTask(TaskEntity task);

    boolean deleteTask(TaskEntity task);
}
