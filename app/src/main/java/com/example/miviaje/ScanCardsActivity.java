package com.example.miviaje;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class ScanCardsActivity extends AppCompatActivity {

    private ImageView map;
    private ImageView favPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_cards);

        map = findViewById(R.id.mapImg);
        favPaths = findViewById(R.id.favImg);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanCardsActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
        favPaths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanCardsActivity.this,SavedPathsActivity.class);
                startActivity(intent);
            }
        });


    }
}
