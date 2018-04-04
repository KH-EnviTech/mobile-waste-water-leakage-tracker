package com.cambodia.od4d.wastewaterleakagetracker.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cambodia.od4d.wastewaterleakagetracker.model.PostModel;

import java.util.ArrayList;

public class SaveSql extends SQLiteOpenHelper {

    private static String db = "water_waste";
    private static int version = 12;
    private static String table = "water";
    private static String c_id = "id";
    private static String c_description = "description";
    private static String c_lat = "lat";
    private static String c_lng = "lng";
    private static String c_image_url = "image_url";
    private static String c_date = "date";

    public SaveSql(Context context) {
        super(context, db, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + table + " (" +
                        c_id + " INTEGER PRIMARY KEY, " +
                        c_description + " TEXT, " +
                        c_image_url + " TEXT, " +
                        c_lat + " TEXT, " +
                        c_date + " TEXT, " +
                        c_lng + " TEXT)";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void add(PostModel postModel) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(c_description, postModel.getDescription());
        values.put(c_image_url, postModel.getImage_url());
        values.put(c_lat, postModel.getLat());
        values.put(c_lng, postModel.getLng());
        values.put(c_date, postModel.getDate());
        long id = sqLiteDatabase.insert(table, null, values);
        sqLiteDatabase.close();
    }

    public ArrayList<PostModel> getDB() {
        ArrayList<PostModel> models = new ArrayList<>();
        Cursor cursor = this.getWritableDatabase().rawQuery("select * from " + table + " order by id desc ", null);
        if (cursor.moveToFirst()) {

            do {
                models.add(new PostModel(
                        cursor.getInt(cursor.getColumnIndex(c_id)),
                        cursor.getString(cursor.getColumnIndex(c_image_url)),
                        cursor.getString(cursor.getColumnIndex(c_description)),
                        cursor.getString(cursor.getColumnIndex(c_lat)),
                        cursor.getString(cursor.getColumnIndex(c_lng)),
                        cursor.getString(cursor.getColumnIndex(c_date))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return models;
    }

    public PostModel getPost(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(table, new String[]{c_id, c_image_url, c_description, c_lat, c_lng, c_date}, c_id + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        // return contact
        return new PostModel(
                cursor.getInt(cursor.getColumnIndex(c_id)),
                cursor.getString(cursor.getColumnIndex(c_image_url)),
                cursor.getString(cursor.getColumnIndex(c_description)),
                cursor.getString(cursor.getColumnIndex(c_lat)),
                cursor.getString(cursor.getColumnIndex(c_lng)),
                cursor.getString(cursor.getColumnIndex(c_date))
        );
    }


    private final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + table;
}
