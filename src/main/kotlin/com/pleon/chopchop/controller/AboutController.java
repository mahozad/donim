package com.pleon.chopchop.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class AboutController {

    @FXML private Node root;

    public void onExitClick() {
        ((Stage) root.getScene().getWindow()).close();
    }
}
