package view;

import data_access.CustomerDAO;
import data_access.GameItemDAO;
import data_access.TransactionDAO;
import gamestore.Customer;
import gamestore.GameItem;
import gamestore.PaymentMethod;
import gamestore.Transaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TransactionMenu implements Menu {
    private static final String[] MENU_ITEMS = {
        "View Recent Transactions",
        "Add New Transaction"
    };

    private static final String[] COLUMN_HEADERS = {
        "ID", "Date", "Customer", "Items", "Total", "Payment"
    };

    private static final int[] COLUMN_WIDTHS = {
        4, 10, 25, 5, 8, 10
    };

    private final TransactionDAO transactionDAO;
    private final CustomerDAO customerDAO;
    private final GameItemDAO gameItemDAO;
    private final SimpleDateFormat dateFormat;

    public TransactionMenu() {
        transactionDAO = new TransactionDAO();
        customerDAO = new CustomerDAO();
        gameItemDAO = new GameItemDAO();
        dateFormat = new SimpleDateFormat("MM-dd-yy");
    }

    @Override
    public void show() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println(" ".repeat(20) + "Transaction Management");
            System.out.println("=".repeat(60) + "\n");

            for (int i = 0; i < MENU_ITEMS.length; i++) {
                System.out.printf("  %d. %s%n", (i + 1), MENU_ITEMS[i]);
            }
            System.out.printf("  0. Exit%n");
            System.out.println("-".repeat(60));
            System.out.print("Enter your choice: ");

            int choice = MenuUtils.getInt(0, MENU_ITEMS.length);

            switch (choice) {
                case 1:
                    viewAllTransactions();
                    break;
                case 2:
                    addNewTransaction();
                    break;
                case 0:
                    return;
            }
            MenuUtils.pressEnterToContinue();
        }
    }

    private void viewAllTransactions() {
        ArrayList<Transaction> transactions = transactionDAO.getAllTransactions();
        Collections.sort(transactions, Comparator.comparing(Transaction::getDate).reversed());

        System.out.println("\n" + "=".repeat(70));
        System.out.println(" ".repeat(25) + "Recent Transactions");
        System.out.println("=".repeat(70));

        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions found in the system.");
            return;
        }

        // Calculate total width
        int totalWidth = COLUMN_WIDTHS.length * 3 - 1; // Account for borders
        for (int width : COLUMN_WIDTHS) {
            totalWidth += width;
        }

        // Print header
        printTableDivider(totalWidth);
        System.out.printf("| %-" + COLUMN_WIDTHS[0] + "s | %-" + COLUMN_WIDTHS[1] + "s | %-" + 
            COLUMN_WIDTHS[2] + "s | %" + COLUMN_WIDTHS[3] + "s | %" + 
            COLUMN_WIDTHS[4] + "s | %-" + COLUMN_WIDTHS[5] + "s |%n",
            COLUMN_HEADERS[0], COLUMN_HEADERS[1], COLUMN_HEADERS[2], 
            COLUMN_HEADERS[3], COLUMN_HEADERS[4], COLUMN_HEADERS[5]);
        printTableDivider(totalWidth);

        // Print only the 10 most recent transactions
        int count = 0;
        for (Transaction transaction : transactions) {
            if (count++ >= 10) break;
            
            System.out.printf("| %"+COLUMN_WIDTHS[0]+"d | %-"+COLUMN_WIDTHS[1]+"s | %-"+
                COLUMN_WIDTHS[2]+"s | %"+COLUMN_WIDTHS[3]+"d | $%"+
                (COLUMN_WIDTHS[4]-1)+".2f | %-"+COLUMN_WIDTHS[5]+"s |%n",
                transaction.getTransactionID(),
                dateFormat.format(transaction.getDate()),
                truncate(transaction.getCustomer().getCustomerName(), COLUMN_WIDTHS[2]),
                transaction.getItems().size(),
                transaction.getTotalAmount(),
                truncate(transaction.getPaymentMethod().toString(), COLUMN_WIDTHS[5])
            );
        }
        printTableDivider(totalWidth);
        
        if (transactions.size() > 10) {
            System.out.printf("%nShowing 10 most recent transactions. %d more transactions available.%n", 
                transactions.size() - 10);
        }
    }

    private void addNewTransaction() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(20) + "Add New Transaction");
        System.out.println("=".repeat(60));

        // Get customer
        ArrayList<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("\nNo customers found in the system. Please add a customer first.");
            return;
        }

        System.out.println("\nAvailable Customers:");
        for (Customer customer : customers) {
            System.out.printf("%s: %s%n", customer.getCustomerID(), customer.getCustomerName());
        }

        Customer selectedCustomer = null;
        while (selectedCustomer == null) {
            System.out.print("\nEnter Customer ID: ");
            final String customerID = MenuUtils.getString();
            
            for (Customer customer : customers) {
                if (customer.getCustomerID().equals(customerID)) {
                    selectedCustomer = customer;
                    break;
                }
            }
            
            if (selectedCustomer == null) {
                System.out.println("Invalid Customer ID. Please try again.");
            }
        }

        // Get items
        ArrayList<GameItem> availableItems = gameItemDAO.getAllGameItems();
        if (availableItems.isEmpty()) {
            System.out.println("\nNo items available in the system. Please add items first.");
            return;
        }

        ArrayList<GameItem> selectedItems = new ArrayList<>();
        boolean addingItems = true;

        while (addingItems) {
            System.out.println("\nAvailable Items:");
            System.out.println("-".repeat(60));
            System.out.printf("%-6s %-30s %-10s %-8s%n", "ID", "Title", "Platform", "Price");
            System.out.println("-".repeat(60));
            
            for (GameItem item : availableItems) {
                System.out.printf("%-6d %-30s %-10s $%-7.2f%n",
                    item.getItemID(),
                    truncate(item.getTitle(), 30),
                    item.getPlatform(),
                    item.getRetailPrice());
            }

            System.out.print("\nEnter Item ID (or 0 to finish): ");
            final int itemID = MenuUtils.getInt(0, Integer.MAX_VALUE);
            
            if (itemID == 0) {
                if (selectedItems.isEmpty()) {
                    System.out.println("Please select at least one item.");
                    continue;
                }
                addingItems = false;
            } else {
                GameItem selectedItem = null;
                for (GameItem item : availableItems) {
                    if (item.getItemID() == itemID) {
                        selectedItem = item;
                        break;
                    }
                }
                
                if (selectedItem == null) {
                    System.out.println("Invalid Item ID. Please try again.");
                } else {
                    selectedItems.add(selectedItem);
                    System.out.printf("Added: %s ($%.2f)%n", selectedItem.getTitle(), selectedItem.getRetailPrice());
                }
            }
        }

        // Get payment method
        System.out.println("\nPayment Methods:");
        PaymentMethod[] paymentMethods = PaymentMethod.values();
        for (int i = 0; i < paymentMethods.length; i++) {
            System.out.printf("%d. %s%n", i + 1, paymentMethods[i]);
        }

        int paymentChoice = MenuUtils.getInt(1, paymentMethods.length);
        PaymentMethod selectedPayment = paymentMethods[paymentChoice - 1];

        // Create transaction
        try {
            int transactionID = MenuUtils.getNextId("transaction");
            Transaction newTransaction = new Transaction(transactionID, new Date(), selectedCustomer, selectedPayment);
            
            // Add items
            for (GameItem item : selectedItems) {
                newTransaction.addItem(item);
            }

            // Save transaction
            if (transactionDAO.addTransaction(newTransaction)) {
                System.out.println("\nTransaction created successfully!");
                System.out.println("\nReceipt:");
                System.out.println("=".repeat(40));
                System.out.println(newTransaction.generateReceipt());
                System.out.println("=".repeat(40));
            } else {
                System.out.println("\nFailed to create transaction.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError creating transaction: " + e.getMessage());
        }
    }

    private void printTableDivider(int width) {
        System.out.println("-".repeat(width));
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
} 