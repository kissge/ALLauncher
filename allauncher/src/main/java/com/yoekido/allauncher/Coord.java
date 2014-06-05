package com.yoekido.allauncher;

import android.graphics.RectF;

import java.util.List;

public class Coord {
    public float x, y;

    Coord(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static float pathDistance(List<Coord> pts1, List<Coord> pts2) {
        float d = 0.0f;
        for (int i = 0; i < pts1.size(); i++) {
            d += distance(pts1.get(i), pts2.get(i));
        }
        return d / pts1.size();
    }

    public static float pathLength(List<Coord> points) {
        float d = 0.0f;
        for (int i = 1; i < points.size(); i++) {
            d += distance(points.get(i - 1), points.get(i));
        }
        return d;
    }

    public static Coord centroid(List<Coord> points) {
        float x = 0.0f, y = 0.0f;
        for (Coord p : points) {
            x += p.x;
            y += p.y;
        }
        return new Coord(x / points.size(), x / points.size());
    }

    public static float distance(Coord p1, Coord p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static RectF boundingBox(List<Coord> points) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        for (Coord p : points) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }
        return new RectF(minX, minY, maxX, maxY);
    }

    public boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }
}
