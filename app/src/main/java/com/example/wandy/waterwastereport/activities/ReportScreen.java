package com.example.wandy.waterwastereport.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.wandy.waterwastereport.R;

import java.io.IOException;

public class ReportScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_screen);
        ImageView imageView = findViewById(R.id.take_report);
        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("img"));
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), uri);
            imageView.setImageBitmap(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
