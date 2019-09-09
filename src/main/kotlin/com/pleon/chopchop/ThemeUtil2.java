package com.pleon.chopchop;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public class ThemeUtil2 {

    private enum Theme {
        DARK, LIGHT
    }

    private static Property<Theme> currentTheme = new SimpleObjectProperty<>(Theme.DARK);

    public static void onThemeChanged(InvalidationListener listener) {
        ThemeUtil2.currentTheme.addListener(listener);
    }

    public static void toggleTheme() {
        if (currentTheme.getValue() == Theme.DARK) {
            currentTheme.setValue(Theme.LIGHT);
        } else {
            currentTheme.setValue(Theme.DARK);
        }
    }

    public static void applyTheme(Node root) {
        if (currentTheme.getValue() == Theme.LIGHT) {
            root.getStyleClass().removeAll("dark");
        } else {
            root.getStyleClass().add("dark");
        }
    }
}
