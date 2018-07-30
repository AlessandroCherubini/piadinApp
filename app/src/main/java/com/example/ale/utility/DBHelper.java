package com.example.ale.utility;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.piadinapp.classi.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper{


    private static int    DB_VERSION = 1;

    // tabella: logins
    public static final String TABLE_LOGINS_NAME = "logins";
    public static final String COLUMN_LOGINS_ID = "userId";
    public static final String COLUMN_LOGINS_NAME = "username";
    public static final String COLUMN_LOGINS_PASSWORD = "password";
    public static final String COLUMN_LOGINS_EMAIL = "email";
    //public static final String COLUMN_LOGINS_PHONE = "phone";

    // tabella: piadine
    public static final String TABLE_PIADINE_NAME = "piadine";
    public static final String COLUMN_PIADINE_ID = "id_piadine";
    public static final String COLUMN_PIADINE_NAME = "nome";
    public static final String COLUMN_PIADINE_INGREDIENTI = "ingredienti";
    public static final String COLUMN_PIADINE_PREZZO = "prezzo";
    public static final String COLUMN_PIADINE_TIMESTAMP = "updated_at";

    // tabella: ingredienti
    public static final String TABLE_INGREDIENTI_NAME = "ingredienti";
    public static final String COLUMN_INGREDIENTI_ID = "id_ingrediente";
    public static final String COLUMN_INGREDIENTI_NAME = "nome";
    public static final String COLUMN_INGREDIENTI_PREZZO = "prezzo";
    public static final String COLUMN_INGREDIENTI_ALLERGENI = "allergeni";
    public static final String COLUMN_INGREDIENTI_TIMESTAMP = "updated_at";

    // costruttore.
    public DBHelper(Context context) {

        super(context, "piadinApp.db", null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.w("DB/onCreate", "Application::onCreate");

        String query_logins = "CREATE TABLE " + TABLE_LOGINS_NAME
                + "(" + COLUMN_LOGINS_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_LOGINS_NAME +
                " TEXT, " + COLUMN_LOGINS_PASSWORD +
                " TEXT, " + COLUMN_LOGINS_EMAIL + " TEXT);";

        String query_piadine = "CREATE TABLE " + TABLE_PIADINE_NAME
                + "(" + COLUMN_PIADINE_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PIADINE_NAME +
                " VARCHAR, " +COLUMN_PIADINE_INGREDIENTI+
                " TEXT, " + COLUMN_PIADINE_PREZZO +
                " DOUBLE, " + COLUMN_PIADINE_TIMESTAMP + " LONG);";

        String query_ingredienti = "CREATE TABLE " + TABLE_INGREDIENTI_NAME
                + "(" + COLUMN_INGREDIENTI_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_INGREDIENTI_NAME +
                " VARCHAR, " + COLUMN_INGREDIENTI_PREZZO +
                " DOUBLE, " + COLUMN_INGREDIENTI_ALLERGENI +
                " VARCHAR, " + COLUMN_INGREDIENTI_TIMESTAMP + " LONG);";


        // creazione tabella: users
        sqLiteDatabase.execSQL(query_logins);
        // creazione tabella: piadine
        sqLiteDatabase.execSQL(query_piadine);
        // creazione tabella: ingredienti
        sqLiteDatabase.execSQL(query_ingredienti);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try{
            System.out.println("UPGRADE DB oldVersion="+oldVersion+" - newVersion="+newVersion);
            //String sql_users = "DROP TABLE IF EXISTS User";
            String sql_piadine = "DROP TABLE IF EXISTS Piadine";

            //sqLiteDatabase.execSQL(sql_users);
            sqLiteDatabase.execSQL(sql_piadine);

            onCreate(sqLiteDatabase);

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
        }finally{
            database.close();
        }

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

    public int updateUserEMail (User queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGINS_NAME, queryValues.nickname);
        values.put(COLUMN_LOGINS_EMAIL, queryValues.email);
        queryValues.userId = database.insert("logins", null, values);
        database.close();
        return database.update("logins", values, "userId = ?", new String[] {String.valueOf(queryValues.userId)});
    }

/*    public int updateUserPhone (User queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGINS_NAME, queryValues.nickname);
        values.put(COLUMN_LOGINS_PHONE, queryValues.phone);
        queryValues.userId = database.insert("logins", null, values);
        database.close();
        return database.update("logins", values, "userId = ?", new String[] {String.valueOf(queryValues.userId)});
    }*/

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


    // Metodo per le ottenere le Piadine dal database Interno.
    public ArrayList<Piadina> getPiadine() {
        ArrayList<Piadina> piadine = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PIADINE_NAME + " ORDER BY " + COLUMN_PIADINE_ID + " ASC;";
        Cursor cursorPiadine = db.rawQuery(sql, null);
        if (cursorPiadine.moveToFirst()){
            do {
                long idPiadina = cursorPiadine.getLong(0);
                String nomePiadina = cursorPiadine.getString(1);
                String ingredientiPiadina = cursorPiadine.getString(2);
                double prezzoPiadina = cursorPiadine.getDouble(3);
                long lastUpdatePiadina = cursorPiadine.getLong(4);

                ArrayList<Ingrediente> ingredienti = getIngredientiFromString(ingredientiPiadina);
                Piadina piadina = new Piadina(idPiadina, nomePiadina, ingredienti, prezzoPiadina, lastUpdatePiadina);

                piadine.add(piadina);
            } while (cursorPiadine.moveToNext());
        }
        return piadine;
    }

    public Piadina getPiadinaByPosition (long position){
        ArrayList<Ingrediente> ingredienti = new ArrayList<>();
        String query = "Select nome, ingredienti, prezzo, updated_at from piadine where id_piadine='"+position+"'";
        Piadina myPiadina= new Piadina(position, "",null,0,0);
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                myPiadina.setNome(cursor.getString(0));
                String descrizione = cursor.getString(1);
                ingredienti = getIngredientiFromString(descrizione);
                myPiadina.setIngredienti(ingredienti);
                myPiadina.setPrice(cursor.getDouble(2));
                myPiadina.setLastUpdated(cursor.getLong(3));
            } while (cursor.moveToNext());
        }
        return myPiadina;
    }


    public void insertPiadina (Piadina piadina){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String piadinaIngredienti = piadina.printIngredienti();

        values.put(COLUMN_PIADINE_NAME, piadina.getNome());
        values.put(COLUMN_PIADINE_INGREDIENTI, piadinaIngredienti);
        values.put(COLUMN_PIADINE_PREZZO, piadina.getPrice());
        values.put(COLUMN_PIADINE_TIMESTAMP, piadina.getLastUpdated());

        try {
            long id = database.insert(TABLE_PIADINE_NAME, null, values);
            piadina.setId(id);
            Log.d("DB/INSERT", "Piadina aggiunta al db interno!");
        }catch(Exception e){
            Log.d("DB/INSERT", e.toString());
        }
        database.close();

    }

    public void insertIngrediente (Ingrediente ingrediente){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_INGREDIENTI_NAME, ingrediente.getName());
        values.put(COLUMN_INGREDIENTI_PREZZO, ingrediente.getPrice());
        values.put(COLUMN_INGREDIENTI_ALLERGENI, ingrediente.getListaAllergeni());
        values.put(COLUMN_INGREDIENTI_TIMESTAMP, ingrediente.getLastUpdated());

        try {
            long id = database.insert(TABLE_INGREDIENTI_NAME, null, values);
            ingrediente.setIdIngrediente(id);
            Log.d("DB/INSERT", "Ingrediente aggiunto al db interno!");
        }catch(Exception e){
            Log.d("DB/INSERT", e.toString());
        }
        database.close();

    }

    public long getInternalTimeStampPiadine(){
        long timeStamp = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PIADINE_NAME + ";";
        Cursor cursorPiadine = db.rawQuery(sql, null);

        if(cursorPiadine.moveToFirst()){
            timeStamp = cursorPiadine.getLong(4);
        }

        return timeStamp;
    }

    public long getInternalTimeStampIngredienti(){
        long timeStamp = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_INGREDIENTI_NAME + ";";
        Cursor cursorPiadine = db.rawQuery(sql, null);

        if(cursorPiadine.moveToFirst()){
            timeStamp = cursorPiadine.getLong(4);
        }

        return timeStamp;
    }

    public Ingrediente getIngredienteByName (String nomeIngrediente) {
        String query = "Select id_ingrediente, prezzo, allergeni, updated_at from ingredienti where nome ='"+nomeIngrediente+"'";
        Ingrediente ingrediente = new Ingrediente(0, nomeIngrediente, 0.0, "", 0);
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ingrediente.setIdIngrediente(cursor.getLong(0));
                ingrediente.setPrice(cursor.getDouble(1));
                ingrediente.setListaAllergeni(cursor.getString(2));
                ingrediente.setLastUpdated(cursor.getLong(3));
            } while (cursor.moveToNext());
        }
        return ingrediente;
    }

    public ArrayList<Ingrediente> getIngredientiFromString (String stringaIngredienti){
        ArrayList<Ingrediente> listaIngredienti = new ArrayList<>();

        String[] result = stringaIngredienti.split(",\\s");

        for (int i=0; i < result.length; i++) {
            Log.d("STRINGA", result[i]);
            Ingrediente ingrediente = getIngredienteByName(result[i]);
            listaIngredienti.add(ingrediente);
        }

        return listaIngredienti;
    }

    public void printPiadineTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PIADINE_NAME + ";";
        Log.d("DB/PRINT", "Stampa tabella piadine!");

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do {
                Log.d("DB/PRINT", "ID Piadina: " + cursor.getLong(0));
                Log.d("DB/PRINT", "Nome Piadina: " + cursor.getString(1));
                Log.d("DB/PRINT", "Descrizione Piaidna: " + cursor.getString(2));
                Log.d("DB/PRINT", "Prezzo Piadina: " + cursor.getDouble(3));
                Log.d("DB/PRINT", "Timestamp Piadina: " + cursor.getLong(4));
            } while (cursor.moveToNext());
        }
        db.close();
    }

    public void printIngredientiTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_INGREDIENTI_NAME + ";";
        Log.d("DB/PRINT", "Stampa tabella ingredienti!");

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do {
                Log.d("DB/PRINT", "ID Ingrediente: " + cursor.getLong(0));
                Log.d("DB/PRINT", "Nome Ingrediente: " + cursor.getString(1));
                Log.d("DB/PRINT", "Prezzo Ingrediente: " + cursor.getDouble(2));
                Log.d("DB/PRINT", "Allergeni Ingrediente: " + cursor.getString(3));
                Log.d("DB/PRINT", "Timestamp Ingrediente: " + cursor.getLong(4));
            } while (cursor.moveToNext());
        }
        db.close();
    }

    public void printLoginsTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_LOGINS_NAME + ";";
        Log.d("DB/PRINT", "Stampa tabella utenti!");

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do {
                Log.d("DB/PRINT", "ID Utente: " + cursor.getLong(0));
                Log.d("DB/PRINT", "Nome Utente: " + cursor.getString(1));
                Log.d("DB/PRINT", "Password Utente: " + cursor.getString(2));
                Log.d("DB/PRINT", "Email Utente: " + cursor.getString(3));
            } while (cursor.moveToNext());
        }
        db.close();
    }
}
