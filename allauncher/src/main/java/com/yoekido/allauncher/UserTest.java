package com.yoekido.allauncher;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserTest {
    private int count;
    private Handler handler;
    private int countdown;
    private Canvas canvas;
    private Paint paint;
    private View imageView;
    private Bitmap background;
    private List<String> tests;
    private Activity activity;
    private int last;

    UserTest(Canvas canvas, Paint paint, View imageView, Bitmap background, AppsManager appsManager, Activity activity) {
        count = 0;
        handler = new Handler();
        this.canvas = canvas;
        this.paint = paint;
        this.imageView = imageView;
        this.background = background;
        this.activity = activity;
        tests = new LinkedList<String>();
        for (Hashtable.Entry<String, List<AppsManager.App>> apps : appsManager.dictionary.entrySet()) {
            for (AppsManager.App app : apps.getValue()) {
                if (app != null) {
                    tests.add(app.name);
                }
            }
        }
    }

    public void start() {
        if (count == 0) {
            countdown();
            last = -1;
            Collections.shuffle(tests);
        }
    }

    public void countdown() {
        countdown = 4;
        final Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (--countdown <= 0) {
                            timer.cancel();
                            next(null);
                        } else {
                            paint.setColor(Color.rgb(128, 128, 128));
                            showText(Integer.toString(countdown));
                        }
                    }
                });
            }
        }, 100, 1000);
    }

    private void showText(String text) {
        paint.setTextSize(130.0f);
        paint.setStrokeWidth(30.0f);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(background, 0, 0, paint);
        canvas.drawText(text, 270, 480, paint);
        imageView.invalidate();
    }

    public void next(String answer) {
        if (count > 0) {
            boolean correct = tests.get(count - 1).equals(answer);
            Toast.makeText(activity, correct ? "Correct!" : "Wrong :(", Toast.LENGTH_SHORT).show();
            Log.d("UserTest", "Test " + count + " " + (correct ? "correct" : "wrong"));
            if (count >= tests.size()) {
                Log.d("UserTest", "-- finish --");
                paint.setColor(Color.rgb(255, 0, 0));
                showText("Finish");
                count = 0;
                return;
            }
        } else if (answer != null) {
            Toast.makeText(activity, answer, Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d("UserTest", "-- start --");
        }
        paint.setColor(Color.rgb(128, 128, 0));
        Log.d("UserTest", "Test " + ++count + " start");
        showText(tests.get(count - 1));
    }

    public void touch() {
        if (last != count) {
            Log.d("UserTest", "Test " + count + " touch");
            last = count;
        }
    }
}
