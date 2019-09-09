package com.pleon.chopchop.model;

import javafx.scene.paint.Color;

import java.awt.TrayIcon.MessageType;

import static javafx.scene.paint.Color.hsb;

public enum Type {

    WORK(25 * 60,
            "You are free to use the system now",
            hsb(0.0, 0.85, 1.0),
            MessageType.INFO),
    BREAK(5 * 60,
            "Please take a break and return later",
            hsb(145, 0.85, 1.0),
            MessageType.WARNING);

    Type(int length, String message, Color baseColor, MessageType messageType) {
        this.length = length;
        this.message = message;
        this.baseColor = baseColor;
        this.messageType = messageType;
    }

    private int length;
    private String message;
    private Color baseColor;
    private MessageType messageType;

    public int getLength() {
        return length;
    }

    public String getMessage() {
        return message;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
