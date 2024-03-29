package org.julheinz.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import org.julheinz.entities.TaskEntity;

import java.util.List;

/**
 * CRUD operations on database local to device via ROOM framework.
 */
public class RoomTaskCrudOperations implements TaskCrudOperations {

    private static final String LOG_TAG = RoomTaskCrudOperations.class.getSimpleName();

    private final DatabaseInteractions crudOnDb;

    public RoomTaskCrudOperations(Context owner) {
        //create a database
        TaskDatabase db = Room.databaseBuilder(owner.getApplicationContext(), TaskDatabase.class, "task-db").build();
        this.crudOnDb = db.getDao();
    }

    /**
     * ROOM DAO Interface. Implemented by ROOM via code generation to access database.
     */
    @Dao //marks interface as DataAccessObjects, meaning the class ROOM should implement by the class it creates at compile time.
    public interface DatabaseInteractions {
        @Insert
        long createTaskInDB(TaskEntity task);

        @Query("SELECT * FROM taskEntity")
        List<TaskEntity> readAllTasksFromDb();

        @Update
        void updateTaskInDb(TaskEntity task);

        @Delete
        void deleteTask(TaskEntity task);
    }

    /**
     * Create a RoomDatabase from the TaskEntity class.
     */
    @Database(entities = {TaskEntity.class}, version = 1)
    public static abstract class TaskDatabase extends RoomDatabase {
        public abstract DatabaseInteractions getDao();
    }

    @Override
    public TaskEntity createTask(TaskEntity task) {
        long id = crudOnDb.createTaskInDB(task);
        task.setId(id);
        return task;
    }

    @Override
    public List<TaskEntity> readAllTasks() {
        return crudOnDb.readAllTasksFromDb();
    }

    @Override
    public void updateTask(TaskEntity task) {
        crudOnDb.updateTaskInDb(task);
        Log.i(LOG_TAG, "Task updated in local database: " + task.toString());
    }

    @Override
    public void deleteTask(TaskEntity task) {
        crudOnDb.deleteTask(task);
        Log.i(LOG_TAG, "Task deleted in local database: " + task.toString());
    }

    @Override
    public void deleteAllTasks(boolean deleteLocalTasks) {
        List<TaskEntity> tasks = this.readAllTasks();
        for (TaskEntity task : tasks) {
            this.deleteTask(task);
        }
        Log.i(LOG_TAG, "All local tasks deleted");
    }

    @Override
    public List<TaskEntity> syncData() {
        return null;
    }
}
