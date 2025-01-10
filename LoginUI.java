

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginUI extends JFrame implements ActionListener{

    JLabel username, password;
    JTextField usr;
    JPasswordField pass;
    JButton login, cancel;

    LoginUI() {

        setTitle("Login - E-Commerce");
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

        usr = new JTextField();
        usr.setFont(new Font("Raleway",Font.PLAIN,19));
        usr.setBorder(BorderFactory.createEtchedBorder());
        usr.setBounds(450, 100, 200, 35);
        image.add(usr);

        // ***************************************************** JPasswordField

        pass = new JPasswordField();
        pass.setFont(new Font("Raleway",Font.PLAIN,25));
        pass.setBorder(BorderFactory.createEtchedBorder());
        pass.setBounds(450, 200, 200, 35);
        pass.setEchoChar('*');
        image.add(pass);

        // ***************************************************** JButton

        login = new JButton("Login");
        login.setFocusable(false);
        login.setBorder(BorderFactory.createEmptyBorder());
        login.setForeground(new Color(0, 153, 76));
        login.setBackground(new Color(102, 255, 178));
        login.setFont(new Font("Raleway",Font.BOLD,20));
        login.setBounds(250,350,180,35);
        login.addActionListener(this);
        image.add(login);

        cancel = new JButton("Register");
        cancel.setFocusable(false);
        cancel.setBorder(BorderFactory.createEmptyBorder());
        cancel.setForeground(new Color(255,255,255));
        cancel.setBackground(new Color(255, 102, 102));
        cancel.setFont(new Font("Raleway",Font.BOLD,20));
        cancel.setBounds(470,350,180,35);
        cancel.addActionListener(this);
        image.add(cancel);

        

        // **********************************************************

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String username = usr.getText();
            String password = pass.getText();
    
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Use BINARY to enforce case sensitivity for username and password
                String query = "SELECT * FROM users WHERE BINARY username = ? AND BINARY password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
    
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Login Successful");
                    new ProductCatalogUI(username).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Credentials");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    
        if (ae.getSource() == cancel) {
            setVisible(false);
            new RegisterUI().setVisible(true);
        }
    }
    

    public static void main(String[] args) {
        new LoginUI();
    }
}

