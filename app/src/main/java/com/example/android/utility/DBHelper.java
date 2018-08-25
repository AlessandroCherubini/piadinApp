package com.example.android.utility;
import com.example.android.classi.Ingrediente;
import com.example.android.classi.Ordine;
import com.example.android.classi.Piadina;
import com.example.android.classi.Timbro;
import com.example.android.classi.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper{


    private static int    DB_VERSION = 1;

    // tabella: logins
    public static final String TABLE_LOGINS_NAME = "logins";
    public static final String COLUMN_LOGINS_ID = "userId";
    public static final String COLUMN_LOGINS_NAME = "username";
    public static final String COLUMN_LOGINS_PASSWORD = "password";
    public static final String COLUMN_LOGINS_EMAIL = "email";
    public static final String COLUMN_LOGINS_PHONE = "phone";

    // tabella: piadine
    public static final String TABLE_PIADINE_NAME = "piadine";
    public static final String COLUMN_PIADINE_ID = "id_piadine";
    public static final String COLUMN_PIADINE_NAME = "nome";
    public static final String COLUMN_PIADINE_INGREDIENTI = "ingredienti";
    public static final String COLUMN_PIADINE_PREZZO = "prezzo";
    public static final String COLUMN_PIADINE_FORMATO = "formato";
    public static final String COLUMN_PIADINE_IMPASTO = "impasto";
    public static final String COLUMN_PIADINE_QUANTITA = "quantita";
    public static final String COLUMN_PIADINE_RATING = "rating";
    public static final String COLUMN_PIADINE_TIMESTAMP = "updated_at";

    // tabella: ingredienti
    public static final String TABLE_INGREDIENTI_NAME = "ingredienti";
    public static final String COLUMN_INGREDIENTI_ID = "id_ingrediente";
    public static final String COLUMN_INGREDIENTI_NAME = "nome";
    public static final String COLUMN_INGREDIENTI_PREZZO = "prezzo";
    public static final String COLUMN_INGREDIENTI_ALLERGENI = "allergeni";
    public static final String COLUMN_INGREDIENTI_CATEGORIA = "categoria";
    public static final String COLUMN_INGREDIENTI_TIMESTAMP = "updated_at";

    //tabella: timbri
    public static final String TABLE_TIMBRI_NAME             = "timbri";
    public static final String COLUMN_TIMBRI_ID              = "id_timbro";
    public static final String COLUMN_TIMBRI_EMAIL           = "email";
    public static final String COLUMN_TIMBRI_NUMERO_TIMBRI   = "numero_timbri";
    public static final String COLUMN_TIMBRI_OMAGGI_RICEVUTI = "omaggi_ricevuti";
    public static final String COLUMN_TIMBRI_TIMESTAMP       = "updated_at";

    public static final int COLUMN_TIMBRI_ID_INDEX              = 0;
    public static final int COLUMN_TIMBRI_EMAIL_INDEX           = 1;
    public static final int COLUMN_TIMBRI_NUMERO_TIMBRI_INDEX   = 2;
    public static final int COLUMN_TIMBRI_OMAGGI_RICEVUTI_INDEX = 3;

    //tabella: ordini
    public static final String TABLE_ORDINI_NAME = "ordini";
    public static final String COLUMN_ORDINI_ID = "id_ordine";
    public static final String COLUMN_ORDINI_EMAIL = "email_utente";
    public static final String COLUMN_ORDINI_TELEFONO = "telefono_utente";
    public static final String COLUMN_ORDINI_DATA = "data_ordine";
    public static final String COLUMN_ORDINI_DESCRIZIONE = "descrizione";
    public static final String COLUMN_ORDINI_PREZZO = "prezzo";
    public static final String COLUMN_ORDINI_NOTA = "nota";
    public static final String COLUMN_ORDINI_TIMESTAMP = "updated_at";

    //tabella: piadine_votate
    public static final String TABLE_RATED_NAME = "le_mie_piadine";
    public static final String COLUMN_RATED_ID = "id_piadina";
    public static final String COLUMN_RATED_EMAIL = "email_utente";
    public static final String COLUMN_RATED_NOME = "nome_piadina";
    public static final String COLUMN_RATED_DESCRIZIONE = "descrizione";
    public static final String COLUMN_RATED_PREZZO = "prezzo";
    public static final String COLUMN_RATED_FORMATO = "formato";
    public static final String COLUMN_RATED_IMPASTO = "impasto";
    public static final String COLUMN_RATED_QUANTITA = "quantita";
    public static final String COLUMN_RATED_VOTO = "voto";
    public static final String COLUMN_RATED_ID_ESTERNO = "id_esterno";
    public static final String COLUMN_RATED_TIMESTAMP = "updated_at";

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
                " TEXT, " + COLUMN_LOGINS_EMAIL +
                " TEXT, " + COLUMN_LOGINS_PHONE + " TEXT);";

        String query_piadine = "CREATE TABLE " + TABLE_PIADINE_NAME
                + "(" + COLUMN_PIADINE_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PIADINE_NAME +
                " VARCHAR, " +COLUMN_PIADINE_INGREDIENTI+
                " TEXT, " + COLUMN_PIADINE_PREZZO +
                " DOUBLE, " + COLUMN_PIADINE_FORMATO +
                " VARCHAR, " + COLUMN_PIADINE_IMPASTO +
                " VARCHAR, " + COLUMN_PIADINE_QUANTITA +
                " INTEGER, " + COLUMN_PIADINE_RATING +
                " INTEGER, " + COLUMN_PIADINE_TIMESTAMP + " LONG);";

        String query_ingredienti = "CREATE TABLE " + TABLE_INGREDIENTI_NAME
                + "(" + COLUMN_INGREDIENTI_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_INGREDIENTI_NAME +
                " VARCHAR, " + COLUMN_INGREDIENTI_PREZZO +
                " DOUBLE, " + COLUMN_INGREDIENTI_ALLERGENI +
                " VARCHAR, " + COLUMN_INGREDIENTI_CATEGORIA +
                " VARCHAR, " + COLUMN_INGREDIENTI_TIMESTAMP + " LONG);";

        String query_timbri = "CREATE TABLE " + TABLE_TIMBRI_NAME
                + "(" + COLUMN_TIMBRI_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TIMBRI_EMAIL +
                " TEXT, " + COLUMN_TIMBRI_NUMERO_TIMBRI +
                " INTEGER, " + COLUMN_TIMBRI_OMAGGI_RICEVUTI +
                " INTEGER, " + COLUMN_TIMBRI_TIMESTAMP + " LONG);";

        String query_ordini = "CREATE TABLE " + TABLE_ORDINI_NAME
                + "(" + COLUMN_ORDINI_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ORDINI_EMAIL +
                " VARCHAR, " + COLUMN_ORDINI_TELEFONO +
                " VARCHAR, " + COLUMN_ORDINI_DATA +
                " VARCHAR, " + COLUMN_ORDINI_PREZZO +
                " DOUBLE, " + COLUMN_ORDINI_DESCRIZIONE +
                " VARCHAR, " + COLUMN_ORDINI_NOTA +
                " VARCHAR, " + COLUMN_ORDINI_TIMESTAMP + " LONG);";

        String query_rating = "CREATE TABLE " + TABLE_RATED_NAME
                + "(" + COLUMN_RATED_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ORDINI_EMAIL +
                " VARCHAR, " + COLUMN_RATED_NOME +
                " VARCHAR, " + COLUMN_RATED_DESCRIZIONE +
                " VARCHAR, " + COLUMN_RATED_PREZZO +
                " DOUBLE, " + COLUMN_RATED_FORMATO +
                " VARCHAR, " + COLUMN_RATED_IMPASTO +
                " VARCHAR, " + COLUMN_RATED_QUANTITA +
                " INTEGER, " + COLUMN_RATED_VOTO +
                " INTEGER, " + COLUMN_RATED_ID_ESTERNO +
                " INTEGER, " + COLUMN_RATED_TIMESTAMP + " LONG);";

        // creazione tabella: users
        sqLiteDatabase.execSQL(query_logins);
        // creazione tabella: piadine
        sqLiteDatabase.execSQL(query_piadine);
        // creazione tabella: ingredienti
        sqLiteDatabase.execSQL(query_ingredienti);
        //Crezione tabella: timbri
        sqLiteDatabase.execSQL(query_timbri);
        // Creazione tabella: ordini
        sqLiteDatabase.execSQL(query_ordini);
        // Creazione tabella: piadine votate
        sqLiteDatabase.execSQL(query_rating);
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

    //**** TABELLA USER ********************************************************
    public User insertUser (User queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.nickname);
        values.put("password", queryValues.password);
        values.put("email", queryValues.email);
        values.put("phone", queryValues.phone);
        try {
            queryValues.userId = database.insert("logins", null, values);
        }catch(Exception e){
            Log.d("INSERT", e.toString());
        }finally{
            database.close();
        }

        return queryValues;
    }

    public int updateUserPassword (User queryValues)
    {
        /*
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.nickname);
        values.put("password", queryValues.password);
        queryValues.userId=database.insert("logins", null, values);
        database.close();
        return database.update("logins", values, "userId = ?", new String[] {String.valueOf(queryValues.userId)});
        */

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password",queryValues.password);

        int rowsModified = database.update(TABLE_LOGINS_NAME,values,COLUMN_LOGINS_ID + " = ?",
                                           new String[] {String.valueOf(queryValues.userId)});
        database.close();

        return rowsModified;
    }

    public int updateUserName(User queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGINS_NAME,queryValues.nickname);

        int rowsModified = database.update(TABLE_LOGINS_NAME,values,COLUMN_LOGINS_ID + " = ?",
                                           new String[] {String.valueOf(queryValues.userId)});
        database.close();

        return rowsModified;
    }

    public int updateUserEmail (User queryValues)
    {
        //In teoria non si usa
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGINS_NAME, queryValues.nickname);
        values.put(COLUMN_LOGINS_EMAIL, queryValues.email);
        queryValues.userId = database.insert("logins", null, values);
        database.close();
        return database.update("logins", values, "userId = ?", new String[] {String.valueOf(queryValues.userId)});
    }

    public int updateUserPhone (User queryValues)
    {
        /*
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGINS_NAME, queryValues.nickname);
        values.put(COLUMN_LOGINS_PHONE, queryValues.phone);
        queryValues.userId = database.insert("logins", null, values);
        database.close();
        return database.update("logins", values, "userId = ?", new String[] {String.valueOf(queryValues.userId)});
        */

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGINS_PHONE,queryValues.phone);

        int rowsModified = database.update(TABLE_LOGINS_NAME,values,COLUMN_LOGINS_ID + " = ?",
                                           new String[] {String.valueOf(queryValues.userId)});
        database.close();

        return rowsModified;
    }

    public User getUserByEmail (String email)
    {
        String query = "Select userId, username, password, phone from logins where email ='"+email+"'";
        User myUser = new User(0, "","", email, "");
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                myUser.userId = cursor.getLong(0);
                myUser.nickname = cursor.getString(1);
                myUser.password = cursor.getString(2);
                myUser.phone = cursor.getString(3);
                //myUser.email = email;
            } while (cursor.moveToNext());
        }
        return myUser;
    }

    public void printLoginsTable() {
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
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //**** TABELLA PIADINE *****************************************************
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
                String formatoPiadina = cursorPiadine.getString(4);
                String impastoPiadina = cursorPiadine.getString(5);
                int quantitaPiadina = cursorPiadine.getInt(6);
                int ratingPiadina = cursorPiadine.getInt(7);
                long lastUpdatePiadina = cursorPiadine.getLong(8);

                ArrayList<Ingrediente> ingredienti = getIngredientiFromString(ingredientiPiadina);
                Piadina piadina = new Piadina(idPiadina, nomePiadina, ingredienti, prezzoPiadina,
                        formatoPiadina, impastoPiadina, quantitaPiadina, ratingPiadina, lastUpdatePiadina);

                piadine.add(piadina);
            } while (cursorPiadine.moveToNext());
        }
        return piadine;
    }

    public Piadina getPiadinaByPosition (long position) {
        ArrayList<Ingrediente> ingredienti = new ArrayList<>();

        String query = "Select nome, ingredienti, prezzo, formato, impasto, quantita, rating, updated_at from piadine where id_piadine='"+position+"'";

        Piadina myPiadina= new Piadina(position, "", null, 0, "", "", 0, 0, 0);

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                myPiadina.setNome(cursor.getString(0));
                String descrizione = cursor.getString(1);
                ingredienti = getIngredientiFromString(descrizione);
                myPiadina.setIngredienti(ingredienti);
                myPiadina.setPrice(cursor.getDouble(2));
                myPiadina.setFormato(cursor.getString(3));
                myPiadina.setImpasto(cursor.getString(4));
                myPiadina.setQuantita(cursor.getInt(5));
                myPiadina.setRating(cursor.getInt(6));
                myPiadina.setLastUpdated(cursor.getLong(7));
            } while (cursor.moveToNext());
        }
        return myPiadina;
    }

