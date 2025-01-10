

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterUI extends JFrame implements ActionListener{

    JLabel username, password;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton login, cancel;

    RegisterUI() {

        setTitle("Register - E-Commerce");
        setLayout(null);
        setLocationRelativeTo(null);
        setBounds(280, 180, 1000, 500);
        setMaximizedBounds(getBounds());
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // ***************************************************** ImageIcon

        ImageIcon i = new ImageIcon(ClassLoader.getSystemResource("images\\loginbg.jpg"));
        Image i2 = i.getImage().getScaledInstance(1000, 500, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0,0, 1000,500);
        add(image);

        // ***************************************************** JLabel

        username = new JLabel("Username");
        username.setBounds(250, 100, 150, 30);
        username.setFont(new Font("Raleway", Font.BOLD, 20));
        image.add(username);

        password = new JLabel("Password");
        password.setBounds(250, 200, 150, 30);
        password.setFont(new Font("Raleway", Font.BOLD, 20));
        image.add(password);

        // ***************************************************** JTextField

        usernameField = new JTextField();
        usernameField.setFont(new Font("Raleway",Font.PLAIN,19));
        usernameField.setBorder(BorderFactory.createEtchedBorder());
        usernameField.setBounds(450, 100, 200, 35);
        image.add(usernameField);

        // ***************************************************** JPasswordField

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Raleway",Font.PLAIN,25));
        passwordField.setBorder(BorderFactory.createEtchedBorder());
        passwordField.setBounds(450, 200, 200, 35);
        passwordField.setEchoChar('*');
        image.add(passwordField);

        // ***************************************************** JButton

        cancel = new JButton("Register");
        cancel.setFocusable(false);
        cancel.setBorder(BorderFactory.createEmptyBorder());
        cancel.setForeground(new Color(255,255,255));
        cancel.setBackground(new Color(255, 102, 102));
        cancel.setFont(new Font("Raleway",Font.BOLD,20));
        cancel.setBounds(390,310,180,35);
        cancel.addActionListener(this);
        image.add(cancel);

        

        // **********************************************************

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent ae){

        
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registration Successful");
            new LoginUI().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }

        if(ae.getSource() == cancel){
            setVisible(false);
            new LoginUI().setVisible(true);
        }
    }

    public static void main(String[] args) {
        new RegisterUI();
    }
}
