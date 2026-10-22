package gui;

import dao.SupplierDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class supplierPanel extends JPanel {
    private final SupplierDAO dao = new SupplierDAO();
    private final JTextField[] fields = {new JTextField(14), new JTextField(14), new JTextField(14), new JTextField(14), new JTextField(14)};
    private final String[] labels = {"Supplier", "Contact person", "Phone", "Email", "Address"};
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Supplier", "Contact", "Phone", "Email", "Address"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private int selectedId = -1;

    public supplierPanel() {
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(0, 4, 6, 6));
        for (int i = 0; i < fields.length; i++) { form.add(new JLabel(labels[i])); form.add(fields[i]); }
        JButton add = new JButton("Add"); JButton update = new JButton("Update"); JButton delete = new JButton("Delete"); JButton clear = new JButton("Clear");
        form.add(add); form.add(update); form.add(delete); form.add(clear);
        add(form, BorderLayout.NORTH); add(new JScrollPane(table), BorderLayout.CENTER);
        add.addActionListener(e -> save(false)); update.addActionListener(e -> save(true));
        delete.addActionListener(e -> delete()); clear.addActionListener(e -> clear());
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) selectRow(); });
        refresh();
    }

    private void save(boolean editing) {
        try {
            String name = fields[0].getText().trim();
            if (name.isEmpty() || fields[1].getText().trim().isEmpty() || fields[2].getText().trim().isEmpty()) throw new IllegalArgumentException("Supplier, contact person, and phone are required.");
            if (editing) {
                if (selectedId < 0) throw new IllegalArgumentException("Select a supplier first.");
                dao.updateSupplier(selectedId, name, fields[1].getText().trim(), fields[2].getText().trim(), fields[3].getText().trim(), fields[4].getText().trim());
            } else dao.addSupplier(name, fields[1].getText().trim(), fields[2].getText().trim(), fields[3].getText().trim(), fields[4].getText().trim());
            clear(); refresh();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Supplier", JOptionPane.ERROR_MESSAGE); }
    }

    private void selectRow() {
        int row = table.convertRowIndexToModel(table.getSelectedRow()); selectedId = (int) model.getValueAt(row, 0);
        for (int i = 0; i < fields.length; i++) fields[i].setText(String.valueOf(model.getValueAt(row, i + 1)));
    }
    private void delete() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a supplier first."); return; }
        try { dao.deleteSupplier(selectedId); clear(); refresh(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Supplier could not be deleted. Remove or reassign its medicines first.\n" + ex.getMessage(), "Supplier", JOptionPane.ERROR_MESSAGE); }
    }
    private void clear() { selectedId = -1; table.clearSelection(); for (JTextField field : fields) field.setText(""); }
    private void refresh() {
        model.setRowCount(0);
        try { for (Object[] row : dao.findAll()) model.addRow(row); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Database", JOptionPane.ERROR_MESSAGE); }
    }
}
