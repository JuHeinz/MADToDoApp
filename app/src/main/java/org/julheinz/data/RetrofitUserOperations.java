package org.julheinz.data;

import android.util.Log;

import org.julheinz.entities.UserEntity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;

/**
 * HTTP requests to the database server that handles user authentication.
 */
public class RetrofitUserOperations {
    private static final String LOG_TAG = RetrofitUserOperations.class.getSimpleName();

    private final UserWebApiResource webApiResource;

    public RetrofitUserOperations() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/").addConverterFactory(GsonConverterFactory.create()).build();
        this.webApiResource = retrofit.create(UserWebApiResource.class);
    }

    /**
     * Trigger retrofit to send a HTTP request to the database server.
     * @param userEntity the password and email address to authenticate against
     * @return true if authentication successful, false if not
     */
    public Boolean authenticate(UserEntity userEntity) {
        try {
            Log.i(LOG_TAG, "Attempting to authenticate user");
            Boolean result = webApiResource.authenticate(userEntity).execute().body();
            Log.i(LOG_TAG, "Authenticate result:" + result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This interface is implemented by a class retrofit creates at run time.
     */
    public interface UserWebApiResource {
        @PUT("/api/users/auth")
        Call<Boolean> authenticate(@Body UserEntity user);
    }
}
