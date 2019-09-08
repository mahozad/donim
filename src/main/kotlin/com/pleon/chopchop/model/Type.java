package com.pleon.chopchop.model;

import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.hsb;

public enum Type {

    WORK(35 * 60,
            "You are free to use the system now",
            hsb(0.0, 0.85, 1.0)),
    BREAK(5 * 60, "Please take a break and return later",
            hsb(145, 0.85, 1.0));

    Type(int length, String message, Color baseColor) {
        this.length = length;
        this.message = message;
        this.baseColor = baseColor;
    }

    private int length;
    private String message;
    private Color baseColor;

    public int getLength() {
        return length;
    }

    public String getMessage() {
        return message;
    }

    public Color getBaseColor() {
        return baseColor;
    }
}
