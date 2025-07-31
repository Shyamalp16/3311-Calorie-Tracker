package main;
import javax.swing.*;
import View.Main_Application_GUI;
import java.awt.*;

public class Application_Singleton_initiator {
    public static void main(String[] args) {
    	Main_Application_GUI instance = Main_Application_GUI.getInstance();
        instance.setVisible(true);
        };
    }



	