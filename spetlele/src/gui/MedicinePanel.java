package gui;

import dao.MedicineDAO;
import dao.SupplierDAO;
import database.DBConnection;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class MedicinePanel extends JPanel {
    private final JTextField name = new JTextField(16);
    private final JTextField category = new JTextField(16);
    private final JTextField company = new JTextField(16);
    private final JTextField type = new JTextField(16);
    private final JTextField price = new JTextField(16);
    private final JTextField quantity = new JTextField(16);
    private final JTextField reorder = new JTextField("10", 16);
    private final JTextField expiry = new JTextField(16);
    private final JTextField search = new JTextField(20);
    private final JComboBox<SupplierChoice> supplier = new JComboBox<>();
    private final String[] columns = {"ID", "Name", "Category", "Company", "Type", "Price", "Stock", "Reorder", "Expiry", "Supplier ID"};
    private final DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private int selectedId = -1;

    public MedicinePanel() {
        setLayout(new BorderLayout(8, 8));
        JPanel top = new JPanel(new BorderLayout(6, 6));
        JPanel form = new JPanel(new GridLayout(0, 4, 6, 6));
        addField(form, "Medicine name", name); addField(form, "Category", category);
        addField(form, "Company", company); addField(form, "Type", type);
        addField(form, "Unit price", price); addField(form, "Quantity in stock", quantity);
        addField(form, "Reorder level", reorder); addField(form, "Expiry (YYYY-MM-DD)", expiry);
        addField(form, "Supplier", supplier);
        JButton add = new JButton("Add medicine"); JButton update = new JButton("Update selected");
        JButton delete = new JButton("Delete selected"); JButton clear = new JButton("Clear");
        form.add(add); form.add(update); form.add(delete); form.add(clear);
        top.add(form, BorderLayout.CENTER);
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT)); searchRow.add(new JLabel("Search medicine:")); searchRow.add(search);
        top.add(searchRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH); add(new JScrollPane(table), BorderLayout.CENTER);
        add.addActionListener(e -> save(false)); update.addActionListener(e -> save(true));
        delete.addActionListener(e -> delete()); clear.addActionListener(e -> clear());
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) selectRow(); });
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        loadSuppliers(); refresh();
    }

    private void addField(JPanel form, String label, Component field) { form.add(new JLabel(label)); form.add(field); }

    private void loadSuppliers() {
        supplier.removeAllItems(); supplier.addItem(new SupplierChoice(null, "Unassigned"));
        try {
            List<Object[]> rows = new SupplierDAO().findAll();
            for (Object[] row : rows) supplier.addItem(new SupplierChoice((Integer) row[0], String.valueOf(row[1])));
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Could not load suppliers: " + ex.getMessage(), "Database", JOptionPane.ERROR_MESSAGE); }
    }

    private void save(boolean editing) {
        try {
            String medicineName = name.getText().trim(); String medicineCategory = category.getText().trim();
            if (medicineName.isEmpty() || medicineCategory.isEmpty()) throw new IllegalArgumentException("Medicine name and category are required.");
            double unitPrice = Double.parseDouble(price.getText().trim());
            int stock = Integer.parseInt(quantity.getText().trim()); int reorderLevel = Integer.parseInt(reorder.getText().trim());
            if (unitPrice < 0 || stock < 0 || reorderLevel < 0) throw new IllegalArgumentException("Price, stock, and reorder level cannot be negative.");
            java.sql.Date expiryDate = expiry.getText().trim().isEmpty() ? null : java.sql.Date.valueOf(expiry.getText().trim());
            SupplierChoice selectedSupplier = (SupplierChoice) supplier.getSelectedItem();
            Integer supplierId = selectedSupplier == null ? null : selectedSupplier.id;
            MedicineDAO dao = new MedicineDAO();
            if (editing) {
                if (selectedId < 0) throw new IllegalArgumentException("Select a medicine first.");
                dao.updateMedicine(selectedId, medicineName, medicineCategory, blankToNull(company.getText()), blankToNull(type.getText()), unitPrice, stock, reorderLevel, expiryDate, supplierId);
            } else dao.addMedicine(medicineName, medicineCategory, blankToNull(company.getText()), blankToNull(type.getText()), unitPrice, stock, reorderLevel, expiryDate, supplierId);
            clear(); refresh();
        } catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this, "Check the fields. Expiry must use YYYY-MM-DD.\n" + ex.getMessage(), "Invalid medicine", JOptionPane.ERROR_MESSAGE); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not save medicine", JOptionPane.ERROR_MESSAGE); }
    }

    private String blankToNull(String value) { String trimmed = value.trim(); return trimmed.isEmpty() ? null : trimmed; }
    private String textValue(Object value) { return value == null ? "" : value.toString(); }

    private void selectRow() {
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        selectedId = ((Number) model.getValueAt(row, 0)).intValue();
        name.setText(String.valueOf(model.getValueAt(row, 1))); category.setText(String.valueOf(model.getValueAt(row, 2)));
        company.setText(textValue(model.getValueAt(row, 3))); type.setText(textValue(model.getValueAt(row, 4)));
        price.setText(String.valueOf(model.getValueAt(row, 5))); quantity.setText(String.valueOf(model.getValueAt(row, 6)));
        reorder.setText(String.valueOf(model.getValueAt(row, 7))); expiry.setText(textValue(model.getValueAt(row, 8)));
        Object supplierId = model.getValueAt(row, 9);
        for (int i = 0; i < supplier.getItemCount(); i++) {
            SupplierChoice choice = supplier.getItemAt(i);
            if (supplierId == null ? choice.id == null : choice.id != null && choice.id.equals(((Number) supplierId).intValue())) { supplier.setSelectedIndex(i); break; }
        }
    }

    private void delete() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a medicine first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this medicine?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { new MedicineDAO().deleteMedicine(selectedId); clear(); refresh(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "A medicine already included in a sale cannot be deleted.\n" + ex.getMessage(), "Delete medicine", JOptionPane.ERROR_MESSAGE); }
    }

    private void clear() {
        selectedId = -1; table.clearSelection(); name.setText(""); category.setText(""); company.setText(""); type.setText("");
        price.setText(""); quantity.setText(""); reorder.setText("10"); expiry.setText(""); supplier.setSelectedIndex(0);
    }

    private void refresh() {
        model.setRowCount(0);
        String sql = "SELECT medicine_id, name, category, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id FROM medicines WHERE name LIKE ? ORDER BY name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + search.getText().trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt("medicine_id"), rs.getString("name"), rs.getString("category"), rs.getString("company"), rs.getString("medicine_type"), rs.getBigDecimal("price"), rs.getInt("quantity_in_stock"), rs.getInt("reorder_level"), rs.getDate("expiry_date"), (Object) rs.getObject("supplier_id")});
            }
        } catch (Exception ex) { if (isShowing()) JOptionPane.showMessageDialog(this, "Could not load medicines: " + ex.getMessage(), "Database", JOptionPane.ERROR_MESSAGE); }
    }

    private static class SupplierChoice {
        private final Integer id; private final String label;
        SupplierChoice(Integer id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}
