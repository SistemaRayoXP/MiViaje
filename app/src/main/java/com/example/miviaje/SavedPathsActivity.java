package com.example.miviaje;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SavedPathsActivity extends AppCompatActivity {

    private ImageView map;
    private ImageView card;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_paths);

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
