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
    private String LOG_TAG = TaskApplication.class.getSimpleName();
    private TaskCrudOperations crudOperations;

    /**
     * Instantiate CRUD operations once at app start, not in OverviewActivity where it would be instantiated every time the activity is instantiated
     */

    public Future<TaskCrudOperations> getCrudOperations() {
        CompletableFuture<TaskCrudOperations> returnValueFutureObj = new CompletableFuture<>();
        //check if there already are crud operations, because this method is called in an activity, this method gets called whenever the activity restarts, e.g. on screen orientation flip
        if (this.crudOperations != null) {
            returnValueFutureObj.complete(this.crudOperations);
        } else {

            TaskCrudOperations localOperations = new RoomTaskCrudOperations(this);
            TaskCrudOperations remoteOperations = new RetrofitTaskCrudOperations();
            TaskCrudOperations syncedOperations = new SyncTaskCrudOperations(localOperations, remoteOperations);

            new Thread(() -> { //new thread for network access because else this runs on UI thread
                try {
                    //check if connection is open
                    HttpURLConnection connection = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/todos").openConnection();
                    Log.d(LOG_TAG, "created connections" + connection);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(500);
                    connection.setReadTimeout(500);
                    connection.getInputStream(); //body of server response
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
        //If we didn't use a future this would always be null because getting the operations runs on another thread (wait 500ms for server..)
        return returnValueFutureObj; // it the completable future gets completed with the right data when it is ready, it gets returned immediately tho.
    }

    public boolean isInOfflineMode(){
        return this.crudOperations instanceof RoomTaskCrudOperations;
    }

    /**
     * Called on app start
     */
    @Override
    public void onCreate() {

        super.onCreate();
    }
}
