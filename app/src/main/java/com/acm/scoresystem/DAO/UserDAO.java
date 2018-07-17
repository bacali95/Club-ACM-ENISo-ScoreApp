package com.acm.scoresystem.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.acm.scoresystem.Model.Notification;
import com.acm.scoresystem.Model.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserDAO extends SQLiteOpenHelper implements DAOInterface<User> {

    private static final String DATABASE_NAME = "Users.db";
    private static final String USER_TABLE_NAME = "user_table";
    private static final String USER_COL0 = "ID";
    private static final String USER_COL1 = "firstName";
    private static final String USER_COL2 = "lastName";
    private static final String USER_COL3 = "email";
    private static final String USER_COL4 = "phoneNumber";
    private static final String USER_COL5 = "password";
    private static final String USER_COL6 = "schoolYear";
    private static final String USER_COL7 = "major";
    private static final String USER_COL8 = "score";
    private static final String USER_COL9 = "problemSolved";
    private static final String USER_COL10 = "codeforcesHandle";
    private static final String USER_INSERT_Query = "CREATE TABLE " + USER_TABLE_NAME +
            "(" + USER_COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            USER_COL1 + " TEXT," +
            USER_COL2 + " TEXT," +
            USER_COL3 + " TEXT," +
            USER_COL4 + " INTEGER," +
            USER_COL5 + " TEXT," +
            USER_COL6 + " TEXT," +
            USER_COL7 + " TEXT," +
            USER_COL8 + " INTEGER," +
            USER_COL9 + " INTEGER," +
            USER_COL10 + " TEXT)";
    private static long USER_ID = 0;

    public UserDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(USER_INSERT_Query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void add(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL0, USER_ID++);
        contentValues.put(USER_COL1, user.getFirstName());
        contentValues.put(USER_COL2, user.getLastName());
        contentValues.put(USER_COL3, user.getEmail());
        contentValues.put(USER_COL4, user.getPhoneNumber());
        contentValues.put(USER_COL5, user.getPassword());
        contentValues.put(USER_COL6, user.getSchoolYear());
        contentValues.put(USER_COL7, user.getMajor());
        contentValues.put(USER_COL8, user.getScore());
        contentValues.put(USER_COL9, user.getProblemSolved());
        contentValues.put(USER_COL10, user.getCodeforcesHandle());
        db.insert(USER_TABLE_NAME, null, contentValues);
    }

    @Override
    public void edit(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL1, user.getFirstName());
        contentValues.put(USER_COL2, user.getLastName());
        contentValues.put(USER_COL3, user.getEmail());
        contentValues.put(USER_COL4, user.getPhoneNumber());
        contentValues.put(USER_COL5, user.getPassword());
        contentValues.put(USER_COL6, user.getSchoolYear());
        contentValues.put(USER_COL7, user.getMajor());
        contentValues.put(USER_COL8, user.getScore());
        contentValues.put(USER_COL9, user.getProblemSolved());
        contentValues.put(USER_COL10, user.getCodeforcesHandle());
        db.update(USER_TABLE_NAME, contentValues, "phoneNumber = " + user.getPhoneNumber(), null);
    }

    @Override
    public void remove(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_TABLE_NAME, "phoneNumber = " + user.getPhoneNumber(), null);
    }

    public User getFirst() {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " LIMIT 1", null);
        cursor.moveToFirst();
        return new User(cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                Long.parseLong(cursor.getString(4)),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                Integer.parseInt(cursor.getString(8)),
                Integer.parseInt(cursor.getString(9)),
                cursor.getString(10));
    }

    @Override
    public List<User> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<User> users = new LinkedList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            users.add(new User(cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    Long.parseLong(cursor.getString(4)),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    Integer.parseInt(cursor.getString(8)),
                    Integer.parseInt(cursor.getString(9)),
                    cursor.getString(10)));
            cursor.moveToNext();
        }
        return users;
    }

    @Override
    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        USER_ID = 0;
        db.delete(USER_TABLE_NAME,null,null);

    }

    @Override
    public long countItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
    }


}
