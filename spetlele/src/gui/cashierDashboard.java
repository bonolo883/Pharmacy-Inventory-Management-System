
package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class cashierDashboard extends JFrame{
    public cashierDashboard() {package gui;

import javax.swing.*;
import java.awt.BorderLayout;

public class cashierDashboard extends JFrame {
    public cashierDashboard(int cashierId) {
        setTitle("PIMS - Cashier Point of Sale");
        setSize(1050, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(new POSPanel(cashierId), BorderLayout.CENTER);
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> { dispose(); new loginForm().setVisible(true); });
        add(logout, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
    setTitle("Ca*hier Dashboard");
    setSize(800,500);
    add( new JLabel("Point of Sale Screen"));
    setLocationRelativeTo(null);
    setVisible(true);
    }
}
