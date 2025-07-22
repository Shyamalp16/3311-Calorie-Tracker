package main;
import javax.swing.*;
import java.awt.*;

import gui.SplashScreen;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
        });
    }
}


