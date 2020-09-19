module donim.application {
    requires java.base;
    requires java.desktop;
    requires kotlin.stdlib;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;

    // The com.jhlabs:filters library
    requires filters;

    exports com.pleon.donim;
    exports com.pleon.donim.node;
    opens com.pleon.donim.controller;
}
