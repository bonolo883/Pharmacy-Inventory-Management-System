
package gui;

import dao.MedicineDAO;
import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MedicinePanel extends JPanel {

    private JTextField txtName;
    private JTextField txtCategory;
    private JTextField txtPrice;
    private JTextField txtQty;
    private JTextField txtSearch;

    private JButton btnSave;
    private JButton btnUpdate;

    private JTable tblMedicine;
    private DefaultTableModel model;

    private int selectedMedicineId = -1;

    public MedicinePanel() {

        setLayout(null);

        // NAME
        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(50, 40, 100, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(150, 40, 180, 25);
        add(txtName);
        
        // CATEGORY
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setBounds(50, 80, 100, 25);
        add(lblCategory);

        txtCategory = new JTextField();
        txtCategory.setBounds(150, 80, 180, 25);
        add(txtCategory);

        // PRICE
        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setBounds(50, 120, 100, 25);
        add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(150, 120, 180, 25);
        add(txtPrice);

        // QUANTITY
        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setBounds(50, 160, 100, 25);
        add(lblQty);

        txtQty = new JTextField();
        txtQty.setBounds(150, 160, 180, 25);
        add(txtQty);

        // SAVE BUTTON
        btnSave = new JButton("Save");
        btnSave.setBounds(150, 200, 85, 30);
        add(btnSave);
        
        //delet btn
        JButton btnDelete =new JButton("Delete"); 
        btnDelete.setBounds( 420,220,100,30);
        add(btnDelete);
        
        // UPDATE BUTTON
        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(245, 200, 85, 30);
        add(btnUpdate);

        // SEARCH
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setBounds(400, 40, 70, 25);
        add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(460, 40, 220, 25);
        add(txtSearch);

        // TABLE
        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("Price");
        model.addColumn("Quantity");

        tblMedicine = new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(tblMedicine);

        scrollPane.setBounds(400,80,550,350);

        add(scrollPane);

        btnSave.addActionListener(e -> saveMedicine());

        btnUpdate.addActionListener(e -> updateMedicine());
        btnUpdate.addActionListener(e -> {
        MedicineDAO dao =
                    new MedicineDAO();

            dao.updateMedicine(
                    selectedMedicineId,
                    txtName.getText(),
                    txtCategory.getText(),
                    Double.parseDouble(
                            txtPrice.getText()),

                    Integer.parseInt(
                            txtQty.getText()));

            JOptionPane.showMessageDialog(
                    null,
                    "Medicine Updated");

            loadMedicines();
        });
        btnDelete.addActionListener(e -> {

            int option =
                    JOptionPane.showConfirmDialog( null, "Delete this medicine?","Confirm",
                            JOptionPane.YES_NO_OPTION);

            if(option ==JOptionPane.YES_OPTION) {
                MedicineDAO dao = new MedicineDAO();
                dao.deleteMedicine(selectedMedicineId);
                JOptionPane.showMessageDialog(null,"Medicine Deleted");
                loadMedicines();
            }
        });
        txtSearch.addActionListener(
                e -> searchMedicine()
        );

        txtSearch.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                searchMedicine();
                            }

                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                searchMedicine();
                            }

                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e) {
                                searchMedicine();
                            }
                        }
                );

        // TABLE CLICK
        tblMedicine.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        int row =
                                tblMedicine.getSelectedRow();

                        if (row >= 0) {

                            selectedMedicineId =
                                    Integer.parseInt(
                                            model.getValueAt(
                                                    row, 0
                                            ).toString()
                                    );

                            txtName.setText(
                                    model.getValueAt(
                                            row, 1
                                    ).toString()
                            );

                            txtCategory.setText(
                                    model.getValueAt(
                                            row, 2
                                    ).toString()
                            );

                            txtPrice.setText(
                                    model.getValueAt(
                                            row, 3
                                    ).toString()
                            );

                            txtQty.setText(
                                    model.getValueAt(
                                            row, 4
                                    ).toString()
                            );
                        }
                    }
                }
        );

        // Load medicines when panel opens
        loadMedicines();
    }

    // SAVE MEDICINE

    private void saveMedicine() {

        try {

            String medicineName =
                    txtName.getText().trim();

            String medicineCategory =
                    txtCategory.getText().trim();

            double medicinePrice =
                    Double.parseDouble(
                            txtPrice.getText().trim()
                    );

            int quantity =
                    Integer.parseInt(
                            txtQty.getText().trim()
                    );

            if (medicineName.isEmpty()
                    || medicineCategory.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter all information."
                );

                return;
            }

            MedicineDAO dao =
                    new MedicineDAO();

            dao.addMedicine(
                    medicineName,
                    medicineCategory,
                    medicinePrice,
                    quantity
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine saved successfully!"
            );

            clearFields();

            loadMedicines();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Price and quantity must be numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // UPDATE MEDICINE
    private void updateMedicine() {

        if (selectedMedicineId == -1) {

            JOptionPane.showMessageDialog(this,"Please select a medicine first.");

            return;
        }

        try {

            String sql =
                    "UPDATE medicines SET medicineName = ?, category = ?, unitPrice = ?, quantityInStock = ? WHERE medicineID = ?";
            Connection con =DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1,txtName.getText().trim() );

            ps.setString(2,txtCategory.getText().trim());

            ps.setDouble(3,Double.parseDouble(txtPrice.getText().trim()));

            ps.setInt(4, Integer.parseInt(txtQty.getText().trim() ) );

            ps.setInt(5, selectedMedicineId);

            ps.executeUpdate();

            ps.close();
            con.close();

            JOptionPane.showMessageDialog(this,"Medicine updated successfully!");

            clearFields();

            loadMedicines();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog( this,"Price and quantity must be numbers.",
                    "Invalid Input",JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {

            JOptionPane.showMessageDialog( this, "Error updating medicine: "
                    + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // LOAD MEDICINES

    private void loadMedicines() {
        searchMedicine();
    }

    // SEARCH MEDICINES

    private void searchMedicine() {

        model.setRowCount(0);

        String search =txtSearch == null ? "": txtSearch.getText().trim();

        String sql =
                "SELECT medicineID, medicineName, "
                + "category, unitPrice, quantityInStock "
                + "FROM medicines "
                + "WHERE medicineName LIKE ? "
                + "ORDER BY medicineName";

        try {

            Connection con =DBConnection.getConnection();

            PreparedStatement ps =con.prepareStatement(sql);

            ps.setString( 1, "%" + search + "%" );

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                model.addRow(
                        new Object[]{
                                rs.getInt("medicineID"),

                                rs.getString("medicineName" ),

                                rs.getString("category"),

                                rs.getDouble( "unitPrice"),

                                rs.getInt("quantityInStock")
                        }
                );
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this, "Could not load medicines:\n"+ e.getMessage(),
                    "Database Error",JOptionPane.ERROR_MESSAGE );
        }
    }

    // CLEAR FIELDs
    private void clearFields() {

        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtQty.setText("");

        selectedMedicineId = -1;

        tblMedicine.clearSelection();
    }
}
