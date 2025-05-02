package gamestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int transactionID;
    private Date date;
    private Customer customer;
    private List<GameItem> items;
    private double totalAmount;
    private PaymentMethod paymentMethod;

    public Transaction(int transactionID, Date date, Customer customer, PaymentMethod paymentMethod) {
        if (transactionID <= 0) throw new IllegalArgumentException("Transaction ID must be positive");
        if (date == null) throw new IllegalArgumentException("Date cannot be null");
        if (customer == null) throw new IllegalArgumentException("Customer cannot be null");
        if (paymentMethod == null) throw new IllegalArgumentException("Payment method cannot be null");

        this.transactionID = transactionID;
        this.date = new Date(date.getTime()); // Defensive copy
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    // Copy constructor
    public Transaction(Transaction other) {
        this.transactionID = other.transactionID;
        this.date = new Date(other.date.getTime()); // Deep copy of Date
        this.customer = new Customer(other.customer); // Assuming Customer has a copy constructor
        this.paymentMethod = other.paymentMethod; // Enum is immutable
        
        // Deep copy items list
        this.items = new ArrayList<>();
        for (GameItem item : other.items) {
            this.items.add(new GameItem(item)); // Assuming GameItem has a copy constructor
        }
        
        this.totalAmount = other.totalAmount;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        if (transactionID <= 0) {
            throw new IllegalArgumentException("Transaction ID must be positive");
        }
        this.transactionID = transactionID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (date.after(new Date())) {
            throw new IllegalArgumentException("Transaction date cannot be in the future");
        }
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
    }

    public List<GameItem> getItems() {
        return items;
    }

    public void addItem(GameItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Game item cannot be null");
        }
        items.add(item);
        calculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.paymentMethod = paymentMethod;
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (GameItem item : items) {
            totalAmount += item.getRetailPrice();
        }
    }

    public boolean processPayment() {
        // Implementation would depend on payment processing system
        // For now, just return true to indicate successful payment
        return true;
    }

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Transaction ID: ").append(transactionID).append("\n");
        receipt.append("Date: ").append(date).append("\n");
        receipt.append("Customer: ").append(customer.getCustomerName()).append("\n");
        receipt.append("Items:\n");
        
        for (GameItem item : items) {
            receipt.append("- ").append(item.getTitle())
                   .append(" ($").append(String.format("%.2f", item.getRetailPrice()))
                   .append(")\n");
        }
        
        receipt.append("\nTotal Amount: $").append(String.format("%.2f", totalAmount));
        receipt.append("\nPayment Method: ").append(paymentMethod);
        
        return receipt.toString();
    }

    @Override
    public String toString() {
        return String.format("Transaction #%d - %s - %s - $%.2f - %s",
            transactionID, date, customer.getCustomerName(), totalAmount, paymentMethod);
    }
} 