package org.julheinz.madtodoapp;

import android.app.Application;

import org.julheinz.data.RetrofitTaskCrudOperations;
import org.julheinz.data.RoomTaskCrudOperations;
import org.julheinz.data.TaskCrudOperations;

public class TaskApplication extends Application {

    private TaskCrudOperations crudOperations;
    public TaskCrudOperations getCrudOperations() {
        return crudOperations;
    }

    public void setCrudOperations(TaskCrudOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    /**
     * Called on app start
     */
    @Override
    public void onCreate(){

        super.onCreate();
        //Instantiate CRUD operations once at app start, not in OverviewActivity where it would be instantiated every time the activity is instantiated
        //TODO: Sync between local and remote DB
        //this.crudOperations = new RetrofitTaskCrudOperations();
        this.crudOperations = new RoomTaskCrudOperations(this);

    }

}
