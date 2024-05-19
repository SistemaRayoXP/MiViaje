package com.example.miviaje;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SavedPathsActivity extends AppCompatActivity {

    private DatabaseHelper dbRutasHelper;
    private ImageView map;
    private ImageView card;
    private Context context;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_paths);

        map = findViewById(R.id.mapImg);
        card = findViewById(R.id.cardImg);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedPathsActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedPathsActivity.this, ScanCardsActivity.class);
                startActivity(intent);
            }
        });
        try {
            createElementsFromDatabase();
        }finally {

        }

    }
    private void createElementsFromDatabase() {
        SQLiteDatabase db = openOrCreateDatabase("rutas_fav.db", MODE_PRIVATE, null);

        // Consulta los datos de la base de datos
        Cursor cursor = db.rawQuery("SELECT * FROM rutas", null);

        // Obtiene una referencia al ScrollView donde se agregarán los elementos
        ScrollView scrollView = findViewById(R.id.scroll_view);

        // Crea un LinearLayout como contenedor de las vistas
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Itera sobre el cursor para crear los elementos dinámicamente
        while (cursor.moveToNext()) {
            String nombreRuta = cursor.getString(cursor.getColumnIndex("nombre_ruta"));
            String destino = cursor.getString(cursor.getColumnIndex("destino"));

            // Crea un TextView para mostrar el nombre de la ruta
            TextView textViewRuta = new TextView(this);
            LinearLayout.LayoutParams layoutParamsRuta = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParamsRuta.setMargins(dpToPx(8), dpToPx(15), dpToPx(8), dpToPx(0)); // Margenes 8dp izquierda/derecha, 15dp arriba/abajo
            textViewRuta.setLayoutParams(layoutParamsRuta);
            textViewRuta.setText("Ruta: " + nombreRuta);
            textViewRuta.setBackgroundColor(ContextCompat.getColor(this,R.color.mainYellow ));
            textViewRuta.setGravity(Gravity.CENTER);
            textViewRuta.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            textViewRuta.setTextColor(ContextCompat.getColor(this,R.color.mainGreen ));

            // Crea un TextView para mostrar el destino
            TextView textViewDestino = new TextView(this);
            LinearLayout.LayoutParams layoutParamsDestino = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParamsDestino.setMargins(dpToPx(8), dpToPx(0), dpToPx(8), dpToPx(15)); // Margenes 8dp izquierda/derecha, 15dp arriba/abajo
            textViewDestino.setLayoutParams(layoutParamsDestino);
            textViewDestino.setText("Destino: " + destino);
            textViewDestino.setBackgroundColor(ContextCompat.getColor(this,R.color.mainYellow));
            textViewDestino.setGravity(Gravity.CENTER);
            textViewDestino.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            textViewDestino.setTextColor(ContextCompat.getColor(this,R.color.mainGreen ));

            // Agrega los TextViews al LinearLayout
            linearLayout.addView(textViewRuta);
            linearLayout.addView(textViewDestino);
        }

        // Agrega el LinearLayout al ScrollView
        scrollView.addView(linearLayout);

        // Cierra el cursor y la conexión a la base de datos
        cursor.close();
        db.close();
    }

    // Función para convertir dp a píxeles
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}