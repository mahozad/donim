package com.pleon.chopchop.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class AboutController {

    @FXML private Node root;
    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize() {
        // Make window movable
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            root.getScene().getWindow().setX(event.getScreenX() - xOffset);
            root.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });
    }

    public void onExitClick() {
        // TODO: Fade out the stage
        ((Stage) root.getScene().getWindow()).close();
    }
}
