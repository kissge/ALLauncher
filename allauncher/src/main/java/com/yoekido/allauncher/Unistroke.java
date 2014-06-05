package com.yoekido.allauncher;

import android.graphics.RectF;

import java.util.LinkedList;
import java.util.List;

public class Unistroke {

    static final float phi = 0.6180339887498949f; // Golden Ratio

    public String name;
    public List<Coord> points;

    Unistroke(String name, List<Coord> points) {
        this.name = name;
        this.points = normalize(points);
    }

    public static Unistroke of(String name, Coord[] points) {
        List<Coord> points2 = new LinkedList<Coord>();
        for (Coord p : points) {
            points2.add(p);
        }
        return new Unistroke(name, points2);
    }

    public static List<Coord> normalize(List<Coord> points) {
        List<Coord> points2 = resample(points, OneDollarRecognizer.numberOfPoints);
        double radians = indicativeAngle(points2);
        List<Coord> points3 = rotateBy(points2, -radians);
        List<Coord> points4 = scaleTo(points3, OneDollarRecognizer.squareSize);
        return translateTo(points4, OneDollarRecognizer.origin);
    }

    public static List<Coord> resample(List<Coord> points, int n) {
        float I = Coord.pathLength(points) / (n - 1);
        float D = 0.0f;
        List<Coord> newPoints = new LinkedList<Coord>();
        newPoints.add(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            float d = Coord.distance(points.get(i - 1), points.get(i));
            if (D + d >= I) {
                float qx = points.get(i - 1).x + ((I - D) / d) * (points.get(i).x - points.get(i - 1).x);
                float qy = points.get(i - 1).y + ((I - D) / d) * (points.get(i).y - points.get(i - 1).y);
                Coord q = new Coord(qx, qy);
                newPoints.add(q);
                points.add(i, q);
                D = 0.0f;
            } else {
                D += d;
            }
        }
        if (newPoints.size() == n - 1) {
            newPoints.add(points.get(points.size() - 1));
        }
        return newPoints;
    }

    public static double indicativeAngle(List<Coord> points) {
        Coord c = Coord.centroid(points);
        return Math.atan2(c.y - points.get(0).y, c.x - points.get(0).x);
    }

    public static List<Coord> rotateBy(List<Coord> points, double radians) {
        Coord c = Coord.centroid(points);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        List<Coord> newPoints = new LinkedList<Coord>();
        for (Coord p : points) {
            float qx = (p.x - c.x) * cos - (p.y - c.y) * sin + c.x;
            float qy = (p.x - c.x) * sin + (p.y - c.y) * cos + c.y;
            newPoints.add(new Coord(qx, qy));
        }
        return newPoints;
    }

    public static List<Coord> scaleTo(List<Coord> points, double size) {
        RectF B = Coord.boundingBox(points);
        List<Coord> newPoints = new LinkedList<Coord>();
        for (Coord p : points) {
            float qx = (float) (p.x * size / B.width());
            float qy = (float) (p.y * size / B.height());
            newPoints.add(new Coord(qx, qy));
        }
        return newPoints;
    }

    public static List<Coord> translateTo(List<Coord> points, Coord pt) {
        Coord c = Coord.centroid(points);
        List<Coord> newPoints = new LinkedList<Coord>();
        for (Coord p : points) {
            float qx = p.x + pt.x - c.x;
            float qy = p.y + pt.y - c.y;
            newPoints.add(new Coord(qx, qy));
        }
        return newPoints;
    }

    public static float distanceAtBestAngle(List<Coord> points, Unistroke T, float a, float b, float threshold) {
        float x1 = phi * a + (1.0f - phi) * b;
        float f1 = distanceAtAngle(points, T, x1);
        float x2 = (1.0f - phi) * a + phi * b;
        float f2 = distanceAtAngle(points, T, x2);
        while (Math.abs(b - a) > threshold) {
            if (f1 < f2) {
                b = x2;
                x2 = x1;
                f2 = f1;
                x1 = phi * a + (1.0f - phi) * b;
                f1 = distanceAtAngle(points, T, x1);
            } else {
                a = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1.0f - phi) * a + phi * b;
                f2 = distanceAtAngle(points, T, x2);
            }
        }
        return f1 < f2 ? f1 : f2;
    }

    public static float distanceAtAngle(List<Coord> points, Unistroke T, float radians) {
        List<Coord> newPoints = rotateBy(points, radians);
        return Coord.pathDistance(newPoints, T.points);
    }
}
