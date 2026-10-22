
package gui;

import database.DBConnection;
import dao.salesDAO;
import model.sale;
import model.saleItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {

    private final int cashierId;


    private JComboBox<Medicine> cmbMedicine;
    private JTextField txtQty;

    private JTable cartTable;
    private DefaultTableModel cartModel;

    private JLabel lblStock;
    private JLabel lblTotal;

    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnClear;
    private JButton btnCheckout;
    private JButton btnRefresh;


    public POSPanel(int cashierId) {
        this.cashierId = cashierId;
        initComponents();
        loadMedicines();
    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15 ));

        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Pharmacy Point of Sale");

        lblTitle.setFont(new Font( "Arial",Font.BOLD,28));

        add(lblTitle, BorderLayout.NORTH);

        JPanel inputPanel =new JPanel(new GridBagLayout());

        inputPanel.setBackground(Color.WHITE);

        inputPanel.setBorder(BorderFactory.createTitledBorder( "Add Medicine"));

        GridBagConstraints gbc =new GridBagConstraints();

        gbc.insets =new Insets(5, 5, 5, 5);

        gbc.fill =GridBagConstraints.HORIZONTAL;

        // Medicine label
        gbc.gridx = 0;
        gbc.gridy = 0;

        inputPanel.add(new JLabel("Medicine:"),gbc);

        // Medicine combo box
        cmbMedicine =new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        inputPanel.add(cmbMedicine,gbc);

        // Quantity
        gbc.gridx = 2;
        gbc.weightx = 0;

        inputPanel.add(new JLabel("Quantity:"),gbc );

        txtQty =new JTextField(8);

        gbc.gridx = 3;

        inputPanel.add(txtQty,gbc);

        // Stock label
        lblStock =new JLabel("Stock: 0");

        gbc.gridx = 4;

        inputPanel.add(lblStock,gbc);

        // Add button
        btnAdd =new JButton("Add to Cart");
        gbc.gridx = 5;
        inputPanel.add(btnAdd,gbc);

        // Refresh button
        btnRefresh = new JButton("Refresh");

        gbc.gridx = 6;

        inputPanel.add(btnRefresh,gbc);

        add(
                inputPanel,
                BorderLayout.PAGE_START
        );

        // CART TABLE
        cartModel =
                new DefaultTableModel(new Object[]{
                                "Medicine ID",
                                "Medicine",
                                "Qty",
                                "Price",
                                "Subtotal"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        cartTable =new JTable(cartModel);

        cartTable.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(cartTable);

        scrollPane.setBorder(BorderFactory.createTitledBorder(
                        "Shopping Cart"));

        add(
                scrollPane,BorderLayout.CENTER
        );

        // BOTTOM PANEL
        JPanel bottomPanel =
                new JPanel(new BorderLayout());

        bottomPanel.setBackground(Color.WHITE);

        // Buttons
        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttonPanel.setBackground(Color.WHITE);

        btnRemove = new JButton("Remove Selected");

        btnClear = new JButton("Clear Cart");

        btnCheckout = new JButton("Checkout");

        buttonPanel.add(btnRemove);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnCheckout);

        bottomPanel.add(buttonPanel,BorderLayout.WEST);

        // Total
        lblTotal =new JLabel("Total: R 0.00");

        lblTotal.setFont(new Font( "Arial",Font.BOLD,22)
        );

        bottomPanel.add(lblTotal,BorderLayout.EAST
        );

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // EVENTS
          cmbMedicine.addActionListener(
                e -> updateStockLabel()
        );

        btnAdd.addActionListener(
                e -> addToCart()
        );

        btnRemove.addActionListener(
                e -> removeSelectedItem()
        );

        btnClear.addActionListener(
                e -> clearCart()
        );

        btnCheckout.addActionListener(
                e -> checkout()
        );

        btnRefresh.addActionListener(
                e -> loadMedicines()
        );
    }

    // LOAD MEDICINES FROM DATABASE

    private void loadMedicines() {

        cmbMedicine.removeAllItems();

        String sql =
                "SELECT medicine_id, name, price, quantity_in_stock " +
                "FROM medicines " +
                "WHERE quantity_in_stock > 0 " +
                "ORDER BY name";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        stmt.executeQuery()
        ) {

            while (rs.next()) {

                Medicine medicine =
                        new Medicine(
                                rs.getInt("medicine_id"),
                                rs.getString("name"),
                                rs.getDouble("price"),
                                rs.getInt("quantity_in_stock")
                        );

                cmbMedicine.addItem(medicine);
            }

            updateStockLabel();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load medicines.\n\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // UPDATE STOCK LABEL

    private void updateStockLabel() {

        Medicine medicine =
                (Medicine) cmbMedicine.getSelectedItem();

        if (medicine == null) {

            lblStock.setText("Stock: 0");

            return;
        }

        lblStock.setText(
                "Stock: " + medicine.stock
        );
    }

    // ADD TO CART
    private void addToCart() {

        Medicine medicine =
                (Medicine) cmbMedicine.getSelectedItem();

        if (medicine == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine.",
                    "No Medicine",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int qty;

        try {

            qty =
                    Integer.parseInt(
                            txtQty.getText().trim()
                    );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid quantity.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (qty <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quantity must be greater than zero.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Check quantity already in cart
        int existingQty =
                getQuantityInCart(
                        medicine.id
                );

        if (existingQty + qty > medicine.stock) {

            JOptionPane.showMessageDialog(
                    this,
                    "Not enough stock available.\n\n"
                    + "Available: " + medicine.stock
                    + "\nAlready in cart: " + existingQty,
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // If already in cart, increase quantity

        for (int i = 0;
             i < cartModel.getRowCount();
             i++) {

            int id =
                    Integer.parseInt(
                            cartModel
                                    .getValueAt(i, 0)
                                    .toString()
                    );

            if (id == medicine.id) {

                int currentQty =
                        Integer.parseInt(
                                cartModel
                                        .getValueAt(i, 2)
                                        .toString()
                        );

                int newQty =
                        currentQty + qty;

                double subtotal =
                        newQty * medicine.price;

                cartModel.setValueAt(
                        newQty,
                        i,
                        2
                );

                cartModel.setValueAt(
                        String.format(
                                "R %.2f",
                                subtotal
                        ),
                        i,
                        4
                );

                calculateTotal();

                txtQty.setText("");

                return;
            }
        }

        // Add new item
        double subtotal =
                qty * medicine.price;

        cartModel.addRow(
                new Object[]{
                        medicine.id,
                        medicine.name,
                        qty,
                        String.format(
                                "R %.2f",
                                medicine.price
                        ),
                        String.format(
                                "R %.2f",
                                subtotal
                        )
                }
        );

        calculateTotal();

        txtQty.setText("");
    }

    // GET QUANTITY ALREADY IN CART

    private int getQuantityInCart(
            int medicineId
    ) {

        for (int i = 0;
             i < cartModel.getRowCount();
             i++) {

            int id =
                    Integer.parseInt(
                            cartModel
                                    .getValueAt(i, 0)
                                    .toString()
                    );

            if (id == medicineId) {

                return Integer.parseInt(
                        cartModel
                                .getValueAt(i, 2)
                                .toString()
                );
            }
        }

        return 0;
    }
    // REMOVE ITEM

    private void removeSelectedItem() {

        int row =
                cartTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select an item first.",
                    "No Item Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        cartModel.removeRow(row);

        calculateTotal();
    }

    // CLEAR CART

    private void clearCart() {

        cartModel.setRowCount(0);

        calculateTotal();
    }

    // CALCULATE TOTAL

    private void calculateTotal() {

        double total = 0;

        for (int i = 0;
             i < cartModel.getRowCount();
             i++) {

            String value =
                    cartModel
                            .getValueAt(i, 4)
                            .toString();

            value =
                    value
                            .replace("R", "")
                            .trim();

            total +=
                    parseMoney(value).doubleValue();
        }

        lblTotal.setText(
                String.format(
                        "Total: R %.2f",
                        total
                )
        );
    }

    // GET TOTAL

    private double getTotal() {

        double total = 0;

        for (int i = 0;
             i < cartModel.getRowCount();
             i++) {

            String value =
                    cartModel
                            .getValueAt(i, 4)
                            .toString();

            value =
                    value
                            .replace("R", "")
                            .trim();

            total +=
                    parseMoney(value).doubleValue();
        }

        return total;
    }

    // CHECKOUT
 
    private void checkout() {

        if (cartModel.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "The cart is empty.",
                    "Checkout",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        double total =
                getTotal();

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        String.format(
                                "Complete sale?\n\n"
                                + "Total: R %.2f",
                                total
                        ),
                        "Confirm Sale",
                        JOptionPane.YES_NO_OPTION
                );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            List<saleItem> items = new ArrayList<>();
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int medicineId = Integer.parseInt(cartModel.getValueAt(i, 0).toString());
                String medicineName = cartModel.getValueAt(i, 1).toString();
                int quantity = Integer.parseInt(cartModel.getValueAt(i, 2).toString());
                BigDecimal price = parseMoney(cartModel.getValueAt(i, 3).toString());
                items.add(new saleItem(medicineId, medicineName, quantity, price));
            }

            sale completedSale = new salesDAO().createSale(cashierId, items);
            showBill(completedSale.getSaleId(), completedSale.getTotalAmount().doubleValue());

            // Clear cart
            cartModel.setRowCount(0);

            calculateTotal();

            // Reload current stock
            loadMedicines();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sale failed.\n\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        }
    }

    /** Parses the locally formatted Rand values shown in the cart (for example, R 267,00). */
    private BigDecimal parseMoney(String value) {
        String text = value.replace("R", "").trim();
        NumberFormat format = NumberFormat.getNumberInstance();
        ParsePosition position = new ParsePosition(0);
        Number parsed = format.parse(text, position);
        if (parsed == null || position.getIndex() != text.length()) {
            throw new NumberFormatException("Invalid amount: " + value);
        }
        return new BigDecimal(parsed.toString()).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private void showBill(int saleId, double total) {
        StringBuilder text = new StringBuilder();
        text.append("HEALTHFIRST PHARMACY\n")
                .append("Sale #: ").append(saleId).append("\n")
                .append("Date: ").append(new java.util.Date()).append("\n")
                .append("----------------------------------------\n")
                .append(String.format("%-20s %5s %10s %10s%n", "Medicine", "Qty", "Price", "Subtotal"));
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            text.append(String.format("%-20s %5s %10s %10s%n",
                    cartModel.getValueAt(i, 1), cartModel.getValueAt(i, 2),
                    cartModel.getValueAt(i, 3), cartModel.getValueAt(i, 4)));
        }
        text.append("----------------------------------------\n")
                .append(String.format("TOTAL: R %.2f%n", total))
                .append("Thank you for your purchase.\n");

        JTextArea bill = new JTextArea(text.toString(), 18, 50);
        bill.setEditable(false);
        bill.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JButton print = new JButton("Print");
        JButton save = new JButton("Save bill");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(save); actions.add(print);
        JPanel content = new JPanel(new BorderLayout(6, 6));
        content.add(new JScrollPane(bill), BorderLayout.CENTER); content.add(actions, BorderLayout.SOUTH);
        print.addActionListener(e -> {
            try { bill.print(); }
            catch (java.awt.print.PrinterException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Print bill", JOptionPane.ERROR_MESSAGE); }
        });
        save.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("PIMS-Sale-" + saleId + ".txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try { java.nio.file.Files.writeString(chooser.getSelectedFile().toPath(), bill.getText()); }
                catch (java.io.IOException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Save bill", JOptionPane.ERROR_MESSAGE); }
            }
        });
        JOptionPane.showMessageDialog(this, content, "Sale complete - Bill #" + saleId, JOptionPane.INFORMATION_MESSAGE);
    }

    // MEDICINE CLASS

    private static class Medicine {

        int id;
        String name;
        double price;
        int stock;

        Medicine(
                int id,
                String name,
                double price,
                int stock
        ) {

            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {

            return name
                    + " - R "
                    + String.format(
                            "%.2f",
                            price
                    );
        }
    }
}

