package com.cambodia.od4d.wastewaterleakagetracker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cambodia.od4d.wastewaterleakagetracker.R;
import com.cambodia.od4d.wastewaterleakagetracker.activities.ReportScreen;
import com.cambodia.od4d.wastewaterleakagetracker.model.Img;
import com.cambodia.od4d.wastewaterleakagetracker.model.PostModel;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class HomeScreen extends AppCompatActivity {
    Uri imageUri;
    private final int CAMERA_REQUEST = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

    }

    @SuppressLint("InlinedApi")
    public void doTrack(View view) {
        final Integer cam = 0x5;
        final Integer WRITE_EXST = 0x3;
        final Integer READ_EXST = 0x4;
        askPermission(Manifest.permission.CAMERA,cam);
        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            camera();
        }

    }
    public void camera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

     if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                Intent intent = new Intent(this,ReportScreen.class);
                intent.putExtra("img",imageUri.toString());
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Can't display this image!");
                builder.create().show();

            }

        }
    }
    private void askPermission(String string,int n){
        if (ContextCompat.checkSelfPermission(getApplication(),
                string)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    string)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{string},n);
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{string},n);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted


        }
    }



}
