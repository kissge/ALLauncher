package com.yoekido.allauncher;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
        dictionary.put("c",
                Arrays.asList(new App(android.R.drawable.ic_menu_camera, "Camera"),
                        new App(android.R.drawable.ic_menu_compass, "Compass"),
                        new App(android.R.drawable.ic_menu_call, "Call"), null,
                        new App(android.R.drawable.ic_menu_month, "Calendar"), null,
                        new App(android.R.drawable.ic_menu_myplaces, "Check-in"), null)
        );
        dictionary.put("m",
                Arrays.asList(new App(android.R.drawable.ic_dialog_email, "Mail"), null,
                        new App(android.R.drawable.ic_menu_edit, "Memo"), null,
                        new App(android.R.drawable.ic_menu_mapmode, "Map"), null, null, null)
        );
        dictionary.put("p",
                Arrays.asList(new App(android.R.drawable.ic_media_play, "Play"), null,
                        new App(android.R.drawable.ic_media_pause, "Pause"), null,
                        new App(android.R.drawable.ic_menu_gallery, "Picture"), null, null, null)
        );
        dictionary.put("s",
                Arrays.asList(new App(android.R.drawable.ic_menu_share, "Share"), null,
                        new App(android.R.drawable.ic_menu_search, "Search"), null,
                        new App(android.R.drawable.ic_menu_preferences, "Settings"), null,
                        new App(android.R.drawable.ic_menu_save, "Save"), null)
        );
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
    }
}