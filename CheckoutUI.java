
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class CheckoutUI extends JFrame {
    private Cart cart;
    private String username;

    public CheckoutUI(Cart cart, String username) {
        this.cart = cart;
        this.username = username;

        setTitle("Checkout - E-Commerce");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel to hold everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("Checkout", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table to show cart items
        String[] columnNames = {"Product Name", "Quantity", "Price (Rs)", "Total Price (Rs)"};
        Object[][] data = new Object[cart.getItems().size()][4];
        int row = 0;
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double totalPriceForItem = product.getPrice() * quantity;

            data[row][0] = product.getName();
            data[row][1] = quantity;
            data[row][2] = product.getPrice();
            data[row][3] = totalPriceForItem;

            row++;
        }

        JTable productTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        productTable.setFillsViewportHeight(true);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Panel for total and confirm button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));

        // Total label
        double totalAmount = cart.calculateTotal();
        JLabel totalLabel = new JLabel("Total Amount: Rs " + totalAmount, SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        // Confirm Order Button
        JButton confirmButton = new JButton("Confirm Order");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(new Color(102, 255, 178));
        confirmButton.setForeground(Color.DARK_GRAY);
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        confirmButton.addActionListener(e -> placeOrder());

        bottomPanel.add(confirmButton, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    private void placeOrder() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int userId = getUserId(conn, username);

            // Insert order into orders table
            String orderQuery = "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, cart.calculateTotal());
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                // Insert items into order_items table
                for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();

                    String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                    PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, product.getId());
                    itemStmt.setInt(3, quantity);
                    itemStmt.setDouble(4, product.getPrice() * quantity);
                    itemStmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(null, "Order placed successfully!");
            new ProductCatalogUI(username).setVisible(true);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getUserId(Connection conn, String username) throws Exception {
        String query = "SELECT user_id FROM users WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
        throw new Exception("User not found");
    }

    // public static void main(String[] args) {
    //     // Create a cart and user for testing
    //     Cart cart = new Cart();
    //     cart.addToCart(new Product(1, "Laptop", 50000, 10), 1);
    //     cart.addToCart(new Product(2, "Mouse", 500, 20), 2);

    //     new CheckoutUI(cart, "Monika").setVisible(true);
    // }
}
