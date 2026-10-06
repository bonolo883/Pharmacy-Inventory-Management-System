
package gui;

import dao.MedicineDAO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MedicinePanel extends JPanel {

    JTextField name;
    JTextField category;
    JTextField price;
    JTextField Qty;
    JButton savebtn;

    public MedicinePanel() {

        setLayout(null);

        // Name
        JLabel l1 = new JLabel("Name");
        l1.setBounds(50, 50, 100, 25);
        add(l1);

        name = new JTextField();
        name.setBounds(180, 50, 150, 25);
        add(name);

        // Category
        JLabel l2 = new JLabel("Category");
        l2.setBounds(50, 90, 100, 25);
        add(l2);

        category = new JTextField();
        category.setBounds(180, 90, 150, 25);
        add(category);

        // Price
        JLabel l3 = new JLabel("Price");
        l3.setBounds(50, 130, 100, 25);
        add(l3);

        price = new JTextField();
        price.setBounds(180, 130, 150, 25);
        add(price);

        // Quantity
        JLabel l4 = new JLabel("Quantity");
        l4.setBounds(50, 170, 100, 25);
        add(l4);

        Qty = new JTextField();
        Qty.setBounds(180, 170, 150, 25);
        add(Qty);

        // Save button
        savebtn = new JButton("Save");
        savebtn.setBounds(180, 220, 100, 30);
        add(savebtn);

        // Save medicine
        savebtn.addActionListener(e -> {
            try {
                MedicineDAO dao = new MedicineDAO();
                dao.addMedicine(name.getText(),category.getText(),
                        Double.parseDouble(price.getText()),
                        Integer.parseInt(Qty.getText())
                );

                JOptionPane.showMessageDialog(
                        this,"Medicine Saved Successfully!"
                );

                // Clear fields
                name.setText("");
                category.setText("");
                price.setText("");
                Qty.setText("");

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,"Please enter a valid price and quantity.",
                         "Invalid Input", JOptionPane.ERROR_MESSAGE
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this, "Error saving medicine: " + ex.getMessage(), 
                        "Database Error",JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });
    }
}

