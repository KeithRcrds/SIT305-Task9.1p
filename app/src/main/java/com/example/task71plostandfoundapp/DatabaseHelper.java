package com.example.task71plostandfoundapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lostfound.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "items";

    public static final String COL_ID = "id";
    public static final String COL_TYPE = "postType";
    public static final String COL_CATEGORY = "category";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone";
    public static final String COL_DESC = "description";
    public static final String COL_DATE = "date";
    public static final String COL_LOCATION = "location";
    public static final String COL_IMAGE = "imageUri";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TYPE + " TEXT, "
                + COL_CATEGORY + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_PHONE + " TEXT, "
                + COL_DESC + " TEXT, "
                + COL_DATE + " TEXT, "
                + COL_LOCATION + " TEXT, "
                + COL_IMAGE + " TEXT, "
                + COL_TIMESTAMP + " TEXT, "
                + COL_LATITUDE + " REAL, "
                + COL_LONGITUDE + " REAL"
                + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
    public boolean insertItem(String postType, String category, String name,
                              String phone, String description, String date,
                              String location, String imageUri, String timestamp, double latitude, double longitude) {

        SQLiteDatabase db = this.getWritableDatabase();

        android.content.ContentValues values = new android.content.ContentValues();

        values.put(COL_TYPE, postType);
        values.put(COL_CATEGORY, category);
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_DESC, description);
        values.put(COL_DATE, date);
        values.put(COL_LOCATION, location);
        values.put(COL_IMAGE, imageUri);
        values.put(COL_TIMESTAMP, timestamp);
        values.put(COL_LATITUDE, latitude);
        values.put(COL_LONGITUDE, longitude);

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }
    public java.util.List<Item> getAllItemsList() {

        java.util.List<Item> list = new java.util.ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("postType"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE));

                list.add(new Item(id, type, category, name, phone, desc, date, location, image, timestamp, latitude, longitude));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    public boolean deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}