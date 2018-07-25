package com.example.ale.utility;
import com.example.ale.piadinapp.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper{
    private final static int    DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, "piadinApp.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table logins (userId Integer primary key autoincrement, "+
                " username text, password text, email text)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try{
            System.out.println("UPGRADE DB oldVersion="+oldVersion+" - newVersion="+newVersion);
            onCreate(sqLiteDatabase);
            if (oldVersion<10){
                String query = "create table logins (userId Integer primary key autoincrement, "+
                        " username text, password text, email text)";
                sqLiteDatabase.execSQL(query);
            }
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // super.onDowngrade(db, oldVersion, newVersion);
        System.out.println("DOWNGRADE DB oldVersion="+oldVersion+" - newVersion="+newVersion);
    }

    public User insertUser (User queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.nickname);
        values.put("password", queryValues.password);
        values.put("email", queryValues.email);
        try {
            queryValues.userId = database.insert("logins", null, values);
        }catch(Exception e){
            Log.d("INSERT", e.toString());
        }
        database.close();
        return queryValues;
    }

    public int updateUserPassword (User queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.nickname);
        values.put("password", queryValues.password);
        queryValues.userId=database.insert("logins", null, values);
        database.close();
        return database.update("logins", values, "userId = ?", new String[] {String.valueOf(queryValues.userId)});
    }

    public User getUser (String username){
        String query = "Select userId, password, email from logins where username ='"+username+"'";
        User myUser = new User(0, username,"", "");
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                myUser.userId=cursor.getLong(0);
                myUser.password=cursor.getString(1);
                myUser.email=cursor.getString(2);
            } while (cursor.moveToNext());
        }
        return myUser;
    }

    public User getUserByEmail (String email){
        String query = "Select userId, username, password from logins where email ='"+email+"'";
        User myUser = new User(0, "","", email);
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                myUser.userId=cursor.getLong(0);
                myUser.nickname = cursor.getString(1);
                myUser.password=cursor.getString(2);
                //myUser.email = email;
            } while (cursor.moveToNext());
        }
        return myUser;
    }

    public boolean userExists (String email){
        String query = "Select username from logins where email='"+email+"'";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        Log.d("UTENTE/CURSOR", cursor.getString(0));
        if(cursor == null){
            return false;
        }

        return true;
    }
}
