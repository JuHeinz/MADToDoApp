package org.julheinz.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.Update;

import org.julheinz.entities.TaskEntity;


import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations on task database via ROOM
 */
public class RoomTaskCrudOperations implements TaskCrudOperations {


    private final List<TaskEntity> taskList = new ArrayList<>();
    private final DataAccessOperationsOnDb crudOnDb;

    public RoomTaskCrudOperations(Context owner) {
        //create a database
        TaskDatabase db = Room.databaseBuilder(owner.getApplicationContext(), TaskDatabase.class, "task-db").build();
        this.crudOnDb = db.getDao();
    }

    /**
     * ROOM DAO Interface. Implemented by ROOM via code generation to access database.
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

        @Delete
        void deleteTask(TaskEntity task);
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
    public TaskEntity createTask(TaskEntity task) {
        long id = crudOnDb.createTaskInDB(task);
        task.setId(id);
        taskList.add(task);
        return task;
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
        Log.i("Task updated: ", task.toString());
        return true;
    }

    @Override
    public boolean deleteTask(TaskEntity task) {
        crudOnDb.deleteTask(task);
        Log.i("Task deleted: ", task.toString());
        return false;
    }

}
