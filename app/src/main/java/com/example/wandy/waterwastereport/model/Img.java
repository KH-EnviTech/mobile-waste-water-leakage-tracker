package com.example.wandy.waterwastereport.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Img implements Serializable {
    Bitmap bitmap;

    public Img(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
