package gui;

import javax.swing.*;
import java.awt.BorderLayout;

public class adminDashboard extends JFrame {
    public adminDashboard() {
        setTitle("PIMS - Administrator Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Medicines", new MedicinePanel());
        tabs.addTab("Suppliers", new supplierPanel());
        tabs.addTab("Users", new userPanel());
        tabs.addTab("Reports", new reportsPanel());
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> { dispose(); new loginForm().setVisible(true); });
        add(tabs, BorderLayout.CENTER); add(logout, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
