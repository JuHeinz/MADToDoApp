package org.julheinz.madtodoapp;

import android.app.Application;
import android.util.Log;

import org.julheinz.data.RetrofitTaskCrudOperations;
import org.julheinz.data.RoomTaskCrudOperations;
import org.julheinz.data.SyncTaskCrudOperations;
import org.julheinz.data.TaskCrudOperations;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class TaskApplication extends Application {
    private final String LOG_TAG = TaskApplication.class.getSimpleName();
    private TaskCrudOperations crudOperations;



    public Future<TaskCrudOperations> getCrudOperations() {
        CompletableFuture<TaskCrudOperations> returnValueFutureObj = new CompletableFuture<>();
        if (this.crudOperations != null) {
            returnValueFutureObj.complete(this.crudOperations);
        } else {

            TaskCrudOperations localOperations = new RoomTaskCrudOperations(this);
            TaskCrudOperations remoteOperations = new RetrofitTaskCrudOperations();
            TaskCrudOperations syncedOperations = new SyncTaskCrudOperations(localOperations, remoteOperations);

            new Thread(() -> {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/todos").openConnection();
                    Log.d(LOG_TAG, "Created connection: " + connection);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(500);
                    connection.setReadTimeout(500);
                    connection.getInputStream();
                    this.crudOperations = syncedOperations;
                    returnValueFutureObj.complete(crudOperations);
                    Log.d(LOG_TAG, "Using synced operations");
                } catch (Exception e) {
                    e.printStackTrace();
                    this.crudOperations = localOperations;
                    returnValueFutureObj.complete(crudOperations);
                    Log.d(LOG_TAG, "Using local operations");
                }
            }).start();
        }
        return returnValueFutureObj;
    }

    public boolean isInOfflineMode(){
        return this.crudOperations instanceof RoomTaskCrudOperations;
    }


    @Override
    public void onCreate() {

        super.onCreate();
    }
}
