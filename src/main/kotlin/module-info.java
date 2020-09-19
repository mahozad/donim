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

    exports com.pleon.donim to javafx.graphics;
    opens com.pleon.donim.node to javafx.fxml;
    opens com.pleon.donim.controller to javafx.fxml;
}
