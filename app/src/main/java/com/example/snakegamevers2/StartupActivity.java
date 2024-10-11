package com.example.snakegamevers2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup); // Set the startup layout

        // Reference to the "Start Game" button
        Button startGameButton = findViewById(R.id.startGameButton);

        // Set a click listener for the button to start the game
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MainActivity (Snake game)
                Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the startup screen so it cannot be returned to
            }
        });
    }
}
