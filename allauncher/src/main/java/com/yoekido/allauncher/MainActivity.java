package com.yoekido.allauncher;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnTouchListener {

    private Bitmap background;
    private Canvas canvas;
    private Paint paint;
    private LinkedList<Coord> points;
    private Status status = Status.STANDBY;
    private Timer timer = null;
    private Handler handler;
    private OneDollarRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        BitmapDrawable wallpaper = (BitmapDrawable) getWallpaper();
        background = wallpaper.getBitmap();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Bitmap bitmap2 = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap2);
        canvas.drawBitmap(background, 0, 0, paint);

        paint = new Paint();
        paint.setStrokeWidth(30.0f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);

        imageView.setImageBitmap(bitmap2);
        imageView.setOnTouchListener(this);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setZOrderOnTop(true);
        SurfaceHolder sfhTrackHolder = surfaceView.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSLUCENT);

        paint.setColor(Color.argb(64, 255, 0, 0));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80.0f);
        canvas.drawText("Draw here", metrics.widthPixels / 2.0f, metrics.heightPixels * 3.0f / 4.0f + 20.0f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{60.0f, 90.0f}, 0));
        canvas.drawOval(new RectF(30.0f, metrics.heightPixels / 2.0f + 30.0f, metrics.widthPixels - 30.0f, metrics.heightPixels - 30.0f), paint);

        paint.setPathEffect(null);
        paint.setStyle(Paint.Style.FILL);

        points = new LinkedList<Coord>();
        handler = new Handler();

        recognizer = new OneDollarRecognizer();
    }

    ;

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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                points.clear();
                if (event.getY() * 2.0f < v.getHeight()) {
                    return true;
                }
                status = Status.GESTURE;
                if (timer != null) {
                    timer.cancel();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (status == Status.GESTURE) {
                    points.clear();
                    status = Status.STANDBY;
                }
                break;
            default:
                if (status == Status.GESTURE) {
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
                    }, 100);
                } else {
                    return true;
                }
        }

        canvas.drawBitmap(background, 0, 0, paint);

        if (status == Status.GESTURE) {
            if (!points.isEmpty() && points.get(points.size() - 1).equals(x, y)) {
                return true;
            }
            points.add(new Coord(x, y));
            paint.setColor(Color.rgb(27, 48, 117));
            for (int i = 0; i < points.size() - 1; i++)
                canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y, paint);
            paint.setColor(Color.rgb(255, 0, 0));
            canvas.drawCircle(x, y, 15.5f, paint);
        }

        findViewById(R.id.imageView).invalidate();
        return true;
    }

    protected void invoke() {
        if (points.size() < 10) {
            return;
        }
        paint.setColor(Color.rgb(255, 0, 0));
        for (int i = 0; i < points.size() - 1; i++)
            canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y, paint);
        findViewById(R.id.imageView).invalidate();
        status = Status.TAIL;

        Unistroke recognized = recognizer.recognize(points);
        String result = recognized == null ? "Nothing matched." : recognized.name;
        Toast.makeText(this, result + " (" + recognizer.score + ")", Toast.LENGTH_SHORT).show();
    }

    enum Status {STANDBY, GESTURE, TAIL}
}
