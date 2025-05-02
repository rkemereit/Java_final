package gamestore;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Store implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int storeID;
    private String storeName;
    private String address;
    private String phoneNumber;
    private ArrayList<Employee> employees;
    private List<Transaction> transactions;

    public Store(int storeID, String storeName, String address, String phoneNumber) {
        if (storeID <= 0) throw new IllegalArgumentException("Store ID must be positive");
        if (storeName == null || storeName.trim().isEmpty()) throw new IllegalArgumentException("Store name cannot be null or empty");
        if (address == null || address.trim().isEmpty()) throw new IllegalArgumentException("Address cannot be null or empty");
        if (!phoneNumber.matches("\\d{3}-\\d{4}")) throw new IllegalArgumentException("Phone number must be in format XXX-XXXX");

        this.storeID = storeID;
        this.storeName = storeName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.employees = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    // Deep copy constructor
    public Store(Store other) {
        this.storeID = other.storeID;
        this.storeName = other.storeName;
        this.address = other.address;
        this.phoneNumber = other.phoneNumber;
        
        // Deep copy employees
        this.employees = new ArrayList<>();
        for (Employee emp : other.employees) {
            this.employees.add(new Employee(emp)); // Assuming Employee has a copy constructor
        }
        
        // Deep copy transactions
        this.transactions = new ArrayList<>();
        for (Transaction trans : other.transactions) {
            this.transactions.add(new Transaction(trans)); // Assuming Transaction has a copy constructor
        }
    }

    // Deep copy method using serialization
    public Store deepCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (Store) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

    public int getStoreID() { return storeID; }
    public String getStoreName() { return storeName; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public ArrayList<Employee> getEmployees() { return employees; }

    public void setStoreID(int storeID) { this.storeID = storeID; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void addEmployee(Employee employee) { this.employees.add(employee); }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactions.add(transaction);
    }

    public String generateSalesReport(Date startDate, Date endDate) {
        StringBuilder report = new StringBuilder();
        report.append("Sales Report from ").append(startDate).append(" to ").append(endDate).append("\n\n");

        double totalSales = 0.0;
        int totalTransactions = 0;

        for (Transaction transaction : transactions) {
            Date transDate = transaction.getDate();
            if (transDate.compareTo(startDate) >= 0 && transDate.compareTo(endDate) <= 0) {
                totalSales += transaction.getTotalAmount();
                totalTransactions++;
            }
        }

        report.append("Total Transactions: ").append(totalTransactions).append("\n");
        report.append("Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
        report.append("Average Transaction Value: $")
              .append(totalTransactions > 0 ? String.format("%.2f", totalSales / totalTransactions) : "0.00")
              .append("\n");

        return report.toString();
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeID=" + storeID +
                ", storeName='" + storeName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", employees=" + employees.size() +
                ", transactions=" + transactions.size() +
                '}';
    }

    public static void main(String[] args) {
        // Create original store
        Store original = new Store(1, "Game Haven", "123 Main St", "555-1234");
        original.addEmployee(new Employee(1, "John Doe", "Manager", 20.0, new Date()));
        
        // Shallow copy (just references)
        Store shallowCopy = original;
        
        // Deep copy using copy constructor
        Store deepCopy1 = new Store(original);
        
        // Deep copy using serialization
        Store deepCopy2 = original.deepCopy();
        
        // Demonstrate the difference
        System.out.println("Original store name: " + original.getStoreName());
        System.out.println("Shallow copy store name: " + shallowCopy.getStoreName());
        System.out.println("Deep copy 1 store name: " + deepCopy1.getStoreName());
        System.out.println("Deep copy 2 store name: " + deepCopy2.getStoreName());
        
        // Modify original
        original.setStoreName("Game Paradise");
        
        // Show that shallow copy changes with original, but deep copies don't
        System.out.println("\nAfter changing original:");
        System.out.println("Original store name: " + original.getStoreName());
        System.out.println("Shallow copy store name: " + shallowCopy.getStoreName());
        System.out.println("Deep copy 1 store name: " + deepCopy1.getStoreName());
        System.out.println("Deep copy 2 store name: " + deepCopy2.getStoreName());
        
        // Show that employee lists are separate in deep copies
        System.out.println("\nEmployee count in each store:");
        System.out.println("Original: " + original.getEmployees().size());
        System.out.println("Shallow copy: " + shallowCopy.getEmployees().size());
        System.out.println("Deep copy 1: " + deepCopy1.getEmployees().size());
        System.out.println("Deep copy 2: " + deepCopy2.getEmployees().size());
    }
} 