package org.julheinz.data;

import android.util.Log;

import org.julheinz.entities.UserEntity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public class RetrofitUserOperations {
    private static final String LOG_TAG = RetrofitUserOperations.class.getSimpleName();

    private final UserWebApiResource webApiResource;

    public RetrofitUserOperations(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.webApiResource = retrofit.create(UserWebApiResource.class);
    }

    public interface UserWebApiResource {

        @PUT("/api/users/auth")
        Call<Boolean> authenticate(@Body UserEntity user);

        @PUT("/api/users/prepare")
        Call<Boolean> prepare(@Body UserEntity user);
    }

    public boolean createUser(UserEntity userEntity){
        try{
            Log.i(LOG_TAG, "Attempting to create user");
            Boolean result = webApiResource.prepare(userEntity).execute().body();
            Log.i(LOG_TAG, "Create result:" + result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean authenticate(UserEntity userEntity){
        try{
            Log.i(LOG_TAG, "Attempting to authenticate user");
            Boolean result = webApiResource.authenticate(userEntity).execute().body();
            Log.i(LOG_TAG, "Authenticate result:" + result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
