import utility.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * From https://alvinalexander.com/java/edu/SplashScreen/node9.shtml
 * Splash screen class for implementation
 */

public class SplashScreen extends JWindow {

    private JProgressBar jProgressBar;

    public SplashScreen() { }

    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    public void showSplash() {

        JPanel content = (JPanel) getContentPane();
        content.setBackground(Color.white);

        jProgressBar = new JProgressBar(0,100);
        jProgressBar.setValue(0);
        jProgressBar.setStringPainted(true);

        // Set the window's bounds, centering the window
        int width = 650;
        int height = 200;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        // Build the splash screen
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("images/BWH_logo_rgb_pos.jpg")); // load the image to a imageIcon
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(590, 120,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  // transform it back
        JLabel label = new JLabel(imageIcon);
       // JLabel title = new JLabel("Team I's Kiosk");
        //title.setFont(new Font("Sans-Serif", Font.BOLD, 24));
        JLabel copyrt = new JLabel
                ("© Brigham & Women’s Hospital 2017", JLabel.CENTER);
        copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
       // content.add(title,BorderLayout.NORTH);
        content.add(jProgressBar, BorderLayout.PAGE_START);
        content.add(label, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);
        Color borderblue = new Color(0, 88, 159, 255);
        content.setBorder(BorderFactory.createLineBorder(borderblue, 10));

        // Display it
        setVisible(true);

    }

    public void showSplashAndExit() {

        showSplash();
        System.exit(0);

    }
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(int progress) {
            jProgressBar.setValue(progress);
    }

    public void Exit(){
        setVisible(false);
    }

}