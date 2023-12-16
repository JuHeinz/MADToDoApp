package org.julheinz.data;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskCrudManager implements TaskCrudOperations {

    public TaskCrudManager() {
        List<TaskEntity> dummyData = this.getDummyData();
        dummyData.forEach(this::createTask);
    }

    private static long idCount = 0;
    private final List<TaskEntity> taskList = new ArrayList<>();

    @Override
    public TaskEntity createTask(TaskEntity taskEntity) {
        taskEntity.setId(++idCount);
        taskList.add(taskEntity);
        return taskEntity;
    }

    @Override
    public TaskEntity readTask(long id) {
        return null;
    }

    @Override
    public List<TaskEntity> readAllTasks() {
        return taskList;
    }

    @Override
    public boolean updateTask(TaskEntity task) {
        return false;
    }

    @Override
    public boolean deleteTask(long id) {
        return false;
    }

    /**
     * Gets previously added tasks from "database" and add them to adapter
     */
    private List<TaskEntity> getDummyData() {
        List<TaskEntity> dummyList = new ArrayList<>();
        dummyList.add(new TaskEntity("Open App", "Open this app", LocalDateTime.now(), LocalDateTime.now(), false));
        dummyList.add(new TaskEntity("Code in Android", "Why not?", LocalDateTime.now(), LocalDateTime.now(), false));
        dummyList.add(new TaskEntity("Eat snacks", "Maybe something healthy", LocalDateTime.now(), LocalDateTime.now(), true));
        dummyList.add(new TaskEntity("Be happy!", "Just enjoy life :)", LocalDateTime.now(), LocalDateTime.now(), true));

        return dummyList;
    }
}
