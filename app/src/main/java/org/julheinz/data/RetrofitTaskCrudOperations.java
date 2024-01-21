package org.julheinz.data;

import android.util.Log;

import org.julheinz.entities.TaskEntity;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RetrofitTaskCrudOperations implements TaskCrudOperations {
    private static final String LOG_TAG = RetrofitTaskCrudOperations.class.getSimpleName();

    private final TaskWebApiResource webApiResource;

    public RetrofitTaskCrudOperations() {
        //API base "retrofit" is filled with data needed to connect to API
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/") // 10.0.2.2. = this PC, if localhost, the phone would refer to itself
                .addConverterFactory(GsonConverterFactory.create()) //GsonConverter = convert Java to JSON
                .build();

        this.webApiResource = retrofit.create(TaskWebApiResource.class); //retrofit.create() returns an object that implements TaskWebApiResource
    }

    /**
     * Describes which operations are available by the web server, is implemented by a class retrofit created during runtime (dynamic proxy)
     */
    public interface TaskWebApiResource {

        @POST("/api/todos")
            // is translated to http request during runtime
        Call<TaskEntity> create(@Body TaskEntity task); // Call = like promise in js

        @GET("/api/todos")
        Call<List<TaskEntity>> readAll();

        @GET("/api/todos/{todoId}")
        Call<TaskEntity> read(@Path("todoId") long id); //@Path connects placeholder todoId in URL with parameter id

        @PUT("/api/todos/{todoId}")
        Call<TaskEntity> update(@Path("todoId") long id, @Body TaskEntity task);

        @DELETE("/api/todos/{todoId}")
        Call<Boolean> delete(@Path("todoId") long id);
    }

    @Override
    public TaskEntity createTask(TaskEntity task) {
        try {
            Log.d(LOG_TAG, "Trying to create task " + task.toString() + " in retrofit");
            TaskEntity result = webApiResource.create(task).execute().body();
            //.execute() = execute call
            //.body() = get body of response
            Log.d(LOG_TAG, "Created " + result.toString() + " in retrofit");
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskEntity readTask(long id) {
        try {
            return webApiResource.read(id).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TaskEntity> readAllTasks() {
        try {
            return webApiResource.readAll().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateTask(TaskEntity task) {
        try {
            webApiResource.update(task.getId(), task).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean deleteTask(TaskEntity task) {
        try {
            return webApiResource.delete(task.getId()).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteAllTasks(boolean deleteLocalTasks) {
        List<TaskEntity> tasks = this.readAllTasks();
        for (TaskEntity task : tasks) {
            this.deleteTask(task);
        }
        Log.i(LOG_TAG, "All remote tasks deleted");
        return true;
    }

    @Override
    public List<TaskEntity> syncData() {
        return null; //is not needed because it is only ever called on SyncTaskCrudOperations
    }
}
