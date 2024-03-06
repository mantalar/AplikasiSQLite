package id.go.kominfo.mantala.aplikasisqlite.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import id.go.kominfo.mantala.aplikasisqlite.dao.FriendDao;
import id.go.kominfo.mantala.aplikasisqlite.model.Friend;

public class FriendDB extends SQLiteOpenHelper implements FriendDao {
    public static final String DBNAME = "dtsvsga.db";
    public static final int DBVERSION = 2;

    public FriendDB(@Nullable Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists friend(" +
                "id integer primary key autoincrement," +
                "name text," +
                "address text," +
                "phone text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("drop table if exists friend");
            onCreate(db);
        }
    }

    @Override
    public void insert(Friend friend) {
        ContentValues values = new ContentValues();
        values.putNull("id");
        values.put("name", friend.getName());
        values.put("address", friend.getAddress());
        values.put("phone", friend.getPhone());
        SQLiteDatabase db = getWritableDatabase();
        db.insert("friend", null, values);
    }

    @Override
    public void update(Friend friend) {
        ContentValues values = new ContentValues();
        values.put("id", friend.getId());
        values.put("name", friend.getName());
        values.put("address", friend.getAddress());
        values.put("phone", friend.getPhone());
        SQLiteDatabase db = getWritableDatabase();
        db.update("friend", values, "id=?", new String[]{String.valueOf(friend.getId())});
    }

    @Override
    public void delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("friend", "id=?", new String[]{String.valueOf(id)});
    }

    @Override
    public Friend getAFriendById(int id) {
        Friend result = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("friend", null, "id=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst())
            result = new Friend(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));

        cursor.close();
        return result;
    }

    @Override
    public List<Friend> getAllFriends() {
        List<Friend> result = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("friend", null, null, null,
                null, null, null);

        while(cursor.moveToNext())
            result.add(new Friend(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3))
            );

        cursor.close();
        return result;
    }
}
