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

        paint.setStyle(Paint.Style.FILL);

        points = new LinkedList<Coord>();
        handler = new Handler();

        LinkedList<Unistroke> usList = new LinkedList<Unistroke>();
        usList.push(Unistroke.of("Triangle",
                new Coord[]{new Coord(137, 139), new Coord(135, 141), new Coord(133, 144), new Coord(132, 146), new Coord(130, 149), new Coord(128, 151), new Coord(126, 155), new Coord(123, 160), new Coord(120, 166), new Coord(116, 171), new Coord(112, 177), new Coord(107, 183), new Coord(102, 188), new Coord(100, 191), new Coord(95, 195), new Coord(90, 199), new Coord(86, 203), new Coord(82, 206), new Coord(80, 209), new Coord(75, 213), new Coord(73, 213), new Coord(70, 216), new Coord(67, 219), new Coord(64, 221), new Coord(61, 223), new Coord(60, 225), new Coord(62, 226), new Coord(65, 225), new Coord(67, 226), new Coord(74, 226), new Coord(77, 227), new Coord(85, 229), new Coord(91, 230), new Coord(99, 231), new Coord(108, 232), new Coord(116, 233), new Coord(125, 233), new Coord(134, 234), new Coord(145, 233), new Coord(153, 232), new Coord(160, 233), new Coord(170, 234), new Coord(177, 235), new Coord(179, 236), new Coord(186, 237), new Coord(193, 238), new Coord(198, 239), new Coord(200, 237), new Coord(202, 239), new Coord(204, 238), new Coord(206, 234), new Coord(205, 230), new Coord(202, 222), new Coord(197, 216), new Coord(192, 207), new Coord(186, 198), new Coord(179, 189), new Coord(174, 183), new Coord(170, 178), new Coord(164, 171), new Coord(161, 168), new Coord(154, 160), new Coord(148, 155), new Coord(143, 150), new Coord(138, 148), new Coord(136, 148)}));
        usList.push(Unistroke.of("check",
                new Coord[]{new Coord(127, 141), new Coord(124, 140), new Coord(120, 139), new Coord(118, 139), new Coord(116, 139), new Coord(111, 140), new Coord(109, 141), new Coord(104, 144), new Coord(100, 147), new Coord(96, 152), new Coord(93, 157), new Coord(90, 163), new Coord(87, 169), new Coord(85, 175), new Coord(83, 181), new Coord(82, 190), new Coord(82, 195), new Coord(83, 200), new Coord(84, 205), new Coord(88, 213), new Coord(91, 216), new Coord(96, 219), new Coord(103, 222), new Coord(108, 224), new Coord(111, 224), new Coord(120, 224), new Coord(133, 223), new Coord(142, 222), new Coord(152, 218), new Coord(160, 214), new Coord(167, 210), new Coord(173, 204), new Coord(178, 198), new Coord(179, 196), new Coord(182, 188), new Coord(182, 177), new Coord(178, 167), new Coord(170, 150), new Coord(163, 138), new Coord(152, 130), new Coord(143, 129), new Coord(140, 131), new Coord(129, 136), new Coord(126, 139)}));
        recognizer = new OneDollarRecognizer(usList);
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
