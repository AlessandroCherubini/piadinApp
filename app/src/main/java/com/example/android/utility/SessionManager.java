package com.example.android.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.android.activity.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    //Private variables
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static final String PREFERENCES_NAME = "piadinApp"; //Shared pref filename

    //Shared preferences keys
    private static final String IS_LOGIN  = "loggedInmode";
    public static final String KEY_NAME   = "name"; //User name
    public static final String KEY_EMAIL  = "email"; //Email address
    public static final String KEY_PHONE  = "phone";
    public static final String KEY_TIMBRI = "timbri";
    public static final String KEY_OMAGGI = "omaggi";

    //Private constructor (static class)
    private SessionManager() {}

    /**
     * Create a login session on user login
     *
     * @param context Application context
     * @param name Username
     * @param email User email
     * @param phone User phone number
     * @param timbri Number of fidelity stamps
     * @param omaggi Number of free piadine
     * @return  Commit in SharedPref result
     */
    public static boolean createLoginSession(Context context, String name, String email, String phone,int timbri,int omaggi) {
        if(!retrieveSharedPrefs(context,true))
            return false;

        //editor = preferences.edit();

        //Store login value as true
        editor.putBoolean(IS_LOGIN, true);
        //Store name
        editor.putString(KEY_NAME, name);
        //Store email
        editor.putString(KEY_EMAIL, email);
        //Store user phone
        editor.putString(KEY_PHONE, phone);
        //Store fidelity card infos
        editor.putInt(KEY_TIMBRI, timbri);
        editor.putInt(KEY_OMAGGI, omaggi);

        //Commit changes
        return editor.commit();
    }

    /**
     * Get stored used session data
     *
     * @param context Application context
     * @return HashMap with user data. In Shared pref is null, return null.
     */
    public static HashMap<String,String> getUserDetails(Context context) {
        if(!retrieveSharedPrefs(context,false))
            return null;

        HashMap<String,String> userData = new HashMap<>();
        //Username
        userData.put(KEY_NAME, preferences.getString(KEY_NAME,""));
        //Email
        userData.put(KEY_EMAIL, preferences.getString(KEY_EMAIL,""));
        //Phone number
        userData.put(KEY_PHONE, preferences.getString(KEY_PHONE,""));

        return userData;
    }

    /**
     * Get stored fidelity badge data
     *
     * @param context Application context
     * @return HashMap with badge data. If get shared pref fails, return null
     */
    public static HashMap<String,Integer> getBadgeDetails(Context context)
    {
        if(!retrieveSharedPrefs(context,false))
            return null;

        HashMap<String,Integer> badgeData = new HashMap<>();
        //Timbri
        badgeData.put(KEY_TIMBRI, preferences.getInt(KEY_TIMBRI,-1));
        //Omaggi
        badgeData.put(KEY_OMAGGI, preferences.getInt(KEY_OMAGGI,-1));

        return badgeData;
    }

    /**
     * Clear session details after logout of user
     *
     * @param context Application context
     * @return Clear data result
     */
    public static boolean logoutUser(Context context)
    {
        if(!retrieveSharedPrefs(context,true))
            return false;

        //Clear all data from shared prefs
        editor.clear();
        if(!editor.commit())
            return false;

        //After logout return to login activity
        Intent i = new Intent(context, MainActivity.class);
        //Close all other activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Add new flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Start main activity
        context.startActivity(i);

        return true;
    }

    /**
     * Sets if user is logged in info in shared pref
     * @param context Application context
     * @param logged Logged value
     * @return Edit pref result
     */
    public static boolean setLoggedIn(Context context,boolean logged)
    {
        if(!retrieveSharedPrefs(context,true))
            return false;

        editor.putBoolean(IS_LOGIN,logged);
        return editor.commit();
    }

    /**
     * Return the login value from prefs
     * @param context Application context
     * @return Login value
     */
    public static boolean loggedIn(Context context)
    {
        if(!retrieveSharedPrefs(context,false))
            return false;

        return preferences.getBoolean(IS_LOGIN,false);
    }

    /**
     * Updates timbri values in shared pref
     * @param context Application context
     * @param timbriValue New value of timbri
     * @return Update data result
     */
    public static boolean updateTimbriValue(Context context,int timbriValue)
    {
        if(!retrieveSharedPrefs(context,true))
            return false;

        editor.putInt(KEY_TIMBRI,timbriValue);
        return editor.commit();
    }

    /**
     * Updates omaggi values in shared pref
     * @param context Application context
     * @param omaggiValue New value of omaggi
     * @return Update data result
     */
    public static boolean updateOmaggiValue(Context context,int omaggiValue)
    {
        if(!retrieveSharedPrefs(context,true))
            return false;

        editor.putInt(KEY_OMAGGI,omaggiValue);
        return editor.commit();
    }

    /**
     * Updates omaggi values in shared pref
     * @param context Application context
     * @param timbri New value of timbri
     * @param omaggi New value of omaggi
     * @return Update data result
     */
    public static boolean updateTimbriAndOmaggiValues(Context context,int timbri,int omaggi)
    {
        if(!retrieveSharedPrefs(context,true))
            return false;

        editor.putInt(KEY_TIMBRI,timbri);
        editor.putInt(KEY_OMAGGI,omaggi);
        return editor.commit();
    }

    /**
     * Update user editable values (only name and phone)
     * @param context Application context
     * @param username New username, could be empty
     * @param phone New phone, could be empty
     * @return Update data result
     */
    public static boolean updateUserData(Context context,String username,String phone)
    {
        if(!retrieveSharedPrefs(context,true))
            return false;

        if(!username.isEmpty()) {
            editor.putString(KEY_NAME,username);
        }
        if(!phone.isEmpty()) {
            editor.putString(KEY_PHONE,phone);
        }

        return editor.commit();
    }

    //PRIVATE FUNCTIONS------------------------------------------------------------------

    /**
     * Gets shared prefs from context. If writeMode is true, opens also the editor
     * @param context Application context
     * @param writeMode Write in shared prefs mode
     * @return Open shared pref result
     */
    private static boolean retrieveSharedPrefs(Context context, boolean writeMode) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        if(preferences == null) {
            Log.d("Shared Pref Error","Failed to load Shared Preferences!");
            return false;
        }

        if(writeMode) {
            editor = preferences.edit();
        }

        return true;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void printSharedPreferences(){
        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SHARED_PREFERENCES", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}