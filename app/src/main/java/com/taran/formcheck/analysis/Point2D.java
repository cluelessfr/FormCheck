package com.taran.formcheck.analysis;

public class Point2D {
    private final double x;
    private final double y;

    public Point2D(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Both X and Y values must be finite");
        }

        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
