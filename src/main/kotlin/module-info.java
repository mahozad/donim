module donim.application {
    requires java.base;
    requires java.desktop;
    requires kotlin.stdlib;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;

    requires filters; // The com.jhlabs:filters library

    opens com.pleon.donim;
    opens com.pleon.donim.controller;
    exports com.pleon.donim;
    exports com.pleon.donim.node;
}
