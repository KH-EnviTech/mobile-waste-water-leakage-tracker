package com.example.wandy.waterwastereport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wandy.waterwastereport.activities.HomeScreen;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
       Thread r = new Thread(){
           @Override
           public void run() {
               try {
                   sleep(2000);
                   Intent intent = new Intent(getApplication(),HomeScreen.class);
                   getApplication().startActivity(intent);
                   finish();
               } catch (InterruptedException e) {

                   e.printStackTrace();
               }
           }
       };

       r.start();
    }
}
