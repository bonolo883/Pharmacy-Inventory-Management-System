package gui;

import database.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class reportsPanel extends JPanel {
    private final JTabbedPane reports = new JTabbedPane();
    private final String[] sql = {
        "SELECT s.sale_id AS 'Sale #', s.sale_date AS 'Date', u.full_name AS Cashier, s.total_amount AS Total FROM sales s JOIN users u ON u.user_id=s.cashier_id ORDER BY s.sale_date DESC",
        "SELECT m.name AS Medicine, SUM(si.quantity_sold) AS 'Units sold', SUM(si.subtotal) AS Revenue FROM sale_items si JOIN medicines m ON m.medicine_id=si.medicine_id GROUP BY m.medicine_id, m.name ORDER BY Revenue DESC",
        "SELECT medicine_id AS ID, name AS Medicine, quantity_in_stock AS 'In stock', reorder_level AS 'Reorder level' FROM medicines WHERE quantity_in_stock <= reorder_level ORDER BY quantity_in_stock, name",
        "SELECT medicine_id AS ID, name AS Medicine, expiry_date AS 'Expiry date', quantity_in_stock AS 'In stock' FROM medicines WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH) ORDER BY expiry_date"
    };
    private final String[] titles = {"Sales", "Item-wise", "Low stock", "Expiry (next month)"};

    public reportsPanel() {
        super(new BorderLayout());
        for (int i = 0; i < titles.length; i++) reports.addTab(titles[i], makeReportTable());
        JButton refresh = new JButton("Refresh reports");
        refresh.addActionListener(e -> loadCurrent());
        add(reports, BorderLayout.CENTER); add(refresh, BorderLayout.SOUTH);
        reports.addChangeListener(e -> loadCurrent());
        loadCurrent();
    }

    private JScrollPane makeReportTable() {
        JTable table = new JTable(new DefaultTableModel());
        table.setAutoCreateRowSorter(true);
        return new JScrollPane(table);
    }

    private void loadCurrent() {
        int index = reports.getSelectedIndex();
        if (index < 0) return;
        JTable table = (JTable) ((JScrollPane) reports.getComponentAt(index)).getViewport().getView();
        DefaultTableModel model = new DefaultTableModel();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql[index]); ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int c = 1; c <= meta.getColumnCount(); c++) model.addColumn(meta.getColumnLabel(c));
            while (rs.next()) {
                Object[] row = new Object[meta.getColumnCount()];
                for (int c = 1; c <= row.length; c++) row[c - 1] = rs.getObject(c);
                model.addRow(row);
            }
            table.setModel(model);
        } catch (Exception ex) {
            model.setColumnIdentifiers(new Object[]{"Report unavailable"}); model.addRow(new Object[]{ex.getMessage()}); table.setModel(model);
        }
    }
}
