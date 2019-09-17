package io.chillout.redditgrab.Presenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.chillout.redditgrab.Model.Reddit;
import io.chillout.redditgrab.R;
import io.chillout.redditgrab.View.DataAdapter;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Call<Reddit> redditCall;
    RecyclerView redditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        redditView=findViewById(R.id.recyclerview_main);

        redditView.setHasFixedSize(false);

        redditView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com/r/dankmemes/")
                .addConverterFactory(GsonConverterFactory.create(gson))

                .client(httpClient).build();


        APICall apiCall = retrofit.create(APICall.class);

        redditCall = apiCall.getNewPosts("new", "3");


        redditCall.enqueue(new Callback<Reddit>() {
            @Override
            public void onResponse(Call<Reddit> call, Response<Reddit> response) {
                Log.v("reddit_call", response.body().toString());
                DataAdapter dataAdapter =new DataAdapter(response.body(),MainActivity.this);
                redditView.setAdapter(dataAdapter);
            }

            @Override
            public void onFailure(Call<Reddit> call, Throwable t) {

            }
        });


    }


}
