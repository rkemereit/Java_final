package gamestore;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String customerID;
    private List<Transaction> purchaseHistory;
    private String customerName;
    private String email;
    private String phone;

    public Customer(String customerID, String customerName, String email, String phone) {
        if (customerID == null || customerID.trim().isEmpty()) throw new IllegalArgumentException("Customer ID cannot be null or empty");
        if (customerName == null || customerName.trim().isEmpty()) throw new IllegalArgumentException("Customer name cannot be null or empty");
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) throw new IllegalArgumentException("Invalid email format");
        if (phone == null || !phone.matches("\\d{3}-\\d{4}")) throw new IllegalArgumentException("Phone must be in format XXX-XXXX");

        this.customerID = customerID;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.purchaseHistory = new ArrayList<>();
    }

    // Copy constructor
    public Customer(Customer other) {
        this.customerID = other.customerID;
        this.customerName = other.customerName;
        this.email = other.email;
        this.phone = other.phone;
        this.purchaseHistory = new ArrayList<>(other.purchaseHistory);
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        if (customerID == null || customerID.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (!customerID.matches("C\\d{3}")) {
            throw new IllegalArgumentException("Customer ID must be in format C followed by 3 digits");
        }
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) throw new IllegalArgumentException("Invalid email format");
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("\\d{3}-\\d{4}")) throw new IllegalArgumentException("Phone must be in format XXX-XXXX");
        this.phone = phone;
    }

    public List<Transaction> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        purchaseHistory.add(transaction);
    }

    public String viewPurchaseHistory() {
        StringBuilder history = new StringBuilder();
        history.append("Purchase History for ").append(customerName).append(" (ID: ").append(customerID).append("):\n");
        for (Transaction transaction : purchaseHistory) {
            history.append(transaction.toString()).append("\n");
        }
        return history.toString();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", customerName, customerID);
    }
} 