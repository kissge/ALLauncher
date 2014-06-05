package com.yoekido.allauncher;

import java.util.List;

public class OneDollarRecognizer {

    static final int numberOfPoints = 64;
    static final double squareSize = 250.0;
    static final Coord origin = new Coord(0.0f, 0.0f);
    static final float angleRange = 0.785398163f;
    static final float anglePrecision = 0.034906585f;
    public double score;
    private List<Unistroke> dictionary;

    OneDollarRecognizer(List<Unistroke> dictionary) {
        this.dictionary = dictionary;
    }

    public Unistroke recognize(List<Coord> points) {
        List<Coord> points2 = Unistroke.normalize(points);

        float b = Float.MAX_VALUE;
        Unistroke u = null;

        for (Unistroke us : dictionary) {
            float d = Unistroke.distanceAtBestAngle(points2, us, -angleRange, angleRange, anglePrecision);
            if (d < b) {
                b = d;
                u = us;
            }
        }

        if (u != null) {
            score = 1.0 - Math.sqrt(2.0) * (double) b / squareSize;
        } else {
            score = Float.NEGATIVE_INFINITY;
        }

        return u;
    }
}