/*    public Piadina getPiadinaByName (String name)
    {
        ArrayList<Ingrediente> ingredienti = new ArrayList<>();
        String query = "Select nome, ingredienti, prezzo, formato, impasto, quantita, rating, updated_at from piadine where nome='"+name+"'";
        Piadina myPiadina= new Piadina(name, null, 0, "", "", "", 0, 0, null);
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
    }*/

    public void insertPiadina (Piadina piadina) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String piadinaIngredienti = piadina.printIngredienti();

        values.put(COLUMN_PIADINE_NAME, piadina.getNome());
        values.put(COLUMN_PIADINE_INGREDIENTI, piadinaIngredienti);
        values.put(COLUMN_PIADINE_PREZZO, piadina.getPrice());
        values.put(COLUMN_PIADINE_FORMATO, piadina.getFormato());
        values.put(COLUMN_PIADINE_IMPASTO, piadina.getImpasto());
        values.put(COLUMN_PIADINE_QUANTITA, piadina.getQuantita());
        values.put(COLUMN_PIADINE_RATING, piadina.getRating());
        values.put(COLUMN_PIADINE_TIMESTAMP, piadina.getLastUpdated());

        try {
            long id = database.insert(TABLE_PIADINE_NAME, null, values);
            piadina.setId(id);
            //Log.d("DB/INSERT", "Piadina aggiunta al db interno!");
        }catch(Exception e){
            Log.d("DB/INSERT", e.toString());
        }
        database.close();

    }

    public long getInternalTimeStampPiadine() {
        long timeStamp = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PIADINE_NAME + ";";
        Cursor cursorPiadine = db.rawQuery(sql, null);

        if(cursorPiadine.moveToFirst()){
            timeStamp = cursorPiadine.getLong(8);
        }

        return timeStamp;
    }

    public void printPiadineTable()
    {
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
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //**** TABELLA INGREDIENTI**************************************************
    public void insertIngrediente (Ingrediente ingrediente)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_INGREDIENTI_NAME, ingrediente.getName());
        values.put(COLUMN_INGREDIENTI_PREZZO, ingrediente.getPrice());
        values.put(COLUMN_INGREDIENTI_ALLERGENI, ingrediente.getListaAllergeni());
        values.put(COLUMN_INGREDIENTI_CATEGORIA, ingrediente.getCategoria());
        values.put(COLUMN_INGREDIENTI_TIMESTAMP, ingrediente.getLastUpdated());

        try {
            long id = database.insert(TABLE_INGREDIENTI_NAME, null, values);
            ingrediente.setIdIngrediente(id);
            //Log.d("DB/INSERT", "Ingrediente aggiunto al db interno!");
        }catch(Exception e){
            Log.d("DB/INSERT", e.toString());
        }
        database.close();

    }

    public long getInternalTimeStampIngredienti()
    {
        long timeStamp = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_INGREDIENTI_NAME + ";";
        Cursor cursorPiadine = db.rawQuery(sql, null);

        if(cursorPiadine.moveToFirst()){
            timeStamp = cursorPiadine.getLong(5);
        }

        return timeStamp;
    }

    public boolean existOrdersByEmail(String emailUtente){
        boolean exist;

        String query = "Select updated_at from ordini where email_utente ='"+emailUtente+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorOrdini = db.rawQuery(query, null);

        if(cursorOrdini.moveToFirst()){
            exist = true;
        }else{
            exist = false;
        }

        return exist;
    }

    public Ingrediente getIngredienteByName (String nomeIngrediente)
    {
        String query = "Select id_ingrediente, prezzo, allergeni, categoria, updated_at from ingredienti where nome ='"+nomeIngrediente+"'";
        Ingrediente ingrediente = new Ingrediente(0, nomeIngrediente, 0.0, "", "", 0);
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ingrediente.setIdIngrediente(cursor.getLong(0));
                ingrediente.setPrice(cursor.getDouble(1));
                ingrediente.setListaAllergeni(cursor.getString(2));
                ingrediente.setCategoria(cursor.getString(3));
                ingrediente.setLastUpdated(cursor.getLong(4));
            } while (cursor.moveToNext());
        }

        database.close();
        return ingrediente;
    }

    public ArrayList<Ingrediente> getIngredientiFromString (String stringaIngredienti)
    {
        ArrayList<Ingrediente> listaIngredienti = new ArrayList<>();

        String[] result = stringaIngredienti.split(",\\s");

        for (int i=0; i < result.length; i++) {
            //Log.d("STRINGA", result[i]);
            Ingrediente ingrediente = getIngredienteByName(result[i]);
            listaIngredienti.add(ingrediente);
        }

        return listaIngredienti;
    }

    // Metodo per le ottenere gli Ingredienti dal database Interno.
    public ArrayList<Ingrediente> getIngredienti()
    {
        ArrayList<Ingrediente> ingredienti = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_INGREDIENTI_NAME + " ORDER BY " + COLUMN_INGREDIENTI_ID + " ASC;";
        Cursor cursorIngredienti = db.rawQuery(sql, null);
        if (cursorIngredienti.moveToFirst()){
            do {
                long idIngrediente = cursorIngredienti.getLong(0);
                String nomeIngrediente = cursorIngredienti.getString(1);
                double prezzoIngrediente  = cursorIngredienti.getDouble(2);
                String allergeniIngrediente = cursorIngredienti.getString(3);
                String categoriaIngrediente = cursorIngredienti.getString(4);
                long lastUpdateIngrediente = cursorIngredienti.getLong(5);

                Ingrediente ingrediente = new Ingrediente(idIngrediente, nomeIngrediente, prezzoIngrediente, allergeniIngrediente,
                        categoriaIngrediente, lastUpdateIngrediente);

                ingredienti.add(ingrediente);
            } while (cursorIngredienti.moveToNext());
        }
        db.close();

        return ingredienti;
    }

    public ArrayList<String> getCategorieIngredienti()
    {
        ArrayList<String> categorie = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT categoria FROM " + TABLE_INGREDIENTI_NAME + " ORDER BY " + COLUMN_INGREDIENTI_ID + " ASC;";
        Cursor cursorIngredienti = db.rawQuery(sql, null);
        if(cursorIngredienti.moveToFirst()){
            do{
                String categoria = cursorIngredienti.getString(0);
                if(!categorie.contains(categoria)){
                    categorie.add(categoria);
                }
            }while (cursorIngredienti.moveToNext());
        }
        db.close();

        return categorie;
    }

    public ArrayList<Ingrediente> getIngredientiByCategoria(String categoriaIngrediente)
    {
        ArrayList<Ingrediente> ingredienti = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select id_ingrediente, nome, prezzo, allergeni, updated_at from ingredienti where categoria ='"+categoriaIngrediente+"'";
        Cursor cursorIngredienti = db.rawQuery(sql, null);
        if (cursorIngredienti.moveToFirst()) {
            do {
                long idIngrediente = cursorIngredienti.getLong(0);
                String nomeIngrediente = cursorIngredienti.getString(1);
                double prezzoIngrediente  = cursorIngredienti.getDouble(2);
                String allergeniIngrediente = cursorIngredienti.getString(3);
                long lastUpdateIngrediente = cursorIngredienti.getLong(4);

                Ingrediente ingrediente = new Ingrediente(idIngrediente, nomeIngrediente, prezzoIngrediente,
                        allergeniIngrediente, categoriaIngrediente, lastUpdateIngrediente);
                ingredienti.add(ingrediente);
            } while (cursorIngredienti.moveToNext());
        }
        db.close();

        return ingredienti;
    }

    public void printIngredientiTable()
    {
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
                Log.d("DB/PRINT", "Categoria Ingrediente: " + cursor.getString(4));
                Log.d("DB/PRINT", "Timestamp Ingrediente: " + cursor.getLong(5));
            } while (cursor.moveToNext());
        }
        db.close();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //**** TABELLA TIMBRI ******************************************************
    public Timbro insertTimbro(Timbro queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMBRI_EMAIL, queryValues.userEmail);
        values.put(COLUMN_TIMBRI_NUMERO_TIMBRI, queryValues.numberTimbri);
        values.put(COLUMN_TIMBRI_OMAGGI_RICEVUTI, queryValues.numberOmaggi);
        try {
            queryValues.timbroId = database.insert(TABLE_TIMBRI_NAME,null,values);
        } catch (Exception e) {
            Log.d("INSERT",e.toString());
        } finally {
            database.close();
        }

        return queryValues;
    }

    public boolean updateTimbroBadgeValues(Timbro queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(COLUMN_TIMBRI_NUMERO_TIMBRI,queryValues.numberTimbri);
        value.put(COLUMN_TIMBRI_OMAGGI_RICEVUTI,queryValues.numberOmaggi);
        int rowModifiedNumber = database.update(TABLE_TIMBRI_NAME,
                                          value,
                                          COLUMN_TIMBRI_ID + " = ?",
                                          new String[] {String.valueOf(queryValues.timbroId)});

        database.close();

        if(rowModifiedNumber == 0) {
            Log.d("Update Badge Values","No Row affected");
            return false;
        }

        Log.d("Update Badge Values","Row affected: " + Integer.toString(rowModifiedNumber));
        return true;
    }

    public Timbro getTimbroByEmail(String email)
    {
        String query = "SELECT * FROM " + TABLE_TIMBRI_NAME + " WHERE " + COLUMN_TIMBRI_EMAIL +
                       " ='" + email + "'";
        Timbro myTimbro = new Timbro(0,email,0,0);
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst()) {
            do {
                myTimbro.timbroId = cursor.getLong(COLUMN_TIMBRI_ID_INDEX);
                myTimbro.numberTimbri = cursor.getInt(COLUMN_TIMBRI_NUMERO_TIMBRI_INDEX);
                myTimbro.numberOmaggi = cursor.getInt(COLUMN_TIMBRI_OMAGGI_RICEVUTI_INDEX);
            } while (cursor.moveToNext());
        }

        return myTimbro;
    }

    public boolean updateTimbroByEmail(String email,int timbri,int omaggi)
    {
        Timbro userRow = getTimbroByEmail(email);
        userRow.numberTimbri = timbri;
        userRow.numberOmaggi = omaggi;

        return updateTimbroBadgeValues(userRow);
    }

    //**** PRINT FUNCTIONS **************************************************
    public void printTimbriTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_TIMBRI_NAME + ";";
        Log.d("DB/PRINT","Stampa tabella timbri!");

        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()) {
            do {
                Log.d("DB/PRINT", "ID Timbro: " + cursor.getLong(COLUMN_TIMBRI_ID_INDEX));
                Log.d("DB/PRINT", "Email Utente: " + cursor.getString(COLUMN_TIMBRI_EMAIL_INDEX));
                Log.d("DB/PRINT", "Numero Timbri: " + cursor.getInt(COLUMN_TIMBRI_NUMERO_TIMBRI_INDEX));
                Log.d("DB/PRINT", "Numero Omaggi: " + cursor.getInt(COLUMN_TIMBRI_OMAGGI_RICEVUTI_INDEX));
            } while (cursor.moveToNext());
        }

        db.close();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //**** TABELLA ORDINE **************************************************
    public void insertOrdine (Ordine ordine) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String piadineOrdine = ordine.printPiadine();

        values.put(COLUMN_ORDINI_EMAIL, ordine.getEmailUtente());
        values.put(COLUMN_ORDINI_TELEFONO, ordine.getTelefonoUtente());
        values.put(COLUMN_ORDINI_DATA, ordine.getTimestampOrdine());
        values.put(COLUMN_ORDINI_PREZZO, ordine.getPrezzoOrdine());
        values.put(COLUMN_ORDINI_DESCRIZIONE, piadineOrdine);
        values.put(COLUMN_ORDINI_NOTA, ordine.getNotaOrdine());
        values.put(COLUMN_ORDINI_TIMESTAMP, ordine.getLastUpdated());

        try {
            long id = database.insert(TABLE_ORDINI_NAME, null, values);
            ordine.setIdOrdine(id);
            Log.d("DB/INSERT", "Ordine aggiunto al db interno!");
        }catch(Exception e){
            Log.d("DB/INSERT", e.toString());
        }

        database.close();
    }

    public ArrayList<Ordine> getOrdiniByEmail (final String email){
        ArrayList<Ordine> ordiniUtente = new ArrayList<>();
        ArrayList<Piadina> piadineOrdine;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select id_ordine, telefono_utente, data_ordine, descrizione, prezzo, nota, updated_at from ordini where email_utente ='"+email+"'";
        Cursor cursorOrdini = db.rawQuery(sql, null);
        if (cursorOrdini.moveToFirst()) {
            do {
                long idOrdine = cursorOrdini.getLong(0);
                String telefonoOrdine = cursorOrdini.getString(1);
                String dataOrdine = cursorOrdini.getString(2);
                String descrizioneOrdine = cursorOrdini.getString(3);
                piadineOrdine = getPiadineFromDescrizioneOrdine(descrizioneOrdine);
                double prezzoOrdine = cursorOrdini.getDouble(4);
                String notaOrdine = cursorOrdini.getString(5);
                long lastUpdateOrdine = cursorOrdini.getLong(6);

                Ordine ordine = new Ordine(idOrdine, email, telefonoOrdine, dataOrdine, prezzoOrdine, piadineOrdine, notaOrdine, lastUpdateOrdine);

                ordiniUtente.add(ordine);
            } while (cursorOrdini.moveToNext());
        }
        db.close();

        return ordiniUtente;

    }

    public ArrayList<Piadina> getPiadineFromDescrizioneOrdine(String descrizione){
        ArrayList<Piadina> piadineOrdine = new ArrayList<>();
        ArrayList<Ingrediente> ingredientiDescrizione = new ArrayList<>();

        // divido la descrizione delle piadine in singole piadine
        String[] strutturaPiadine = descrizione.split(" - ");
        for(int i = 0; i < strutturaPiadine.length; i++){
            strutturaPiadine[i] = strutturaPiadine[i].replace("[", "");
            strutturaPiadine[i] = strutturaPiadine[i].replace("]", "");
            // prendo gli attributi della singola Piaidna
            String[] attributiPiadina = strutturaPiadine[i].split("; ");
            String nomePiadina = attributiPiadina[0];
            String formatoPiadina = attributiPiadina[1];
            String impastoPiadina = attributiPiadina[2];
            String printIngredientiPiadina = attributiPiadina[3];
            String printPrezzoPiadina = attributiPiadina[4];
            String printQuantitaPiadina = attributiPiadina[5];
            double prezzoPiadina = Double.valueOf(printPrezzoPiadina);
            int quantitaPiadina = Integer.valueOf(printQuantitaPiadina);
            int ratingPiadina = 0;

            // prendo gli ingredienti e riempio l'array
            String[] nomiIngredienti = printIngredientiPiadina.split(", ");
            for(String nome : nomiIngredienti){
                Ingrediente ingrediente = getIngredienteByName(nome);
                ingredientiDescrizione.add(ingrediente);
            }
            ArrayList<Ingrediente> ingredientiPiadina = new ArrayList<>();
            ingredientiPiadina.addAll(ingredientiDescrizione);
            // Compongo la piadina con tutti gli elementi presi dalla descrizione
            Piadina piadina = new Piadina(nomePiadina, formatoPiadina, impastoPiadina, ingredientiPiadina, prezzoPiadina,
                    quantitaPiadina, ratingPiadina);

            piadineOrdine.add(piadina);
            ingredientiDescrizione.clear();
        }

        return piadineOrdine;
    }

    public void printTabellaOrdine() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_ORDINI_NAME + ";";
        Log.d("DB/PRINT","Stampa tabella ordini!");

        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()) {
            do {
                Log.d("DB/PRINT", "ID Ordine: " + cursor.getLong(0));
                Log.d("DB/PRINT", "Email Utente: " + cursor.getString(1));
                Log.d("DB/PRINT", "Telefono Utente: " + cursor.getString(2));
                Log.d("DB/PRINT", "Timestamp Ordine: " + cursor.getString(3));
                Log.d("DB/PRINT", "Totale Ordine: " + cursor.getDouble(4));
                Log.d("DB/PRINT", "Piadine Ordinate: " + cursor.getString(5));
                Log.d("DB/PRINT", "Nota Ordine: " + cursor.getString(6));
                Log.d("DB/PRINT", "Last Update Ordine: " + cursor.getLong(7));
            } while (cursor.moveToNext());
        }

        db.close();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void insertPiadinaVotata(Piadina piadina, String emailUtente){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String piadinaIngredienti = piadina.printIngredienti();

        values.put(COLUMN_RATED_EMAIL, emailUtente);
        values.put(COLUMN_RATED_NOME, piadina.getNome());
        values.put(COLUMN_RATED_DESCRIZIONE, piadinaIngredienti);
        values.put(COLUMN_RATED_PREZZO, piadina.getPrice());
        values.put(COLUMN_RATED_FORMATO, piadina.getFormato());
        values.put(COLUMN_RATED_IMPASTO, piadina.getImpasto());
        values.put(COLUMN_RATED_QUANTITA, piadina.getQuantita());
        values.put(COLUMN_RATED_VOTO, piadina.getRating());
        values.put(COLUMN_RATED_ID_ESTERNO, piadina.getIdEsterno());
        values.put(COLUMN_RATED_TIMESTAMP, piadina.getLastUpdated());

        try {
            long id = database.insert(TABLE_RATED_NAME, null, values);
            piadina.setId(id);
            //Log.d("DB/INSERT", "Piadina aggiunta al db interno!");
        }catch(Exception e){
            Log.d("DB/INSERT", e.toString());
        }
        database.close();
    }

    public void deletePiadinaVotata(Piadina piadina, String emailUtente){
        SQLiteDatabase database = this.getWritableDatabase();
        int idEsterno = piadina.getIdEsterno();
        boolean result = database.delete(TABLE_RATED_NAME, "id_esterno=" + idEsterno, null) > 0;
        if(result == false){
            Log.d("DELETEVOTO", "Errore nel cancellare la piadina interna");
        }
    }

    public ArrayList<Piadina> getLeMiePiadineByEmail(String emailUtente){
        ArrayList<Piadina> leMiePiadine = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_RATED_NAME + " WHERE " + COLUMN_RATED_EMAIL +
                " ='" + emailUtente + "'";
        Cursor cursorPiadine = db.rawQuery(sql, null);

        if (cursorPiadine.moveToFirst()){
            do {
                long idPiadina = cursorPiadine.getLong(0);
                String nomePiadina = cursorPiadine.getString(2);
                String ingredientiPiadina = cursorPiadine.getString(3);
                double prezzoPiadina = cursorPiadine.getDouble(4);
                String formatoPiadina = cursorPiadine.getString(5);
                String impastoPiadina = cursorPiadine.getString(6);
                int quantitaPiadina = cursorPiadine.getInt(7);
                int ratingPiadina = cursorPiadine.getInt(8);
                int idEsternoPiadina = cursorPiadine.getInt(9);
                long lastUpdatePiadina = cursorPiadine.getLong(10);

                ArrayList<Ingrediente> ingredienti = getIngredientiFromString(ingredientiPiadina);
                Piadina piadina = new Piadina(idPiadina, nomePiadina, formatoPiadina, impastoPiadina, ingredienti,
                        prezzoPiadina, quantitaPiadina, ratingPiadina, idEsternoPiadina, lastUpdatePiadina);

                leMiePiadine.add(piadina);
            } while (cursorPiadine.moveToNext());
        }

        return leMiePiadine;
    }

    public boolean existMiePiadineByEmail(String emailUtente){
        boolean exist;

        String query = "Select updated_at from le_mie_piadine where email_utente ='"+emailUtente+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorPiadine = db.rawQuery(query, null);

        if(cursorPiadine.moveToFirst()){
            exist = true;
        }else{
            exist = false;
        }

        return exist;
    }

    public void updateMiePiadineByID(int idEsterno, int nuovoVoto){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RATED_VOTO, nuovoVoto);

        SQLiteDatabase database = this.getWritableDatabase();
        database.update(TABLE_RATED_NAME, cv, "id_esterno="+idEsterno,null);

        database.close();
    }

}
