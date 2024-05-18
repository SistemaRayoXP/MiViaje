package com.example.miviaje;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME_1 = "rutas.db";
    private static String DB_NAME_2 = "rutas_gps.db";
    private Context context;
    private String dbName;

    public DatabaseHelper(Context context, String dbName) {
        super(context, dbName, null, 1);
        this.context = context;
        this.dbName = dbName;
        if (!databaseExists()) {
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new RuntimeException("Error copiando la base de datos: " + dbName, e);
            }
        }
    }

    private boolean databaseExists() {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private void copyDatabase() throws IOException {
        InputStream input = context.getAssets().open(dbName);
        String outFileName = context.getDatabasePath(dbName).getPath();
        File file = new File(outFileName);
        file.getParentFile().mkdirs();
        OutputStream output = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        input.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No es necesario implementar esto aun...
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No es necesario implementar esto aun...
    }

    public Cursor getRutasCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id, nombre_ruta, coordenadas FROM rutas", null);
    }
}
