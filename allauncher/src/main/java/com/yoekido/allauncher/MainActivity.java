package com.yoekido.allauncher;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;


public class MainActivity extends Activity implements View.OnTouchListener {

    private Bitmap background;
    private Canvas canvas;
    private Paint paint;
    private LinkedList<Coord> points;

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
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        imageView.setImageBitmap(bitmap2);
        imageView.setOnTouchListener(this);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setZOrderOnTop(true);
        SurfaceHolder sfhTrackHolder = surfaceView.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSLUCENT);

        points = new LinkedList<Coord>();
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                points.clear();
                points.add(new Coord(x, y));
                break;
        }

        points.add(new Coord(x, y));

        canvas.drawBitmap(background, 0, 0, paint);
        paint.setColor(Color.rgb(27, 48, 117));
        for (int i = 0; i < points.size() - 1; i++)
            canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y, paint);
        paint.setColor(Color.rgb(255, 0, 0));
        canvas.drawCircle(x, y, 15.5f, paint);

        findViewById(R.id.imageView).invalidate();
        return true;
    }
}
