package com.yoekido.allauncher;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class AppsManager {
    public Hashtable<String, List<App>> dictionary;

    AppsManager(Application application) {
        PackageManager pm = application.getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(0);
        dictionary = new Hashtable<String, List<App>>();
        for (ApplicationInfo appInfo : applications) {
            App app = new App(pm, appInfo);
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                for (int i = 0; i < app.name.length(); i++) {
                    Character c = app.name.charAt(i);
                    if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                        String label = c.toString().toLowerCase();
                        List<App> list = dictionary.containsKey(label) ? dictionary.get(label) : reservedList();
                        add(list, app);
                        dictionary.put(label, list);
                        break;
                    }
                }
            }
        }
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
    }
}