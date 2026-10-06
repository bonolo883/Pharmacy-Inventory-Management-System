
package gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class adminDashboard extends JFrame{
   public adminDashboard() {
    setTitle("Admin Dashboard");
    setSize(900,600);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    JTabbedPane tabs = new JTabbedPane();
    tabs.add("Medicines",new medicinePanel());
    
    add(tabs);
  
    setLocationRelativeTo(null);
    setVisible(true);
    }
}
