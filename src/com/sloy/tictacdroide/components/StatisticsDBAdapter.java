package com.sloy.tictacdroide.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatisticsDBAdapter {
	
	public static final String KEY_ID = "_id";
	public static final String KEY_GANA = "gana";
	public static final String KEY_DURACION = "duracion";
	public static final String KEY_FECHA = "fecha";
	public static final String KEY_P1NOMBRE = "p1nombre";
	public static final String KEY_P1HUMANO = "p1humano";
	public static final String KEY_P1PUNTOS = "p1puntos";
	public static final String KEY_P1DIFICULTAD = "p1dificultad";
	public static final String KEY_P2NOMBRE = "p2nombre";
	public static final String KEY_P2HUMANO = "p2humano";
	public static final String KEY_P2PUNTOS = "p2puntos";
	public static final String KEY_P2DIFICULTAD = "p2dificultad";
	
	public static final String TAG = "tictacdroideDB";
	
	public static final String DATABASE_NAME = "data";
	public static final String DATABASE_TABLE = "estadisticas";
	public static final int DATABASE_VERSION = 1;
	
	
	/* Sentencias SQL*/
	public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "+DATABASE_TABLE
		+ " ("+KEY_ID+" integer primary key autoincrement, " +KEY_GANA+" integer not null, "
		+KEY_DURACION+" integer not null, "+KEY_FECHA+" integer not null, "
		+KEY_P1NOMBRE+" text not null, "+KEY_P1HUMANO+" integer not null, "
		+KEY_P1PUNTOS+" integer not null, "+KEY_P1DIFICULTAD+" integer not null, "
		+KEY_P2NOMBRE+" text not null, "+KEY_P2HUMANO+" integer not null, "
		+KEY_P2PUNTOS+" integer not null, "+KEY_P2DIFICULTAD+" integer not null" +
		");";
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
		
		@Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
        
        @Override
        public void onOpen(SQLiteDatabase db){
        	db.execSQL(DATABASE_CREATE);
        }
	}//class
	
	/**
     * Constructor - toma el contexto para abrir y cerrar la BD
     * 
     * @param ctx el Context con el que trabajar
     */
    public StatisticsDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Abre la base de datos. Si no puede ser abierta, intenta crear una nueva
     * instancia de la base de datos. Si no puede ser creada, lanza una excepción 
     * para señalar el fallo.
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public StatisticsDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createEntry(int gana, int tiempo, int fecha, String p1Nombre, boolean p1Humano, int p1Puntos,
    	int p1Dificultad, String p2Nombre, boolean p2Humano, int p2Puntos, int p2Dificultad) {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_GANA, gana);
	        initialValues.put(KEY_DURACION, tiempo);
	        initialValues.put(KEY_FECHA, fecha);
	        initialValues.put(KEY_P1NOMBRE, p1Nombre);
	        initialValues.put(KEY_P1HUMANO, p1Humano);
	        initialValues.put(KEY_P1PUNTOS, p1Puntos);
	        initialValues.put(KEY_P1DIFICULTAD, p1Dificultad);
	        initialValues.put(KEY_P2NOMBRE, p2Nombre);
	        initialValues.put(KEY_P2HUMANO, p2Humano);
	        initialValues.put(KEY_P2PUNTOS, p2Puntos);
	        initialValues.put(KEY_P2DIFICULTAD, p2Dificultad);
	       // Log.i(TAG, "creada entrada");
	        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public Integer cuentaPartidasJugadas(){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{}, KEY_P2HUMANO+"=0", null, null, null, null);
    	
    	if(!c.moveToFirst()) return 0;
    	int i;
    	for(i=1;i<=1000;i++){
    		if(!c.moveToNext()) break;
    	}
    	
    	
    	return i;
    }
    
    public Integer cuentaPartidasGanadas(){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{KEY_GANA}, KEY_P2HUMANO+"=0 AND "+ KEY_GANA+"=0", null, null, null, null);
    	if(!c.moveToFirst()) return 0;
    	int i;
    	for(i=1;i<=1000;i++){
    		if(!c.moveToNext()) break;
    	}
    	
    	return i;
    }
    
    public Integer cuentaPartidasPerdidas(){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{KEY_GANA}, KEY_P2HUMANO+"=0 AND "+ KEY_GANA+"=1", null, null, null, null);
    	if(!c.moveToFirst()) return 0;
    	int i;
    	for(i=1;i<=1000;i++){
    		if(!c.moveToNext()) break;
    	}
    	
    	return i;
    }
    
    public Integer cuentaPartidasEmpatadas(){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{KEY_GANA}, KEY_P2HUMANO+"=0 AND "+ KEY_GANA+"=-1", null, null, null, null);
    	if(!c.moveToFirst()) return 0;
    	int i;
    	for(i=1;i<=1000;i++){
    		if(!c.moveToNext()) break;
    	}
    	
    	return i;
    }
    
    public Boolean checkGanaEnGoogle(){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{KEY_GANA}, KEY_P2HUMANO+"=0 AND "+ KEY_GANA+"=0 AND "+KEY_P2DIFICULTAD+"=5", null, null, null, null);
    	return c.moveToFirst();
    }
    
    public Boolean checkHacker(){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{KEY_GANA}, KEY_P2HUMANO+"=0 AND "+ KEY_GANA+"=0 AND "+KEY_P2DIFICULTAD+">5", null, null, null, null);
    	return c.moveToFirst();

    }
    
    public void checkAll(){
    	cuentaPartidasEmpatadas();
    	cuentaPartidasGanadas();
    	cuentaPartidasJugadas();
    	cuentaPartidasPerdidas();
    	checkGanaEnGoogle();
    	checkHacker();
    }
    
    public int getBestScore(Integer level){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{KEY_P1PUNTOS}, KEY_P2DIFICULTAD+"="+level, null, null, null, null);
    	int batery = 0;
    	if(!c.moveToFirst()) return batery;
    	for(int i=0;true;i++){
    		if(!c.moveToNext()) break;
    		int tmp = c.getInt(0);
    		if(tmp>batery) batery = tmp;
    	}
    	return batery;
    }
    
    public int getBestTime(Integer level){
    	Cursor c = mDb.query(DATABASE_TABLE,new String[]{KEY_DURACION}, KEY_P2DIFICULTAD+"="+level, null, null, null, null);
    	int batery = 0;
    	if(!c.moveToFirst()) return batery;
    	for(int i=0;true;i++){
    		if(!c.moveToNext()) break;
    		int tmp = c.getInt(0);
    		if(tmp>batery) batery = tmp;
    	}
    	return batery;
    }
}