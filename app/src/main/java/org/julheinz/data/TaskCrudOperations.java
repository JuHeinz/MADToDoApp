package org.julheinz.data;

import org.julheinz.entities.TaskEntity;

import java.util.List;

/**
 * Interface of CRUD Operations. Implemented by several classes that all deal with CRUD.
 * This way, an Object of the type of this Interface can be declared and instantiated with implementing classes that execute these commands their own ways,
 * e.g. using different database frameworks.
 */
public interface TaskCrudOperations {

    TaskEntity createTask(TaskEntity task);

    List<TaskEntity> readAllTasks();

    void updateTask(TaskEntity task);

    void deleteTask(TaskEntity task);

    void deleteAllTasks(boolean deleteLocalTasks);

    List<TaskEntity> syncData();
}
