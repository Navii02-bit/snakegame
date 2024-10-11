package com.example.snakegamevers2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SnakeGameView extends View {

    private static final int STEP_SIZE = 20;
    private static final int GAME_DELAY = 150;

    private Paint snakePaint;
    private Paint foodPaint;
    private Queue<Point> snakePoints;
    private Point foodPoint;

    private String direction = "RIGHT";
    private Random random = new Random();
    private Handler handler = new Handler();
    private boolean gameRunning = true;

    public SnakeGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        snakePaint = new Paint();
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.STROKE);
        snakePaint.setStrokeWidth(40);

        foodPaint = new Paint();
        foodPaint.setColor(Color.RED);
        foodPaint.setStyle(Paint.Style.FILL);

        snakePoints = new LinkedList<>();
        snakePoints.add(new Point(100, 100)); // Start point

        placeFoodRandomly();
    }

    public void update() {
        moveSnake();
        invalidate(); // Redraw the view
    }

    private void moveSnake() {
        Point head = snakePoints.peek(); // Current head of the snake
        Point newHead = new Point(head.x, head.y);

        switch (direction) {
            case "UP":
                newHead.y -= STEP_SIZE;
                break;
            case "DOWN":
                newHead.y += STEP_SIZE;
                break;
            case "LEFT":
                newHead.x -= STEP_SIZE;
                break;
            case "RIGHT":
                newHead.x += STEP_SIZE;
                break;
        }

        // Add the new head and remove the tail unless food is eaten
        if (Math.abs(newHead.x - foodPoint.x) < 40 && Math.abs(newHead.y - foodPoint.y) < 40) {
            snakePoints.add(newHead); // Grow the snake
            placeFoodRandomly();
        } else {
            snakePoints.add(newHead);
            snakePoints.poll(); // Remove the tail
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw food
        canvas.drawCircle(foodPoint.x, foodPoint.y, 20, foodPaint);

        // Draw snake
        Point first = snakePoints.peek();
        if (first != null) {
            for (Point p : snakePoints) {
                canvas.drawRect(p.x, p.y, p.x + 40, p.y + 40, snakePaint);
            }
        }
    }

    private void placeFoodRandomly() {
        int x = random.nextInt(getWidth() - 50) + 25;
        int y = random.nextInt(getHeight() - 50) + 25;
        foodPoint = new Point(x, y);
    }

    public boolean isGameOver() {
        // Check if the snake head hits the boundary (Game Over condition)
        Point head = snakePoints.peek();
        return head.x < 0 || head.y < 0 || head.x > getWidth() || head.y > getHeight();
    }

    public void setDirection(String newDirection) {
        // Prevent reversing direction
        if ((newDirection.equals("UP") && !direction.equals("DOWN")) ||
                (newDirection.equals("DOWN") && !direction.equals("UP")) ||
                (newDirection.equals("LEFT") && !direction.equals("RIGHT")) ||
                (newDirection.equals("RIGHT") && !direction.equals("LEFT"))) {
            direction = newDirection;
        }
    }

    private static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
