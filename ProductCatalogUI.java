

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductCatalogUI extends JFrame {
    private Cart cart = new Cart();
    private String username;

    public ProductCatalogUI(String username) {
        this.username = username;

        // Set up frame
        setTitle("Product Catalog - E-Commerce");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel for products
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 20, 20)); // Grid layout with padding
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fetch products
        List<Product> products = getAllProducts();
        for (Product product : products) {
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new BorderLayout());
            productPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
            productPanel.setBackground(Color.WHITE);

            // Product Name
            JLabel nameLabel = new JLabel(product.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Product Price
            JLabel priceLabel = new JLabel("Price: Rs " + product.getPrice(), SwingConstants.CENTER);
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            priceLabel.setForeground(Color.DARK_GRAY);
            priceLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Product Description (placeholder)
            JLabel descriptionLabel = new JLabel("<html><center>Amazing product you’ll love!</center></html>", SwingConstants.CENTER);
            descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Add to Cart Button
            JButton addToCartButton = new JButton("Add to Cart");
            addToCartButton.setFont(new Font("Arial", Font.BOLD, 14));
            addToCartButton.setBackground(new Color(102, 255, 178));
            addToCartButton.setForeground(Color.DARK_GRAY);
            addToCartButton.setFocusPainted(false);
            addToCartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Hover effect
            addToCartButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    addToCartButton.setBackground(new Color(51, 204, 102));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    addToCartButton.setBackground(new Color(102, 255, 178));
                }
            });

            addToCartButton.addActionListener(e -> {
                cart.addToCart(product, 1);
                JOptionPane.showMessageDialog(null, product.getName() + " added to cart!");
            });

            // Add components to product panel
            productPanel.add(nameLabel, BorderLayout.NORTH);
            productPanel.add(descriptionLabel, BorderLayout.CENTER);
            productPanel.add(priceLabel, BorderLayout.SOUTH);
            productPanel.add(addToCartButton, BorderLayout.AFTER_LAST_LINE);

            // Add product panel to main panel
            panel.add(productPanel);
        }

        // Cart Summary Button
        JButton cartSummaryButton = new JButton("Cart Summary");
        cartSummaryButton.setFont(new Font("Arial", Font.BOLD, 14));
        cartSummaryButton.setBackground(new Color(255, 255, 153));
        cartSummaryButton.setForeground(Color.BLACK);
        cartSummaryButton.setFocusPainted(false);
        cartSummaryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        cartSummaryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cartSummaryButton.setBackground(new Color(255, 204, 51));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                cartSummaryButton.setBackground(new Color(255, 255, 153));
            }
        });

        cartSummaryButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Your cart is empty!");
            } else {
                StringBuilder cartSummary = new StringBuilder("Cart Summary:\n");

                // Loop through each product in the cart
                for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();
                    double price = product.getPrice();
                    double totalPriceForItem = price * quantity;

                    // Append product details, including price and total for the item
                    cartSummary.append(product.getName())
                            .append(" (Qty: ")
                            .append(quantity)
                            .append(", Price: Rs ")
                            .append(price)
                            .append(", Total: Rs ")
                            .append(totalPriceForItem)
                            .append(")\n");
                }

                // Append the total items and cost to the summary
                cartSummary.append("Total Items: ").append(cart.getTotalItems()).append("\n");
                cartSummary.append("Total Cost: Rs ").append(cart.calculateTotal());

                // Display the cart summary in a message dialog
                JOptionPane.showMessageDialog(null, cartSummary.toString());
            }
        });

        // Checkout Button
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutButton.setBackground(new Color(255, 102, 102));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        checkoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                checkoutButton.setBackground(new Color(204, 51, 51));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                checkoutButton.setBackground(new Color(255, 102, 102));
            }
        });

        checkoutButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Your cart is empty!");
            } else {
                new CheckoutUI(cart, username).setVisible(true);
                dispose();
            }
        });

        // Add components to frame
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.add(cartSummaryButton);
        bottomPanel.add(checkoutButton);

        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM products";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(new Product(rs.getInt("product_id"), rs.getString("name"),
                        rs.getDouble("price"), rs.getInt("quantity")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // public static void main(String[] args) {
    //     new test("testUser").setVisible(true);
    // }
}
