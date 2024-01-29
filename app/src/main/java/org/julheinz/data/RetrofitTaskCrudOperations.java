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
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.webApiResource = retrofit.create(TaskWebApiResource.class);
    }


    public interface TaskWebApiResource {

        @POST("/api/todos")
        Call<TaskEntity> create(@Body TaskEntity task);

        @GET("/api/todos")
        Call<List<TaskEntity>> readAll();

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
            Log.d(LOG_TAG, "Created " + result + " in retrofit");
            return result;
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
    public void updateTask(TaskEntity task) {
        try {
            webApiResource.update(task.getId(), task).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTask(TaskEntity task) {
        try {
            webApiResource.delete(task.getId()).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllTasks(boolean deleteLocalTasks) {
        List<TaskEntity> tasks = this.readAllTasks();
        for (TaskEntity task : tasks) {
            this.deleteTask(task);
        }
        Log.i(LOG_TAG, "All remote tasks deleted");
    }

    @Override
    public List<TaskEntity> syncData() {
        return null;
    }
}
