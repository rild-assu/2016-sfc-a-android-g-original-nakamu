package com.lifeistech.android.myapplication02;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        Resources resources = context.getResources();
        presentImage = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        if (presentImage == null) Log.d("GameView", "nullだよ");
        playerImage = BitmapFactory.decodeResource(resources, R.drawable.player_image);
    }

    @Override
    public void run() {
        player = new Player();
        present = new Present();
        while (thread != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(presentImage, present.x, present.y, null);
            canvas.drawBitmap(playerImage, player.x, player.y, null);
            if (present.y > screenHeight) {
                present.reset();
            } else {
                present.update();
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(FRAME_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Present {
        private static final int WIDTH = 100;
        private static final int HEIGHT = 100;

        float x, y;

        public Present() {
            Random random = new Random();
            x = random.nextInt(screenWidth - WIDTH);
            y = 0;
        }

        public void update() {
            y += 15.0f;
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
            this.x += diffX;
            this.x = Math.max(0, x);
            this.x = Math.min(screenWidth - WIDTH, x);
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
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread = null;
    }


}
