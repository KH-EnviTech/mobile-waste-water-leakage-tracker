package com.example.wandy.waterwastereport.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wandy.waterwastereport.R;

public class SlashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen);

        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                startActivity(new Intent(SlashScreen.this, HomeScreen.class));
                finish();
            }
        };
        handler.postDelayed(r, 3000);

    }

}
