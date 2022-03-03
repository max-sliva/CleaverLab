package Java;

import javax.swing.*;
import java.awt.*;

public class ToArduino {
    public static void main(String[] args) {
        createGUI();
    }

    private static void createGUI() {
        JFrame frame = new JFrame("Arduino devices updater");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setWest(frame);

        frame.setSize(400,300);
        frame.setVisible(true);
    }

    private static void setWest(JFrame frame) { //левая панель со списком портов
        JList listOfPorts = new JList();

        frame.add(listOfPorts, BorderLayout.WEST);
    }

}
