package com.cambodia.od4d.wastewaterleakagetracker.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cambodia.od4d.wastewaterleakagetracker.model.Language;

/**
 * Created by wandy on 3/17/18.
 */

public class LanguageSql extends SQLiteOpenHelper {

    private static String db = "water_waste_language";
    private static int version = 12;
    private static String table = "language";
    private static String c_language = "language";

    public LanguageSql(Context context) {
        super(context, db, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + table + " (" +
                        c_language + " TEXT)";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void add() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(c_language, Language.ENGLISH);
        sqLiteDatabase.insert(table, null, values);
        sqLiteDatabase.close();
    }

    public String getLanguage() {

        Cursor cursor = this.getWritableDatabase().rawQuery("select * from " + table, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;

        return cursor.getString(cursor.getColumnIndex(c_language));
    }

    public void update(String language) {
        ContentValues cv = new ContentValues();
        cv.put(c_language, language);
        this.getWritableDatabase().update(table, cv, "1", null);
    }

    private final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + table;

    private boolean isEmptyTable() {

        String count = "SELECT count(*) FROM " + table;
        Cursor mcursor = this.getWritableDatabase().rawQuery(count, null);
        mcursor.moveToFirst();
//        mcursor.close();
        int getCount = mcursor.getInt(0);
        return !(mcursor.getInt(0) > 0);

    }

    public void check(){
        if(isEmptyTable())
            add();
    }
}
