
package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class cashierDashboard extends JFrame{
    public cashierDashboard() {
    setTitle("Ca*hier Dashboard");
    setSize(800,500);
    add( new JLabel("Point of Sale Screen"));
    setLocationRelativeTo(null);
    setVisible(true);
    }
}
