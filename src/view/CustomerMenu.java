package view;

import data_access.CustomerDAO;
import gamestore.Customer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CustomerMenu implements Menu {
    private static final String[] MENU_ITEMS = {
        "View All Customers",
        "Add New Customer",
        "Update Customer",
        "Delete Customer"
    };

    private static final String[] COLUMN_HEADERS = {
        "ID", "Name", "Email", "Phone", "Purchases"
    };

    private static final int[] COLUMN_WIDTHS = {
        6, 30, 30, 10, 9
    };

    private final CustomerDAO customerDAO;

    public CustomerMenu() {
        customerDAO = new CustomerDAO();
    }

    @Override
    public void show() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println(" ".repeat(20) + "Customer Management");
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
                    viewAllCustomers();
                    break;
                case 2:
                    addNewCustomer();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 0:
                    return;
            }
            MenuUtils.pressEnterToContinue();
        }
    }

    private void viewAllCustomers() {
        ArrayList<Customer> customers = customerDAO.getAllCustomers();
        Collections.sort(customers, Comparator.comparing(Customer::getCustomerName));

        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(25) + "Customers");
        System.out.println("=".repeat(60));

        if (customers.isEmpty()) {
            System.out.println("\nNo customers found in the system.");
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
            COLUMN_WIDTHS[2] + "s | %-" + COLUMN_WIDTHS[3] + "s | %" + COLUMN_WIDTHS[4] + "s |%n",
            COLUMN_HEADERS[0], COLUMN_HEADERS[1], COLUMN_HEADERS[2], COLUMN_HEADERS[3], COLUMN_HEADERS[4]);
        printTableDivider(totalWidth);

        // Print data rows
        for (Customer customer : customers) {
            System.out.printf("| %-"+COLUMN_WIDTHS[0]+"s | %-"+COLUMN_WIDTHS[1]+"s | %-"+
                COLUMN_WIDTHS[2]+"s | %-"+COLUMN_WIDTHS[3]+"s | %"+COLUMN_WIDTHS[4]+"d |%n",
                customer.getCustomerID(),
                truncate(customer.getCustomerName(), COLUMN_WIDTHS[1]),
                truncate(customer.getEmail(), COLUMN_WIDTHS[2]),
                customer.getPhone(),
                customer.getPurchaseHistory().size()
            );
        }
        printTableDivider(totalWidth);
    }

    private void addNewCustomer() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(23) + "Add New Customer");
        System.out.println("=".repeat(60) + "\n");

        String customerID = "C" + String.format("%03d", customerDAO.getNextCustomerId());
        
        System.out.print("Enter customer name: ");
        String customerName = MenuUtils.getString();
        
        String email;
        do {
            System.out.print("Enter email address: ");
            email = MenuUtils.getString();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("\nError: Invalid email format. Please try again.");
            }
        } while (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
        
        String phone;
        do {
            System.out.print("Enter phone number (XXX-XXXX): ");
            phone = MenuUtils.getString();
            if (!phone.matches("\\d{3}-\\d{4}")) {
                System.out.println("\nError: Invalid phone format. Please use XXX-XXXX format.");
            }
        } while (!phone.matches("\\d{3}-\\d{4}"));

        try {
            Customer customer = new Customer(customerID, customerName, email, phone);
            customerDAO.addCustomer(customer);
            System.out.println("\nSuccess: Customer added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(22) + "Update Customer");
        System.out.println("=".repeat(60) + "\n");

        ArrayList<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers available to update.");
            return;
        }

        System.out.println("Available Customers:");
        System.out.println("-".repeat(50));
        for (Customer customer : customers) {
            System.out.printf("  %s. %-30s (%s)%n", 
                customer.getCustomerID(), 
                truncate(customer.getCustomerName(), 30),
                customer.getPhone());
        }
        System.out.println("-".repeat(50));

        System.out.print("\nEnter Customer ID to update (or 0 to cancel): ");
        String customerID = MenuUtils.getString().toUpperCase();
        
        if (customerID.equals("0")) {
            System.out.println("\nInfo: Update operation cancelled.");
            return;
        }

        Customer customerToUpdate = null;
        for (Customer customer : customers) {
            if (customer.getCustomerID().equals(customerID)) {
                customerToUpdate = customer;
                break;
            }
        }

        if (customerToUpdate == null) {
            System.out.println("\nError: Customer not found with ID: " + customerID);
            return;
        }

        System.out.println("\nCurrent customer details:");
        System.out.println("-".repeat(50));
        System.out.println("Name: " + customerToUpdate.getCustomerName());
        System.out.println("Email: " + customerToUpdate.getEmail());
        System.out.println("Phone: " + customerToUpdate.getPhone());
        System.out.println("-".repeat(50));

        System.out.println("\nEnter new details (press Enter to keep current value):");
        
        System.out.print("New name [" + customerToUpdate.getCustomerName() + "]: ");
        String newName = MenuUtils.getString();
        if (!newName.isEmpty()) {
            customerToUpdate.setCustomerName(newName);
        }

        String newEmail;
        do {
            System.out.print("New email [" + customerToUpdate.getEmail() + "]: ");
            newEmail = MenuUtils.getString();
            if (!newEmail.isEmpty() && !newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("\nError: Invalid email format. Please try again.");
            }
        } while (!newEmail.isEmpty() && !newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
        if (!newEmail.isEmpty()) {
            customerToUpdate.setEmail(newEmail);
        }

        String newPhone;
        do {
            System.out.print("New phone [" + customerToUpdate.getPhone() + "]: ");
            newPhone = MenuUtils.getString();
            if (!newPhone.isEmpty() && !newPhone.matches("\\d{3}-\\d{4}")) {
                System.out.println("\nError: Invalid phone format. Please use XXX-XXXX format.");
            }
        } while (!newPhone.isEmpty() && !newPhone.matches("\\d{3}-\\d{4}"));
        if (!newPhone.isEmpty()) {
            customerToUpdate.setPhone(newPhone);
        }

        try {
            customerDAO.updateCustomer(customerToUpdate);
            System.out.println("\nSuccess: Customer updated successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(22) + "Delete Customer");
        System.out.println("=".repeat(60) + "\n");

        ArrayList<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers available to delete.");
            return;
        }

        System.out.println("Available Customers:");
        System.out.println("-".repeat(50));
        for (Customer customer : customers) {
            System.out.printf("  %s. %-30s (%s)%n", 
                customer.getCustomerID(), 
                truncate(customer.getCustomerName(), 30),
                customer.getPhone());
        }
        System.out.println("-".repeat(50));

        System.out.print("\nEnter Customer ID to delete (or 0 to cancel): ");
        String customerID = MenuUtils.getString().toUpperCase();
        
        if (customerID.equals("0")) {
            System.out.println("\nInfo: Delete operation cancelled.");
            return;
        }

        Customer customerToDelete = null;
        for (Customer customer : customers) {
            if (customer.getCustomerID().equals(customerID)) {
                customerToDelete = customer;
                break;
            }
        }

        if (customerToDelete == null) {
            System.out.println("\nError: Customer not found with ID: " + customerID);
            return;
        }

        if (!customerToDelete.getPurchaseHistory().isEmpty()) {
            System.out.println("\nWarning: This customer has purchase history!");
            System.out.println("Deleting this customer will also remove their purchase records.");
        }

        System.out.println("\nWarning: This action cannot be undone!");
        System.out.printf("Are you sure you want to delete customer '%s'? (y/n): ", 
            customerToDelete.getCustomerName());
        String confirm = MenuUtils.getString().toLowerCase();
        
        if (confirm.equals("y")) {
            try {
                customerDAO.deleteCustomer(customerToDelete.getCustomerID());
                System.out.println("\nSuccess: Customer deleted successfully!");
            } catch (Exception e) {
                System.out.println("\nError: Failed to delete customer. " + e.getMessage());
            }
        } else {
            System.out.println("\nInfo: Delete operation cancelled.");
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