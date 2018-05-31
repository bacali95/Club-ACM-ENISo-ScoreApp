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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationDAO extends SQLiteOpenHelper implements DAOInterface<Notification> {

    private static final String DATABASE_NAME = "Notifications.db";
    private static final String NOTIF_TABLE_NAME = "notification_table";
    private static final String NOTIF_COL0 = "ID";
    private static final String NOTIF_COL1 = "body";
    private static final String NOTIF_COL2 = "sentTime";
    private static final String NOTIF_INSERT_Query = "CREATE TABLE " + NOTIF_TABLE_NAME +
            "(" + NOTIF_COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            NOTIF_COL1 + " TEXT," +
            NOTIF_COL2 + " TEXT)";
    private static long NOTIF_ID = 0;

    public NotificationDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NOTIF_INSERT_Query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTIF_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void add(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIF_COL0, NOTIF_ID++);
        contentValues.put(NOTIF_COL1, notification.getBody());
        contentValues.put(NOTIF_COL2, notification.getSentTime());
        db.insert(NOTIF_TABLE_NAME, null, contentValues);
    }

    @Override
    public void edit(Notification notification) {
        // Nothing
    }

    @Override
    public void remove(Notification notification) {
        // Nothing
    }

    @Override
    public List<Notification> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Notification> result = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + NOTIF_TABLE_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new Notification(cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        return result;
    }

    @Override
    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        NOTIF_ID = 0;
        db.delete(NOTIF_TABLE_NAME, null, null);
    }

    @Override
    public long countItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, NOTIF_TABLE_NAME);
    }

}
