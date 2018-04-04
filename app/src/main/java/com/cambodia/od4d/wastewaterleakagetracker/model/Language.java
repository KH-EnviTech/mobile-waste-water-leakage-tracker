package com.cambodia.od4d.wastewaterleakagetracker.model;

import com.cambodia.od4d.wastewaterleakagetracker.R;
/**
 * Created by wandy on 3/17/18.
 */

public class Language {

    public static String KHMER = "khmer";
    public static String ENGLISH = "english";

    public String camera;
    public String gallery;
    public String history;
    public String save;
    public String post;
    public String reset;
    public String newPost;
    public String currentLanguage;
    public String chooseAction;
    public int drawableLanguage;

    public Language(String language){
        if (language.equalsIgnoreCase(KHMER)){
            camera = "kh";
            gallery = "kh";
            history = "kh";
            save = "kh";
            post = "kh";
            reset = "kh";
            newPost = "kh";
            chooseAction = "kh";
            drawableLanguage = R.drawable.kh;
        } else {
            camera = "camera";
            gallery = "galley";
            history = "history";
            save = "save";
            post = "post";
            reset = "reset";
            newPost = "New Post";
            reset = "reset";
            chooseAction = "Please Choose An Action";
            drawableLanguage = R.drawable.us;
        }

        currentLanguage = language;

    }



}
