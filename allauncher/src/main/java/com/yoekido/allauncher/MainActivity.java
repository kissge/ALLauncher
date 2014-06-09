package com.yoekido.allauncher;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnTouchListener {

    private Bitmap background;
    private Canvas canvas;
    private Paint paint;
    private List<Coord> points;
    private Status status = Status.STANDBY;
    private Timer timer = null;
    private Handler handler;
    private OneDollarRecognizer recognizer;
    private DisplayMetrics metrics;
    private int selection = -1;
    private AppsManager appsManager;
    private List<AppsManager.App> candidates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        BitmapDrawable wallpaper = (BitmapDrawable) getWallpaper();
        background = wallpaper.getBitmap();

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawBitmap(background, 0, 0, paint);

        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);

        imageView.setImageBitmap(bitmap);
        imageView.setOnTouchListener(this);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setZOrderOnTop(true);
        SurfaceHolder sfhTrackHolder = surfaceView.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSLUCENT);

        paint.setColor(Color.argb(64, 255, 0, 0));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80.0f);
        canvas.drawText("Draw here", metrics.widthPixels / 2.0f, metrics.heightPixels * 0.5f + 20.0f, paint);
        paint.setStrokeWidth(30.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{60.0f, 90.0f}, 0));
        canvas.drawOval(new RectF(30.0f, 30.0f, metrics.widthPixels - 30.0f, metrics.heightPixels - 30.0f), paint);

        paint.setPathEffect(null);

        points = new LinkedList<Coord>();
        handler = new Handler();

        recognizer = new OneDollarRecognizer();

        appsManager = new AppsManager(this.getApplication());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (status == Status.STANDBY) {
            draw();
            status = Status.GESTURE;
            points.clear();
            points.add(new Coord(x, y));
            if (timer != null) {
                timer.cancel();
            }
        } else if (status == Status.GESTURE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (timer != null) {
                        timer.cancel();
                    }
                    status = Status.STANDBY;
                    draw();
                    break;
                default:
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer(true);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    invoke();
                                }
                            });
                        }
                    }, 50);
            }

            if (!points.get(points.size() - 1).equals(x, y)) {
                points.add(new Coord(x, y));
                draw();
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                status = Status.STANDBY;
                if (0 <= selection && selection < candidates.size()) {
                    startActivity(this.getApplication().getPackageManager().getLaunchIntentForPackage(candidates.get(selection).packageName));
                }
                draw();
            } else {
                draw(x, y);
            }
        }

        return true;
    }

    private void draw(float x, float y) {
        draw();
        paint.setStrokeWidth(30.0f);
        paint.setStyle(Paint.Style.STROKE);

        x = 4 * (x - points.get(points.size() - 1).x);
        y = 4 * (y - points.get(points.size() - 1).y);

        double length = Math.sqrt(x * x + y * y);
        if (length >= 1.6 * 80.0) {
            x *= 1.8 * 80.0 / length;
            y *= 1.8 * 80.0 / length;
            selection = (8 + (int) Math.round(4 * Math.atan2(y, x) / Math.PI)) % 8;
            float x2 = (float) (metrics.widthPixels * 0.5 + 2.6 * 80.0 * Math.cos(Math.PI * selection / 4));
            float y2 = (float) (280.0 + 2.6 * 80.0 * Math.sin(Math.PI * selection / 4));
            paint.setColor(Color.argb(64, 128, 128, 128));
            canvas.drawCircle(x2, y2, 80.0f, paint);
        } else {
            selection = -1;
        }

        paint.setColor(Color.rgb(88, 88, 255));
        canvas.drawLine(metrics.widthPixels * 0.5f, 280.0f,
                metrics.widthPixels * 0.5f + x, 280.0f + y, paint);

        findViewById(R.id.imageView).invalidate();
    }

    private void draw() {
        paint.setStrokeWidth(30.0f);
        canvas.drawBitmap(background, 0, 0, paint);

        if (status == Status.GESTURE) {
            paint.setColor(Color.rgb(27, 48, 117));
            for (int i = 0; i < points.size() - 1; i++) {
                canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y, paint);
            }
            paint.setColor(Color.rgb(255, 0, 0));
            canvas.drawCircle(points.get(points.size() - 1).x, points.get(points.size() - 1).y, 15.5f, paint);
        } else if (status == Status.TAIL) {
            paint.setColor(Color.argb(64, 255, 0, 0));
            Path path = new Path();
            path.moveTo(points.get(0).x, points.get(0).y);
            for (int i = 1; i < points.size(); i++) {
                path.lineTo(points.get(i).x, points.get(i).y);
            }
            canvas.drawPath(path, paint);

            paint.setColor(Color.argb(64, 128, 128, 128));
            for (int i = 0; i < 8; i++) {
                float x = (float) (metrics.widthPixels * 0.5 + 2.6 * 80.0 * Math.cos(Math.PI * i / 4));
                float y = (float) (280.0 + 2.6 * 80.0 * Math.sin(Math.PI * i / 4));
                if (candidates.size() > i && candidates.get(i) != null) {
                    int size = selection == i ? 60 : 40;
                    candidates.get(i).icon.setBounds((int) x - size, (int) y - size, (int) x + size, (int) y + size);
                    candidates.get(i).icon.draw(canvas);
                    paint.setTextSize(16.0f);
                    paint.setStrokeWidth(3.0f);
                    paint.setColor(Color.argb(128, 0, 0, 0));
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawText(candidates.get(i).name, x, y + size + 10, paint);
                    paint.setColor(Color.rgb(255, 255, 255));
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawText(candidates.get(i).name, x, y + size + 10, paint);
                }
            }
        }

        findViewById(R.id.imageView).invalidate();
    }

    private void invoke() {
        if (points.size() < 10 || status != Status.GESTURE) {
            return;
        }

        Unistroke recognized = recognizer.recognize(points);
        if (recognized != null) {
            status = Status.TAIL;
            candidates = appsManager.get(recognized.name.toLowerCase());
        } else {
            Toast.makeText(this, String.format("? (%2.1f%%)", recognizer.score * 100), Toast.LENGTH_SHORT).show();
            canvas.drawBitmap(background, 0, 0, paint);
            status = Status.STANDBY;
        }

        draw();
    }

    enum Status {STANDBY, GESTURE, TAIL}
}
