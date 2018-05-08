package com.cambodia.od4d.wastewaterleakagetracker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.cambodia.od4d.wastewaterleakagetracker.R;

public class SlashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen);
        ImageView img = findViewById(R.id.img);
//        img.setImageDrawable(resizeImage(getResources().getDrawable(R.drawable.app_icon_dark)));

        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                startActivity(new Intent(SlashScreen.this, HomeScreen.class));
                finish();
            }
        };
        handler.postDelayed(r, 3000);

    }

    private Drawable resizeImage(Drawable img){
        Bitmap b = ((BitmapDrawable)img).getBitmap();
        Bitmap bitmapResize = Bitmap.createScaledBitmap(b, 350,350,false);
        return new BitmapDrawable(getResources(), bitmapResize);
    }

}
