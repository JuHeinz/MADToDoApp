package org.julheinz.data;

import android.app.Activity;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.Update;

import org.julheinz.entities.TaskEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations on task database via ROOM
 */
public class TaskCrudManager implements TaskCrudOperations {
    private static long idCount = 0;
    private final List<TaskEntity> taskList = new ArrayList<>();
    private DataAccessOperationsOnDb crudOnDb;

    public TaskCrudManager(Activity owner) {
        //create a database
        TaskDatabase db = Room.databaseBuilder(owner.getApplicationContext(), TaskDatabase.class, "task-db").build();
        this.crudOnDb = db.getDao();
    }

    /**
     * ROOM DAO Interface. Wird von ROOM zur Laufzeit implementiert, greift auf Datenbank zu
     */
    @Dao
    public interface DataAccessOperationsOnDb {
        @Insert
        long createTaskInDB(TaskEntity task);

        @Query("SELECT * FROM taskEntity")
        List<TaskEntity> readAllTasksFromDb();

        @Query("SELECT * FROM taskEntity WHERE id=:id")
        TaskEntity readTaskFromDb(long id);

        @Update
        void updateTaskInDb(TaskEntity task);

    }

    /**
     * Is used by ROOM to create database on basis of the class/entity we give it
     * Use converter because ROOM can't save LocalDateTimeObjects
     */
    @TypeConverters({LocalDateTimeConverter.class})
    @Database(entities = {TaskEntity.class}, version = 1)
    public static abstract class TaskDatabase extends RoomDatabase {
        public abstract DataAccessOperationsOnDb getDao();
    }

    @Override
    public TaskEntity createTask(TaskEntity taskEntity) {
        long id = crudOnDb.createTaskInDB(taskEntity);
        taskEntity.setId(id);
        taskList.add(taskEntity);
        return taskEntity;
    }

    @Override
    public TaskEntity readTask(long id) {
        return crudOnDb.readTaskFromDb(id);
    }

    @Override
    public List<TaskEntity> readAllTasks() {
        return crudOnDb.readAllTasksFromDb();
    }

    @Override
    public boolean updateTask(TaskEntity task) {
        crudOnDb.updateTaskInDb(task);
        //TODO: replace with actual boolean
        return true;
    }

    @Override
    public boolean deleteTask(long id) {
        //TODO: implement in room
        return false;
    }

    /**
     * Gets previously added tasks from "database" and add them to adapter
     */
    private List<TaskEntity> getDummyData() {
        List<TaskEntity> dummyList = new ArrayList<>();

        return dummyList;
    }
}
