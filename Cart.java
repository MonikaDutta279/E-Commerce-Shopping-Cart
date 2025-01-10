

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Product, Integer> items;

    public Cart() {
        items = new HashMap<>();
    }

    // Add a product to the cart with the specified quantity
    public void addToCart(Product product, int quantity) {
        if (quantity > 0) {
            items.put(product, items.getOrDefault(product, 0) + quantity);
        } else {
            System.out.println("Invalid quantity. Please add at least 1 item.");
        }
    }

    // Remove a product completely from the cart
    public void removeFromCart(Product product) {
        if (items.containsKey(product)) {
            items.remove(product);
        } else {
            System.out.println("Product not found in the cart.");
        }
    }

    // Update the quantity of a product in the cart
    public void updateQuantity(Product product, int newQuantity) {
        if (items.containsKey(product)) {
            if (newQuantity > 0) {
                items.put(product, newQuantity);
            } else {
                System.out.println("Quantity must be greater than zero.");
            }
        } else {
            System.out.println("Product not found in the cart.");
        }
    }

    // Calculate the total cost of items in the cart
    public double calculateTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    // Get the total number of items in the cart
    public int getTotalItems() {
        int totalItems = 0;
        for (int quantity : items.values()) {
            totalItems += quantity;
        }
        return totalItems;
    }

    public double getTotalCost() {
        return calculateTotal();
    }


    // Check if the cart is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Clear all items in the cart
    public void clearCart() {
        items.clear();
    }

    // Get all items in the cart
    public Map<Product, Integer> getItems() {
        return items;
    }

    // Display the contents of the cart (for testing purposes)
    public void displayCartContents() {
        if (isEmpty()) {
            System.out.println("The cart is empty.");
        } else {
            System.out.println("Cart Contents:");
            for (Map.Entry<Product, Integer> entry : items.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                System.out.println("Product: " + product.getName() + " | Quantity: " + quantity +
                        " | Price: Rs " + product.getPrice() + " | Subtotal: Rs " + (product.getPrice() * quantity));
            }
            System.out.println("Total Items: " + getTotalItems());
            System.out.println("Total Cost: Rs " + calculateTotal());
        }
    }
}
