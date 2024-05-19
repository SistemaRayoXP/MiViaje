package com.example.miviaje;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class RouteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rutas_fav.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "rutas";
    private static final String COLUMN_NAME_RUTA = "nombre_ruta";
    private static final String COLUMN_DESTINO = "destino";
    private static final String COLUMN_RUTA = "ruta";
    private Context context;

    public RouteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_RUTA + " TEXT, " +
                COLUMN_DESTINO + " TEXT, " +
                COLUMN_RUTA + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addRoute(String nombreRuta, String destino, List<LatLng> ruta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_RUTA, nombreRuta);
        values.put(COLUMN_DESTINO, destino);

        // Convertir List<LatLng> a JSON
        JSONArray jsonArray = new JSONArray();
        for (LatLng point : ruta) {
            JSONObject jsonPoint = new JSONObject();
            try {
                jsonPoint.put("latitude", point.latitude);
                jsonPoint.put("longitude", point.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonPoint);
        }
        values.put(COLUMN_RUTA, jsonArray.toString());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean databaseExists() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }
}
