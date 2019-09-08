package com.pleon.chopchop.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.IOException;

public class SplashController {

    @FXML
    private Node brand;
    @FXML
    private Node splashRoot;

    // @FXML // required if method is not public
    public void initialize() throws IOException {
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(brand);
        rotate.setAxis(Rotate.Z_AXIS);
        rotate.setByAngle(360);
        rotate.setDuration(Duration.millis(2000));
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        rotate.setDelay(Duration.millis(1500));
        rotate.play();
        fadeOut(splashRoot);
    }

    private void fadeOut(Node root) throws IOException {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2),
                new EventHandler<>() {

                    float opacity = 1f;

                    @Override
                    public void handle(ActionEvent event) {
                        opacity = Math.max(opacity - 0.01f, 0);
                        root.setOpacity(opacity);
                    }
                }
        ));
        timeline.setCycleCount(100);
        timeline.setDelay(Duration.seconds(2));
        Parent mainSceneRoot = FXMLLoader.load(getClass()
                .getResource("/fxml/scene-main.fxml"));
        timeline.setOnFinished(event1 -> brand.getScene().setRoot(mainSceneRoot));
        timeline.play();
    }
}
