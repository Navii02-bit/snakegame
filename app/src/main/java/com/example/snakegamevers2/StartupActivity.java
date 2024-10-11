package com.example.snakegamevers2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StartupActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SnakeGamePrefs";
    private static final String HIGH_SCORE_KEY = "highScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Start Game Button
        Button startGameButton = findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the main game activity
                Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Instructions Button
        Button instructionsButton = findViewById(R.id.instructionsButton);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the instructions activity
                Intent intent = new Intent(StartupActivity.this, InstructionsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize SharedPreferences for high score management
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Reset High Score Button
        Button resetHighScoreButton = findViewById(R.id.resetHighScoreButton);
        resetHighScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to reset high score
                resetHighScore();
            }
        });
    }

    // Method to reset high score
    private void resetHighScore() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HIGH_SCORE_KEY, 0); // Set the high score to 0
        editor.apply();

        // Show a toast message to inform the user
        Toast.makeText(this, "High Score has been reset!", Toast.LENGTH_SHORT).show();
    }
}
