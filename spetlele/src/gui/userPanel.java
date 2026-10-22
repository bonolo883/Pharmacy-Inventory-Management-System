package gui;

import dao.UserDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class userPanel extends JPanel {
    private final UserDAO dao = new UserDAO();
    private final JTextField username = new JTextField(14);
    private final JPasswordField password = new JPasswordField(14);
    private final JTextField fullName = new JTextField(14);
    private final JComboBox<String> role = new JComboBox<>(new String[]{"Cashier", "Admin"});
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Username", "Role", "Full name"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private int selectedId = -1;

    public userPanel() {
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(0, 4, 6, 6));
        form.add(new JLabel("Username")); form.add(username); form.add(new JLabel("Password (leave blank to keep)")); form.add(password);
        form.add(new JLabel("Full name")); form.add(fullName); form.add(new JLabel("Role")); form.add(role);
        JButton add = new JButton("Create user"); JButton update = new JButton("Update selected"); JButton delete = new JButton("Delete selected"); JButton clear = new JButton("Clear");
        form.add(add); form.add(update); form.add(delete); form.add(clear);
        add(form, BorderLayout.NORTH); add(new JScrollPane(table), BorderLayout.CENTER);
        add.addActionListener(e -> save(false)); update.addActionListener(e -> save(true)); delete.addActionListener(e -> delete()); clear.addActionListener(e -> clear());
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) selectRow(); });
        refresh();
    }

    private void save(boolean editing) {
        try {
            String name = username.getText().trim(); String person = fullName.getText().trim(); String pass = new String(password.getPassword());
            if (name.isEmpty() || person.isEmpty() || (!editing && pass.isEmpty())) throw new IllegalArgumentException("Username, full name, and a password for new users are required.");
            if (editing) {
                if (selectedId < 0) throw new IllegalArgumentException("Select a user first.");
                dao.updateUser(selectedId, name, pass, (String) role.getSelectedItem(), person);
            } else dao.addUser(name, pass, (String) role.getSelectedItem(), person);
            clear(); refresh();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "User", JOptionPane.ERROR_MESSAGE); }
    }
    private void selectRow() {
        int row = table.convertRowIndexToModel(table.getSelectedRow()); selectedId = (int) model.getValueAt(row, 0);
        username.setText(String.valueOf(model.getValueAt(row, 1))); role.setSelectedItem(model.getValueAt(row, 2)); fullName.setText(String.valueOf(model.getValueAt(row, 3))); password.setText("");
    }
    private void delete() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        if ("Admin".equals(model.getValueAt(row, 2))) { JOptionPane.showMessageDialog(this, "Administrator accounts are protected. Only cashier accounts can be deleted."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this cashier account?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dao.deleteUser(selectedId); clear(); refresh(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "User could not be deleted because of linked sales.\n" + ex.getMessage(), "User", JOptionPane.ERROR_MESSAGE); }
    }
    private void clear() { selectedId = -1; table.clearSelection(); username.setText(""); password.setText(""); fullName.setText(""); role.setSelectedItem("Cashier"); }
    private void refresh() {
        model.setRowCount(0);
        try { for (Object[] row : dao.findAll()) model.addRow(row); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Database", JOptionPane.ERROR_MESSAGE); }
    }
}
