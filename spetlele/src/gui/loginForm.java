
package gui;
import dao.UserDAO;
import javax.swing.*;
import java.awt.event.*;

public class loginForm extends JFrame {

    JTextField usertxt;
    JPasswordField passtxt;
    JButton loginBtn;

    public loginForm() {

        setTitle("PIMS Login");
        setSize(380,260);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel userlabel = new JLabel("Username");

        userlabel.setBounds(40,40,100,25);
        add(userlabel);

        usertxt = new JTextField();

        usertxt.setBounds(140,40,120,25);

        add(usertxt);

        JLabel passLabel = new JLabel("Password");

        passLabel.setBounds(40,80,100,25);

        add(passLabel);

        passtxt = new JPasswordField();

        passtxt.setBounds(140,80,120,25);

        add(passtxt);

        loginBtn = new JButton("Login");

        loginBtn.setBounds(110,130,100,30);

        add(loginBtn);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        setLocationRelativeTo(null);
    }

    private void login() {

        UserDAO dao = new UserDAO();
        String role;
        try {
            role = dao.login(usertxt.getText().trim(), String.valueOf(passtxt.getPassword()));
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database connection", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(role == null) {
            JOptionPane.showMessageDialog(this,"Invalid Login");
        } else if(role.equals("Admin")) {
            new adminDashboard();
            dispose();
        } else {
            new cashierDashboard(dao.getLastUserId());
            dispose();
        }
    }
}

