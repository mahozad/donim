package com.pleon.chopchop.model;

import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.hsb;

public enum Type {

    WORK(25 * 60, hsb(0.0, 0.85, 1.0)),
    BREAK(2 * 60, hsb(145, 0.85, 1.0));

    Type(int length, Color baseColor) {
        this.length = length;
        this.baseColor = baseColor;
    }

    private int length;
    private Color baseColor;

    public int getLength() {
        return length;
    }

    public Color getBaseColor() {
        return baseColor;
    }
}
