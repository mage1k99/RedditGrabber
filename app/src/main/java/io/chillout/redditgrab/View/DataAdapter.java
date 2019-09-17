package io.chillout.redditgrab.View;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.chillout.redditgrab.Model.Reddit;
import io.chillout.redditgrab.Presenter.APICall;
import io.chillout.redditgrab.R;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>
{

    Reddit reddit;
    Context context;
    private LayoutInflater inflater;
    Retrofit retrofit;
    APICall downloadCall;
    Call<ResponseBody> downloadFile;

    public DataAdapter(Reddit reddit, Context context) {
        this.reddit = reddit;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imView = inflater.inflate(R.layout.recycle_item, parent, false);

         retrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com/r/dankmemes/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(new OkHttpClient()).build();

         downloadCall = retrofit.create(APICall.class);


        return new ViewHolder(imView);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.captionText.setText(reddit.data.children.get(position).data.title);
        Glide.with(context).load(reddit.data.children.get(position).data.thumbnail).fitCenter().into(holder.postImage);
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile=downloadCall.downloadFileWithDynamicUrlSync(reddit.data.children.get(position).data.url);
                downloadFile.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Boolean fileDownloaded=writeResponseBodyToDisk(response.body(),reddit.data.children.get(position).data.id);
                        if(fileDownloaded)
                        Log.v("do","doo");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return reddit.data.children.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage,shareButton,downloadButton,redditButton /*why the hell its's here?*/ ;
        TextView captionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            captionText=itemView.findViewById(R.id.postCaption);
            postImage=itemView.findViewById(R.id.imageGot);
            shareButton=itemView.findViewById(R.id.share_button);
            downloadButton=itemView.findViewById(R.id.download_button);
            redditButton=itemView.findViewById(R.id.view_in_reddit);
        }
    }



    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.DIRECTORY_DOWNLOADS + File.separator + "kk" + File.separator + fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("downloading", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e("exceptionnnn",e.getMessage());

            return  false;
        }

    }

}
