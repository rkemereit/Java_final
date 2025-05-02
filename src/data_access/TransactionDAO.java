package data_access;

import gamestore.Customer;
import gamestore.GameItem;
import gamestore.PaymentMethod;
import gamestore.Transaction;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionDAO {
    private static final String FILE_PATH = Paths.get("src", "data", "transactions.csv").toString();
    private static final String HEADER = "TransactionID,Date,CustomerID,TotalAmount,PaymentMethod,ItemIDs";
    
    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.err.println("Error: Transaction data file not found at: " + FILE_PATH);
            return transactions;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip header
            if (line == null) {
                System.err.println("Warning: Transaction file is empty");
                return transactions;
            }
            
            while ((line = br.readLine()) != null) {
                try {
                    // Split on commas not inside quotes
                    String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (values.length < 6) {
                        System.err.println("Warning: Skipping invalid line: " + line);
                        continue;
                    }
                    
                    int transactionID = Integer.parseInt(values[0].trim());
                    Date transactionDate = dateFormat.parse(values[1].trim());
                    String customerID = values[2].trim();
                    double totalAmount = Double.parseDouble(values[3].trim());
                    
                    // Convert payment method string to enum
                    PaymentMethod paymentMethod;
                    String paymentStr = values[4].trim().toUpperCase();
                    switch (paymentStr) {
                        case "CARD":
                            paymentMethod = PaymentMethod.CARD;
                            break;
                        case "CASH":
                            paymentMethod = PaymentMethod.CASH;
                            break;
                        case "GIFTCARD":
                        case "GIFT_CARD":  // Support old format for backward compatibility
                            paymentMethod = PaymentMethod.GIFTCARD;
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid payment method: " + paymentStr);
                    }
                    
                    // Parse item IDs from quoted string
                    String itemsStr = values[5].trim().replace("\"", "");
                    String[] itemIDs = itemsStr.split(",");
                    
                    // Get customer from CustomerDAO
                    CustomerDAO customerDAO = new CustomerDAO();
                    Customer customer = customerDAO.getAllCustomers().stream()
                        .filter(c -> c.getCustomerID().equals(customerID))
                        .findFirst()
                        .orElse(null);
                    
                    if (customer == null) {
                        throw new IllegalArgumentException("Customer not found: " + customerID);
                    }
                    
                    // Get items from GameItemDAO
                    GameItemDAO gameItemDAO = new GameItemDAO();
                    ArrayList<GameItem> items = new ArrayList<>();
                    for (String itemID : itemIDs) {
                        int id = Integer.parseInt(itemID.trim());
                        gameItemDAO.getAllGameItems().stream()
                            .filter(item -> item.getItemID() == id)
                            .findFirst()
                            .ifPresent(items::add);
                    }
                    
                    if (items.isEmpty()) {
                        throw new IllegalArgumentException("No valid items found for transaction");
                    }
                    
                    // Create transaction with base constructor
                    Transaction transaction = new Transaction(transactionID, transactionDate, customer, paymentMethod);
                    
                    // Add items and set total amount
                    for (GameItem item : items) {
                        transaction.addItem(item);
                    }
                    transaction.setTotalAmount(totalAmount);
                    
                    // Add transaction to customer's purchase history
                    customer.addTransaction(transaction);
                    
                    transactions.add(transaction);
                } catch (Exception e) {
                    System.err.println("Warning: Error parsing line: " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading transactions file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    public boolean addTransaction(Transaction transaction) {
        ensureFileExists();
        ArrayList<Transaction> transactions = getAllTransactions();
        
        // Check if transaction ID already exists
        for (Transaction t : transactions) {
            if (t.getTransactionID() == transaction.getTransactionID()) {
                System.err.println("Error: Transaction with ID " + transaction.getTransactionID() + " already exists");
                return false;
            }
        }
        
        // Add new transaction
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Build the item IDs string
            StringBuilder itemIDs = new StringBuilder();
            for (GameItem item : transaction.getItems()) {
                if (itemIDs.length() > 0) {
                    itemIDs.append(",");
                }
                itemIDs.append(item.getItemID());
            }
            
            // Write the transaction data
            fw.write(String.format("%n%d,%s,%s,%.2f,%s,\"%s\"",
                transaction.getTransactionID(),
                dateFormat.format(transaction.getDate()),
                transaction.getCustomer().getCustomerID(),
                transaction.getTotalAmount(),
                transaction.getPaymentMethod().toString(),
                itemIDs.toString()));
            
            // Add transaction to customer's purchase history
            transaction.getCustomer().addTransaction(transaction);
            
            return true;
        } catch (IOException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            return false;
        }
    }

    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                File directory = file.getParentFile();
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                file.createNewFile();
                // Write header
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(HEADER);
                }
            } catch (IOException e) {
                System.err.println("Error creating transaction file: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        TransactionDAO dao = new TransactionDAO();
        ArrayList<Transaction> transactions = dao.getAllTransactions();
        
        System.out.println("All Transactions:");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
} 