package com.yoekido.allauncher;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class AppsManager {
    public Hashtable<String, List<App>> dictionary;
    protected Application application;

    AppsManager(Application application) {
        // dummy data
        this.application = application;
        dictionary = new Hashtable<String, List<App>>();
        dictionary.put("a", Arrays.asList(new App("amber", 0xffbf00),
                new App("amethyst", 0x9966cc),
                new App("apricot", 0xfbceb1), null,
                new App("aquamarine", 0x7fffd4), null,
                new App("azure", 0x007fff), null));
        dictionary.put("b", Arrays.asList(new App("baby blue", 0x89cff0),
                new App("beige", 0xf5f5dc),
                new App("black", 0x000000), null,
                new App("blue", 0x0000ff),
                new App("bronze", 0xcd7f32),
                new App("brown", 0x964b00), null));
        dictionary.put("c", Arrays.asList(new App("cerulean", 0x007ba7),
                new App("chocolate", 0x7b3f00),
                new App("cobalt blue", 0x0047ab),
                new App("coffee", 0x6f4e37),
                new App("copper", 0xb87333),
                new App("coral", 0xf88379),
                new App("crimson", 0xdc143c),
                new App("cyan", 0x00ffff)));
        dictionary.put("e", Arrays.asList(new App("emerald", 0x50c878), null, null, null, null, null, null, null));
        dictionary.put("g", Arrays.asList(new App("gold", 0xffd700), null,
                new App("gray", 0x808080), null,
                new App("green", 0x00ff00), null, null, null));
        dictionary.put("l", Arrays.asList(new App("lavender", 0xb57edc), null,
                new App("lemon", 0xfff700), null,
                new App("lime", 0xbfff00), null, null, null));
        dictionary.put("m", Arrays.asList(new App("magenta", 0xff00ff), null, null, null,
                new App("maroon", 0x800000), null, null, null));
        dictionary.put("n", Arrays.asList(new App("navy blue", 0x000080), null, null, null, null, null, null, null));
        dictionary.put("o", Arrays.asList(new App("olive", 0x808000), null, null, null,
                new App("orange", 0xffa500), null, null, null));
        dictionary.put("p", Arrays.asList(new App("peach", 0xffe5b4),
                new App("pear", 0xd1e231),
                new App("persian blue", 0x1c39bb), null,
                new App("pink", 0xffc0cb),
                new App("plum", 0x8e4585),
                new App("purple", 0x800080), null));
        dictionary.put("r", Arrays.asList(new App("raspberry", 0xe30b5c), null,
                new App("red", 0xff0000), null,
                new App("rose", 0xff007f), null,
                new App("ruby", 0xe0115f), null));
        dictionary.put("s", Arrays.asList(new App("salmon", 0xfa8072), null,
                new App("sapphire", 0x0f52ba), null,
                new App("scarlet", 0xff2400), null,
                new App("silver", 0xc0c0c0), null));
        dictionary.put("v", Arrays.asList(new App("violet", 0xee82ee), null, null, null,
                new App("viridian", 0x40826d), null, null, null));
        dictionary.put("w", Arrays.asList(new App("white", 0xffffff), null, null, null, null, null, null, null));
    }

    public List<App> get(String label) {
        if (dictionary.containsKey(label)) {
            return dictionary.get(label);
        } else {
            return new LinkedList<App>();
        }
    }

    private List<App> reservedList() {
        List<App> list = new LinkedList<App>();
        for (int i = 0; i < 8; i++) {
            list.add(null);
        }
        return list;
    }

    private void add(List<App> list, App element) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                list.set(i, element);
                return;
            }
        }
    }

    public class App {
        public Drawable icon;
        public String name;
        public String packageName;

        App(PackageManager pm, ApplicationInfo info) {
            this.icon = info.loadIcon(pm);
            this.name = info.loadLabel(pm).toString();
            this.packageName = info.packageName;
        }

        App(int icon, String name) {
            this.icon = application.getResources().getDrawable(icon);
            this.name = name;
            this.packageName = null;
        }

        App(String name, int color) {
            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(color | 0xff000000);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0.0f, 0.0f, 100.0f, 100.0f, paint);
            this.icon = new BitmapDrawable(application.getResources(), bitmap);
            this.name = name;
        }
    }
}