package com.example.miviaje;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SavedPathsActivity extends AppCompatActivity {

    private DatabaseHelper dbRutasHelper;
    private ImageView map;
    private ImageView card;

    private TextView testText;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_paths);

        testText = findViewById(R.id.testText);

        dbRutasHelper = new DatabaseHelper(this,"rutas.db");
        SQLiteDatabase dbRutas =dbRutasHelper.getReadableDatabase();
        Cursor cursor = dbRutas.rawQuery("Select * from rutas limit 1",null);

        cursor.moveToFirst();
        testText.setText(cursor.getString(cursor.getColumnIndex("nombre_ruta")));

        map = findViewById(R.id.mapImg);
        card = findViewById(R.id.cardImg);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedPathsActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedPathsActivity.this,ScanCardsActivity.class);
                startActivity(intent);
            }
        });


    }
}
