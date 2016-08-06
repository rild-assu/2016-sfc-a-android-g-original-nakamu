package com.lifeistech.android.myapplication02;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by Life_is_Tech on 16/08/05.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    static final long FPS = 30;
    static final long FRAME_TIME = 1000 / FPS;
    SurfaceHolder surfaceHolder;
    int screenWidth, screenHeight;
    Thread thread;
    Present present;
    Bitmap presentImage;
    Player player;
    Bitmap playerImage;
    Bitmap backgroundImage;
    int score = 0;
    int life = 3;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        Resources resources = context.getResources();
        backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.background_image);
        presentImage = BitmapFactory.decodeResource(resources, R.drawable.present_image);
        playerImage = BitmapFactory.decodeResource(resources, R.drawable.player_image);
    }

    @Override
    public void run() {
        player = new Player();
        present = new Present();
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(50);
        while (thread != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(backgroundImage, 0, 0, null);
            canvas.drawBitmap(presentImage, present.x, present.y, null);
            canvas.drawBitmap(playerImage, player.x, player.y, null);
            if (player.isEnter(present)) {
                present.reset();
                score += 10;
            } else if (present.y > screenHeight) {
                present.reset();
                life--;
            } else {
                present.update();
            }
            canvas.drawText("SCORE:" + score, 50, 150, textPaint);
            canvas.drawText("LIFE:" + life, 50, 300, textPaint);
            if (life <= 0) {
                canvas.drawText("Game Over", screenWidth / 6, screenHeight / 2, textPaint);
                surfaceHolder.unlockCanvasAndPost(canvas);
                break;
            }
            if (score % 100 == 0 && score != 0) {
                present.speed += 0.1f;
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(FRAME_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("speed" + present.speed, "");
        }
    }

    class Present {
        private static final int WIDTH = 100;
        private static final int HEIGHT = 100;

        float x, y;

        float speed = 14.0f;

        public Present() {
            Random random = new Random();
            x = random.nextInt(screenWidth - WIDTH);
            y = 0;
        }

        public void update() {
            y += speed;
        }

        public void reset() {
            Random random = new Random();
            x = random.nextInt(screenWidth - WIDTH);
            y = 0;
        }

    }

    class Player {
        final int WIDTH = 200;
        final int HEIGHT = 200;
        float x, y;

        public Player() {
            x = 0;
            y = screenHeight - HEIGHT;
        }

        public void move(float diffX) {
            this.x += diffX * 1.2;
            this.x = Math.max(0, x);
            this.x = Math.min(screenWidth - WIDTH, x);
        }

        public boolean isEnter(Present present) {
            if (present.x + Present.WIDTH > x && present.x < x + WIDTH &&
                    present.y + Present.HEIGHT > y && present.y < y + HEIGHT) {
                return true;
            }
            return false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        if (screenWidth > 0 && screenHeight > 0) {
            backgroundImage = Bitmap.createScaledBitmap(backgroundImage, screenWidth, screenHeight, true);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread = null;
    }

}