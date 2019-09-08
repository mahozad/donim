package com.pleon.chopchop.controller;

import com.pleon.chopchop.CircularProgressBar;
import com.pleon.chopchop.model.Type;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;

import static com.pleon.chopchop.ImageUtil.getImage;

public class MainController {

    //@formatter:off
    private static final long MILLIS_PER_FRAME = 5;
    private boolean shouldMinimizeToTray = true;
    @FXML private CircularProgressBar progressBar;
    @FXML private Button restart;
    @FXML private Button skip;
    @FXML private Pane root;
    //@formatter:on

    private Type type = Type.WORK;
    private StringProperty remainingTimeString =
            new SimpleStringProperty(String.format("%02d:%02d",
                    type.getLength() / 60, type.getLength() % 60));
    private StringProperty pauseString = new SimpleStringProperty("Start");
    private int remainingTime = type.getLength();
    private Timeline timeline = new Timeline();
    private float DELTA = -0.04f;
    private boolean paused = true;
    private Timeline trayAnimation;

    // @FXML // required if method is not public
    public void initialize() {

        java.awt.Image[] trayImages = new java.awt.Image[53];
        for (int i = 0; i < 53; i++) {
            String path = String.format("/tray/%d.png", i + 1);
            trayImages[i] = Toolkit.getDefaultToolkit().createImage(getImage(path));
        }

        trayAnimation = new Timeline();
        trayAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50),
                new EventHandler<>() {
                    int firstFrameDelay = 0;
                    int i = 0;

                    @Override
                    public void handle(ActionEvent event) {
                        TrayIcon trayIcon = SystemTray.getSystemTray().getTrayIcons()[0];
                        trayIcon.setImage(trayImages[i]);
                        if (i == 0 && firstFrameDelay < 100) {
                            firstFrameDelay++;
                            if (firstFrameDelay == 100) {
                                firstFrameDelay = 0;
                                i++;
                            }
                        } else {
                            i = (i + 1) % trayImages.length;
                        }
                    }
                }));
        trayAnimation.setCycleCount(Timeline.INDEFINITE);

    }

    private void startTimer() {
        remainingTime = type.getLength();
        timeline.setCycleCount(remainingTime);
        timeline.play();
    }

    public String getRemainingTimeString() {
        return remainingTimeString.get();
    }

    public StringProperty remainingTimeStringProperty() {
        return remainingTimeString;
    }

    public void setRemainingTimeString(String remainingTimeString) {
        this.remainingTimeString.set(remainingTimeString);
    }

    public String getPauseString() {
        return pauseString.get();
    }

    public StringProperty pauseStringProperty() {
        return pauseString;
    }

    public void setPauseString(String pauseString) {
        this.pauseString.set(pauseString);
    }

    public void onExitClick() {
        /*if (shouldMinimizeToTray) {
            ((Stage) root.getScene().getWindow()).hide();
        } else {
            fadeOut((Stage) root.getScene().getWindow(), event -> System.exit(0));
        }*/

        fadeOut((Stage) root.getScene().getWindow(), event -> System.exit(0));
    }

    public void minimizeApp() {
        /*Stage.getWindows().filtered(Window::isShowing).forEach(window ->
                ((Stage) window).setIconified(true)
        );*/

        /*fadeOut((Stage) root.getScene().getWindow(), event -> {
            // make it opaque again, so it'll reappear properly if they click the taskbar
            ((Stage) root.getScene().getWindow()).setOpacity(1f);
            ((Stage) root.getScene().getWindow()).setIconified(true);
        });*/

        fadeOut((Stage) root.getScene().getWindow(), event -> {
            // make it opaque again, so it'll reappear properly if they click the taskbar
            ((Stage) root.getScene().getWindow()).setOpacity(1f);
            ((Stage) root.getScene().getWindow()).hide();
        });
    }

    private void fadeOut(Stage frame, EventHandler<ActionEvent> onFinished) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(MILLIS_PER_FRAME),
                new EventHandler<>() {

                    float opacity = 1f;

                    @Override
                    public void handle(ActionEvent event) {
                        opacity = Math.max(opacity + DELTA, 0);
                        frame.setOpacity(opacity);
                    }
                }
        ));
        timeline.setCycleCount((int) (1 / -DELTA));
        timeline.setOnFinished(onFinished);
        timeline.play();
    }

    public void restart() {
        startTimer();
    }

    public void pauseResume() {
        if (timeline.getKeyFrames().isEmpty()) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
                setRemainingTimeString(String.format("%02d:%02d",
                        remainingTime / 60, remainingTime % 60));
                progressBar.tick((double) remainingTime / type.getLength(), type);
                remainingTime--;
            }));

            timeline.setOnFinished(event -> {
                type = (type == Type.WORK) ? Type.BREAK : Type.WORK;
                startTimer();
            });

            restart.setDisable(false);
            skip.setDisable(false);
            startTimer();
        }

        if (paused) {
            timeline.play();
            trayAnimation.play();
            setPauseString("Pause");
        } else {
            timeline.pause();
            trayAnimation.stop();
            setPauseString("Resume");
        }

        paused = !paused;
    }

    public void skip() {
        setPauseString("Pause"); // in case timer was paused while the skip pressed
        type = (type == Type.WORK) ? Type.BREAK : Type.WORK;
        timeline.stop();
        startTimer();
    }

    public void changeTheme() {
        if (root.getStyleClass().contains("dark")) {
            root.getStyleClass().remove("dark");
        } else {
            root.getStyleClass().add("dark");
        }
    }
}
