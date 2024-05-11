package com.example.miviaje;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity{

    private SessionManager sessionManager;
    private Button guestButton;
    private Button loginButton;
    private ProgressBar mainProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        guestButton = findViewById(R.id.guestButton);
        loginButton = findViewById(R.id.loginButton);
        mainProgressBar = findViewById(R.id.mainProgressBar);

        if(sessionManager.isLoged() || sessionManager.isGuest()){
            Intent intent = new Intent(MainActivity.this,MapActivity.class);
            startActivity(intent);
        }else {
            loginButton.setVisibility(View.VISIBLE);
            guestButton.setVisibility(View.VISIBLE);
            mainProgressBar.setVisibility(View.INVISIBLE);
        }

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.loginAsGuest();
                Intent intent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This Option is under Development", Toast.LENGTH_SHORT).show();
            }
        });


    }

}