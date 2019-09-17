package io.chillout.redditgrab.Presenter;

import org.json.JSONObject;

import io.chillout.redditgrab.Model.Reddit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APICall {

    @GET("/new.json")
    Call<Reddit>  getNewPosts(@Query("sort") String sort , @Query( "limit") String limit);

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
