package com.example.snakegamevers2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout gameBoard;
    private TextView scoreText, highScoreText;
    private int score = 0;
    private int highScore = 0; // Variable to store high score

    private ImageView snakeHead, food;
    private Handler handler = new Handler();
    private String currentDirection = "RIGHT";
    private Runnable gameLoop;
    private boolean gameRunning = true;
    private static final int STEP_SIZE = 60; // Movement step size
    private static final int GAME_DELAY = 150; // Delay for smoother movement
    private Random random = new Random();
    private List<ImageView> snakeBody = new ArrayList<>();

    private Button restartButton, mainMenuButton;

    private SharedPreferences sharedPreferences; // SharedPreferences to store high score
    private static final String PREFS_NAME = "SnakeGamePrefs";
    private static final String HIGH_SCORE_KEY = "highScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBoard = findViewById(R.id.gameBoard);
        scoreText = findViewById(R.id.scoreText);
        highScoreText = findViewById(R.id.highScoreText);

        Button upButton = findViewById(R.id.upButton);
        Button downButton = findViewById(R.id.downButton);
        Button leftButton = findViewById(R.id.leftButton);
        Button rightButton = findViewById(R.id.rightButton);
        restartButton = findViewById(R.id.restartButton);
        mainMenuButton = findViewById(R.id.mainMenuButton); // New Main Menu Button

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load the saved high score
        highScore = sharedPreferences.getInt(HIGH_SCORE_KEY, 0);
        updateHighScoreDisplay();

        // Set up the control buttons
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentDirection.equals("DOWN")) {
                    currentDirection = "UP";
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentDirection.equals("UP")) {
                    currentDirection = "DOWN";
                }
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentDirection.equals("RIGHT")) {
                    currentDirection = "LEFT";
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentDirection.equals("LEFT")) {
                    currentDirection = "RIGHT";
                }
            }
        });

        // Restart Button logic
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame(); // Restart the game when clicked
            }
        });

        // Main Menu Button logic - Navigates back to the StartupActivity
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartupActivity.class);
                startActivity(intent);
                finish(); // Close the MainActivity so it's removed from the back stack
            }
        });

        // Initialize the game after the gameBoard has been laid out
        gameBoard.post(new Runnable() {
            @Override
            public void run() {
                initializeGame();
            }
        });
    }

    private void initializeGame() {
        // Reset game state for restart
        gameRunning = true;
        currentDirection = "RIGHT"; // Reset the direction to the default (RIGHT)
        score = 0;
        updateScoreDisplay(); // Reset score display
        snakeBody.clear(); // Clear snake body list

        // Create the snake head
        snakeHead = new ImageView(this);
        snakeHead.setImageResource(android.R.drawable.presence_online);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60); // Size for snake head
        params.leftMargin = 0;
        params.topMargin = 0;
        gameBoard.addView(snakeHead, params);
        snakeBody.add(snakeHead); // Add snake head to body list

        // Create the food
        food = new ImageView(this);
        food.setImageResource(android.R.drawable.star_big_on);
        RelativeLayout.LayoutParams foodParams = new RelativeLayout.LayoutParams(60, 60);
        gameBoard.addView(food, foodParams);
        placeFoodRandomly(); // Place food randomly on the board

        // Start the game loop
        startGameLoop(); // Start game loop here
    }

    private void startGameLoop() {
        gameLoop = new Runnable() {
            @Override
            public void run() {
                if (gameRunning) {
                    moveSnake();
                    handler.postDelayed(this, GAME_DELAY);
                }
            }
        };
        handler.post(gameLoop); // Ensure game loop is always started
    }

    private void moveSnake() {
        RelativeLayout.LayoutParams headParams = (RelativeLayout.LayoutParams) snakeHead.getLayoutParams();

        // Store previous position of the snake head
        int prevX = headParams.leftMargin;
        int prevY = headParams.topMargin;

        // Move snake head based on the current direction
        switch (currentDirection) {
            case "UP":
                headParams.topMargin -= STEP_SIZE;
                break;
            case "DOWN":
                headParams.topMargin += STEP_SIZE;
                break;
            case "LEFT":
                headParams.leftMargin -= STEP_SIZE;
                break;
            case "RIGHT":
                headParams.leftMargin += STEP_SIZE;
                break;
        }

        // Check for boundary collision (Game Over)
        if (headParams.leftMargin < 0 || headParams.topMargin < 0 ||
                headParams.leftMargin + snakeHead.getWidth() > gameBoard.getWidth() ||
                headParams.topMargin + snakeHead.getHeight() > gameBoard.getHeight()) {
            gameOver();
            return;
        }

        moveSnakeBody(prevX, prevY); // Move the body

        // Check for food collision
        if (checkCollision(snakeHead, food)) {
            growSnake(prevX, prevY); // Grow the snake
            placeFoodRandomly(); // Reposition food
            increaseScore(); // Update the score
        }

        snakeHead.setLayoutParams(headParams); // Update the position of the head
    }

    private void moveSnakeBody(int prevX, int prevY) {
        for (int i = 1; i < snakeBody.size(); i++) {
            ImageView segment = snakeBody.get(i);
            RelativeLayout.LayoutParams segmentParams = (RelativeLayout.LayoutParams) segment.getLayoutParams();

            int tempX = segmentParams.leftMargin;
            int tempY = segmentParams.topMargin;

            segmentParams.leftMargin = prevX;
            segmentParams.topMargin = prevY;

            prevX = tempX;
            prevY = tempY;

            segment.setLayoutParams(segmentParams);
        }
    }

    private void growSnake(int x, int y) {
        // Create a new segment for the snake
        ImageView newSegment = new ImageView(this);
        newSegment.setImageResource(android.R.drawable.presence_online);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60);
        params.leftMargin = x;
        params.topMargin = y;
        gameBoard.addView(newSegment, params);
        snakeBody.add(newSegment); // Add the new segment to the snake body
    }

    private boolean checkCollision(View v1, View v2) {
        // Check if two views are colliding (overlapping)
        int[] loc1 = new int[2];
        int[] loc2 = new int[2];
        v1.getLocationOnScreen(loc1);
        v2.getLocationOnScreen(loc2);
        return loc1[0] < loc2[0] + v2.getWidth() && loc1[0] + v1.getWidth() > loc2[0] &&
                loc1[1] < loc2[1] + v2.getHeight() && loc1[1] + v1.getHeight() > loc2[1];
    }

    private void placeFoodRandomly() {
        // Place the food at a random position within the game board
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) food.getLayoutParams();
        params.leftMargin = random.nextInt(gameBoard.getWidth() - food.getWidth());
        params.topMargin = random.nextInt(gameBoard.getHeight() - food.getHeight());
        food.setLayoutParams(params);
    }

    private void increaseScore() {
        score++; // Increase the score by 1
        updateScoreDisplay();

        // Check if the new score is higher than the current high score
        if (score > highScore) {
            highScore = score;
            updateHighScoreDisplay();
            saveHighScore(); // Save the new high score
        }
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + score);
    }

    private void updateHighScoreDisplay() {
        highScoreText.setText("High Score: " + highScore);
    }

    private void saveHighScore() {
        // Save the high score using SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HIGH_SCORE_KEY, highScore);
        editor.apply();
    }

    private void gameOver() {
        gameRunning = false; // Stop the game loop
        Toast.makeText(this, "Game Over! Final Score: " + score, Toast.LENGTH_SHORT).show();
    }

    private void restartGame() {
        // Stop the game
        gameRunning = false;
        handler.removeCallbacks(gameLoop); // Stop the game loop

        // Reset everything
        gameBoard.removeAllViews(); // Clear the game board

        // Reset the game state and reinitialize the snake and food
        initializeGame();
    }
}
