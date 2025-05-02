package data_access;

import gamestore.Customer;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CustomerDAO {
    private static final String FILE_PATH = Paths.get("src", "data", "customers.csv").toString();
    
    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        boolean needsMigration = false;
        
        Path filePath = Paths.get(FILE_PATH);
        if (!Files.exists(filePath)) {
            System.err.println("Error: Customer data file not found at: " + FILE_PATH);
            return customers;
        }
        
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line = br.readLine(); // Read header
            if (line == null) {
                System.err.println("Warning: Customer file is empty");
                return customers;
            }
            
            // Check if we need to migrate data
            if (!line.toLowerCase().contains("email")) {
                needsMigration = true;
            }
            
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = line.split(",");
                    if (values.length < 2) {
                        System.err.println("Warning: Skipping invalid line: " + line);
                        continue;
                    }
                    
                    String customerID = values[0].trim();
                    String customerName = values[1].trim();
                    String email, phone;
                    
                    if (values.length >= 4) {
                        // New format with email and phone
                        email = values[2].trim();
                        phone = values[3].trim();
                    } else {
                        // Old format - generate default email and phone
                        email = customerName.toLowerCase().replace(" ", ".") + "@gamestore.com";
                        phone = "555-1234";
                    }
                    
                    Customer customer = new Customer(customerID, customerName, email, phone);
                    customers.add(customer);
                } catch (Exception e) {
                    System.err.println("Warning: Error parsing line: " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading customers file: " + e.getMessage());
            e.printStackTrace();
        }
        
        // If we found old format data, migrate it to new format
        if (needsMigration && !customers.isEmpty()) {
            migrateToNewFormat(customers);
        }
        
        return customers;
    }

    public int getNextCustomerId() {
        ArrayList<Customer> customers = getAllCustomers();
        int maxId = 0;
        
        for (Customer customer : customers) {
            try {
                // Extract the numeric part of the ID (e.g., "C001" -> 1)
                int id = Integer.parseInt(customer.getCustomerID().substring(1));
                maxId = Math.max(maxId, id);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // Skip invalid IDs
                System.err.println("Warning: Invalid customer ID format: " + customer.getCustomerID());
            }
        }
        
        return maxId + 1;
    }

    private void migrateToNewFormat(ArrayList<Customer> customers) {
        Path originalPath = Paths.get(FILE_PATH);
        Path tempPath = Paths.get(FILE_PATH + ".tmp");
        
        try {
            // Write to temporary file
            try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
                // Write header
                writer.write("CustomerID,CustomerName,Email,Phone\n");
                
                // Write all customers with full information
                for (Customer customer : customers) {
                    writer.write(String.format("%s,%s,%s,%s%n",
                        customer.getCustomerID(),
                        customer.getCustomerName(),
                        customer.getEmail(),
                        customer.getPhone()));
                }
            }
            
            // Replace the original file with the temporary file
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Successfully migrated customer data to new format.");
            
        } catch (IOException e) {
            System.err.println("Error migrating customer data: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignore cleanup errors
            }
        }
    }

    public void addCustomer(Customer customer) {
        Path filePath = Paths.get(FILE_PATH);
        
        // Create file and write header if it doesn't exist
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath.getParent());
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write("CustomerID,CustomerName,Email,Phone\n");
                }
            } catch (IOException e) {
                System.err.println("Error creating customer file: " + e.getMessage());
                return;
            }
        }

        // Check if ID already exists
        ArrayList<Customer> existingCustomers = getAllCustomers();
        for (Customer existing : existingCustomers) {
            if (existing.getCustomerID().equals(customer.getCustomerID())) {
                throw new IllegalArgumentException("Customer ID already exists: " + customer.getCustomerID());
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            writer.write(String.format("%s,%s,%s,%s%n",
                customer.getCustomerID(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getPhone()));
        } catch (IOException e) {
            System.err.println("Error writing customer: " + e.getMessage());
        }
    }

    public void updateCustomer(Customer customer) {
        ArrayList<Customer> customers = getAllCustomers();
        boolean found = false;
        
        Path originalPath = Paths.get(FILE_PATH);
        Path tempPath = Paths.get(FILE_PATH + ".tmp");
        
        try {
            // Write to temporary file
            try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
                // Write header
                writer.write("CustomerID,CustomerName,Email,Phone\n");
                
                // Write all customers, updating the matching one
                for (Customer existingCustomer : customers) {
                    if (existingCustomer.getCustomerID().equals(customer.getCustomerID())) {
                        writer.write(String.format("%s,%s,%s,%s%n",
                            customer.getCustomerID(),
                            customer.getCustomerName(),
                            customer.getEmail(),
                            customer.getPhone()));
                        found = true;
                    } else {
                        writer.write(String.format("%s,%s,%s,%s%n",
                            existingCustomer.getCustomerID(),
                            existingCustomer.getCustomerName(),
                            existingCustomer.getEmail(),
                            existingCustomer.getPhone()));
                    }
                }
            }
            
            if (!found) {
                Files.deleteIfExists(tempPath);
                throw new IllegalArgumentException("Customer not found with ID: " + customer.getCustomerID());
            }
            
            // Replace the original file with the temporary file
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignore cleanup errors
            }
        }
    }

    public void deleteCustomer(String customerID) {
        ArrayList<Customer> customers = getAllCustomers();
        boolean found = false;
        
        Path originalPath = Paths.get(FILE_PATH);
        Path tempPath = Paths.get(FILE_PATH + ".tmp");
        
        try {
            // Write to temporary file
            try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
                // Write header
                writer.write("CustomerID,CustomerName,Email,Phone\n");
                
                // Write all customers except the one to delete
                for (Customer customer : customers) {
                    if (!customer.getCustomerID().equals(customerID)) {
                        writer.write(String.format("%s,%s,%s,%s%n",
                            customer.getCustomerID(),
                            customer.getCustomerName(),
                            customer.getEmail(),
                            customer.getPhone()));
                    } else {
                        found = true;
                    }
                }
            }
            
            if (!found) {
                Files.deleteIfExists(tempPath);
                throw new IllegalArgumentException("Customer not found with ID: " + customerID);
            }
            
            // Replace the original file with the temporary file
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignore cleanup errors
            }
        }
    }
    
    public static void main(String[] args) {
        CustomerDAO dao = new CustomerDAO();
        ArrayList<Customer> customers = dao.getAllCustomers();
        
        System.out.println("All Customers:");
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
} 