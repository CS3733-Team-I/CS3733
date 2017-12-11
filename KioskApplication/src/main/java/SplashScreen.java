import utility.ResourceManager;

import javax.swing.*;
import java.awt.*;

/**
 * From https://alvinalexander.com/java/edu/SplashScreen/node9.shtml
 * Splash screen class for implementation
 */

public class SplashScreen extends JWindow {

    private int duration;

    public SplashScreen(int d) {
        duration = d;
    }

    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    public void showSplash(boolean visablility) {

        JPanel content = (JPanel) getContentPane();
        content.setBackground(Color.white);

        // Set the window's bounds, centering the window
        int width = 200;
        int height = 200;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        // Build the splash screen
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("images/BWH_Logo.png")); // load the image to a imageIcon
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  // transform it back
        JLabel label = new JLabel(imageIcon);
       // JLabel title = new JLabel("Team I's Kiosk");
        //title.setFont(new Font("Sans-Serif", Font.BOLD, 24));
        JLabel copyrt = new JLabel
                ("© Brigham & Women’s Hospital 2017", JLabel.CENTER);
        copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
       // content.add(title,BorderLayout.NORTH);
        content.add(label, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);
        //Color oraRed = new Color(156, 20, 20, 255);
        //content.setBorder(BorderFactory.createLineBorder(oraRed, 10));

        // Display it
        setVisible(visablility);

        // Wait a little while, maybe while loading resources
        /*try {
            Thread.sleep(duration);
        } catch (Exception e) {
        }*/
        //setVisible(false);

    }

    public void showSplashAndExit() {

        showSplash(true);
        System.exit(0);

    }

    public void Exit(){
        this.showSplash(false);
    }

}