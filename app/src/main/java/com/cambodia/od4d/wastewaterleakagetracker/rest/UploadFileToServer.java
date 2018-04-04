package com.cambodia.od4d.wastewaterleakagetracker.rest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import com.cambodia.od4d.wastewaterleakagetracker.config.Configs;
import com.cambodia.od4d.wastewaterleakagetracker.model.KeyValue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class UploadFileToServer extends AsyncTask<Void, Integer, String> {

    private float totalSize = 0;
    private ArrayList<KeyValue> filePaths;
    private OnExecutionProgress onExecutionProgress;
    private String URL;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    public interface OnExecutionProgress {
        void onPreExecute();

        void onProgressUpdating(int progress);

        void onCompleteExecution(String response);
    }

    private void setOnExecutionProgress(OnExecutionProgress onExecutionProgress) {
        this.onExecutionProgress = onExecutionProgress;
    }

    public UploadFileToServer(ArrayList<KeyValue> filePaths, String URL, Activity activity, OnExecutionProgress onExecutionProgress) {
        this.URL = URL;
        this.activity = activity;
        this.filePaths = filePaths;
        setOnExecutionProgress(onExecutionProgress);
    }

    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        //  progressBar.setProgress(0);
        onExecutionProgress.onPreExecute();
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        onExecutionProgress.onProgressUpdating(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String responseString;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);
        httppost.addHeader(Configs.instance.authorizedKey, Configs.instance.authorizedValue);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / totalSize * 100)));
                        }
                    });

            File image = new File(filePaths.get(0).getValue());

            Bitmap b = BitmapFactory.decodeFile(filePaths.get(0).getValue());

            if (b == null){
                InputStream image_stream = activity.getContentResolver().openInputStream(Uri.parse(filePaths.get(0).getValue()));

                b = BitmapFactory.decodeStream(image_stream);
            }

            Bitmap out = getResizedBitmap(b);
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(image);
                out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                b.recycle();
                out.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FileBody body = new FileBody(image);

            for (int i = 0; i < filePaths.size(); i++) {
                if (i == 0) {
                    entity.addPart(filePaths.get(0).getKey(), body);
                }
                entity.addPart(filePaths.get(i).getKey(), new StringBody(filePaths.get(i).getValue()));
            }

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);
//            httppost.setEntity(object);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
//            responseString = statusCode + "";
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);

            } else {
                responseString = "Error occurred! Http Status Code: " + statusCode;
            }

        } catch (Exception e) {
            responseString = e.toString();
        }

        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("GG", "Response from server: " + result);

        onExecutionProgress.onCompleteExecution(result);

        super.onPostExecute(result);
    }

    private Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = 1000;
            height = (int) (width / bitmapRatio);
        } else {
            height = 1000;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


}
