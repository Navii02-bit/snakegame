package com.example.snakegamevers2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions); // Set the new instructions layout

        // Back to Main Menu button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to StartupActivity (Main Menu)
                Intent intent = new Intent(InstructionsActivity.this, StartupActivity.class);
                startActivity(intent);
                finish(); // Close the instructions activity
            }
        });
    }
}
