package com.example.ale.utility;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.ale.piadinapp.MainActivity;
import com.example.ale.piadinapp.classi.PiadinApp;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Sharedpref file name
    private static final String PREF_NAME = "piadinApp";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_PHONE = "phone";

    public static final String KEY_TIMBRI = "timbri";
    public static final String KEY_OMAGGI = "omaggi";

    //public static final String KEY_DB = "db_version";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email, String phone, int timbri, int omaggi){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_PHONE, phone);

        //Store fidelity card infos
        editor.putInt(KEY_TIMBRI,timbri);
        editor.putInt(KEY_OMAGGI,omaggi);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
/*    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Main Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Main Activity
            _context.startActivity(i);
        }

    }*/
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        // user phone number
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

        // return user
        return user;
    }

    public HashMap<String,Integer> getBadgeDetails()
    {
        HashMap<String,Integer> badge = new HashMap<>();
        //Timbri
        badge.put(KEY_TIMBRI,pref.getInt(KEY_TIMBRI,-1));
        //Omaggi
        badge.put(KEY_OMAGGI,pref.getInt(KEY_OMAGGI,-1));

        return badge;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Main Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
/*    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }*/


    public void setLoggedIn(boolean logged){
        editor.putBoolean("loggedInmode", logged);
        editor.commit();
    }

    public boolean loggedIn(){
        return pref.getBoolean("loggedInmode", false);
    }

    /**
     * Update valore dei timbri.
     * @param timbriValue int Nuovo valore dei timbri
     */
    public void updateTimbriValue(int timbriValue)
    {
        editor.putInt(KEY_TIMBRI,timbriValue);
        editor.commit();
    }

    /**
     * Update valore degli omaggi
     * @param omaggiValue int Nuovo valore degli omaggi
     */
    public void updateOmaggiValue(int omaggiValue)
    {
        editor.putInt(KEY_OMAGGI,omaggiValue);
        editor.commit();
    }

    public void updateTimbriAndOmaggiValue(int timbri,int omaggi)
    {
        editor.putInt(KEY_TIMBRI,timbri);
        editor.putInt(KEY_TIMBRI,omaggi);

        editor.commit();
    }
}
