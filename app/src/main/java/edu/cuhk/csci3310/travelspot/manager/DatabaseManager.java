package edu.cuhk.csci3310.travelspot.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.csci3310.travelspot.model.Spot;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "travelSpotDB";
    private final static String SPOT_TABLE = "Spot";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String NOTE = "note";
    private static final String LOCATION = "location";
    private static final String COLOR = "color";
    private static final String FINISH = "finish";

    private static final String CREATE_SPOT_TABLE = "CREATE TABLE " + SPOT_TABLE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE + " TEXT, "
            + LATITUDE + " REAL, "
            + LONGITUDE + " REAL, "
            + NOTE + " TEXT, "
            + LOCATION + " TEXT, "
            + COLOR + " TEXT, "
            + FINISH + " INTEGER )";

    public DatabaseManager(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SPOT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL = "DROP TABLE IF EXISTS " + SPOT_TABLE;
        db.execSQL(SQL);
        onCreate(db);
    }

    public long insertSpot(Spot spot){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TITLE, spot.getTitle());
        contentValues.put(LATITUDE, spot.getLatitude());
        contentValues.put(LONGITUDE, spot.getLongitude());
        contentValues.put(NOTE, spot.getNote());
        contentValues.put(LOCATION, spot.getLocation());
        contentValues.put(COLOR, spot.getColor());
        contentValues.put(FINISH, spot.getFinish());


        long result = db.insert("Spot",null,contentValues);

        return result;
    }

    public Boolean updateSpot(Spot spot){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, spot.getId());
        contentValues.put(TITLE, spot.getTitle());
        contentValues.put(LATITUDE, spot.getLatitude());
        contentValues.put(LONGITUDE, spot.getLongitude());
        contentValues.put(NOTE, spot.getNote());
        contentValues.put(LOCATION, spot.getLocation());
        contentValues.put(COLOR, spot.getColor());
        contentValues.put(FINISH, spot.getFinish());

        Cursor cursor = db.rawQuery("SELECT * FROM Spot WHERE id = ?",new String[] {String.valueOf(spot.getId())});

        if (cursor.getCount()>0) {
            long result = db.update("Spot", contentValues, "id=?", new String[]{String.valueOf(spot.getId())});
            if (result == -1) {
                return false;
            }
            return true;
        }
        return false;
    }

    public List<Spot> getAllSpots(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Spot> spots = new ArrayList<>();

        Cursor cursor = null;
        db.beginTransaction();
        try{
            cursor = db.rawQuery("SELECT * FROM Spot",null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        Spot spot = new Spot();
                        spot.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                        spot.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                        spot.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE)));
                        spot.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE)));
                        spot.setNote(cursor.getString(cursor.getColumnIndexOrThrow(NOTE)));
                        spot.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(LOCATION)));
                        spot.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLOR)));
                        spot.setFinish(cursor.getInt(cursor.getColumnIndexOrThrow(FINISH)));
                        spots.add(spot);
                    } while(cursor.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cursor != null;
            cursor.close();
        }

        return spots;
    }

    public Spot getSpot(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Spot spot = null;
        Cursor cursor = null;
        db.beginTransaction();
        try{
            cursor = db.rawQuery("SELECT * FROM " + SPOT_TABLE + " WHERE id = ?",new String[] {String.valueOf(id)});

            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        spot = new Spot();
                        spot.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                        spot.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                        spot.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE)));
                        spot.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE)));
                        spot.setNote(cursor.getString(cursor.getColumnIndexOrThrow(NOTE)));
                        spot.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(LOCATION)));
                        spot.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLOR)));
                        spot.setFinish(cursor.getInt(cursor.getColumnIndexOrThrow(FINISH)));
                    } while(cursor.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cursor != null;
            cursor.close();
        }

        return spot;
    }

    public void updateNote(int id, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NOTE, note);
        db.update(SPOT_TABLE, contentValues, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteSpot(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SPOT_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
