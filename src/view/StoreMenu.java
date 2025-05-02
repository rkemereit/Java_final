package view;

import data_access.StoreDAO;
import gamestore.Store;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StoreMenu implements Menu {
    private static final String[] MENU_ITEMS = {
        "View All Stores",
        "Add New Store",
        "Update Store Information",
        "Delete Store"
    };

    private static final String[] COLUMN_HEADERS = {
        "ID", "Name", "Address", "Phone"
    };

    private static final int[] COLUMN_WIDTHS = {
        6, 30, 40, 15
    };

    private final StoreDAO storeDAO;

    public StoreMenu() {
        storeDAO = new StoreDAO();
    }

    @Override
    public void show() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println(" ".repeat(20) + "Store Management");
            System.out.println("=".repeat(60) + "\n");

            for (int i = 0; i < MENU_ITEMS.length; i++) {
                System.out.printf("  %d. %s%n", (i + 1), MENU_ITEMS[i]);
            }
            System.out.printf("  0. Return to Main Menu%n%n");
            System.out.println("-".repeat(60));
            System.out.print("Enter your choice: ");

            int choice = MenuUtils.getInt(0, MENU_ITEMS.length);

            switch (choice) {
                case 1:
                    viewAllStores();
                    break;
                case 2:
                    addStore();
                    break;
                case 3:
                    // updateStore();
                    break;
                case 4:
                    deleteStore();
                    break;
                case 0:
                    return;
            }
            MenuUtils.pressEnterToContinue();
        }
    }

    private void viewAllStores() {
        ArrayList<Store> stores = storeDAO.getAllStores();
        Collections.sort(stores, Comparator.comparing(Store::getStoreName));

        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(25) + "Stores List");
        System.out.println("=".repeat(60));
        
        if (stores.isEmpty()) {
            System.out.println("\nNo stores found in the system.");
            return;
        }

        // Calculate total width
        int totalWidth = COLUMN_WIDTHS.length * 3 - 1; // Account for borders
        for (int width : COLUMN_WIDTHS) {
            totalWidth += width;
        }

        // Print table header
        printTableDivider(totalWidth);
        System.out.printf("| %-4s | %-28s | %-38s | %-13s |%n",
            COLUMN_HEADERS[0], COLUMN_HEADERS[1], COLUMN_HEADERS[2], COLUMN_HEADERS[3]);
        printTableDivider(totalWidth);

        // Print stores
        for (Store store : stores) {
            System.out.printf("| %4d | %-28s | %-38s | %-13s |%n",
                store.getStoreID(),
                truncate(store.getStoreName(), 28),
                truncate(store.getAddress(), 38),
                store.getPhoneNumber()
            );
        }
        printTableDivider(totalWidth);
    }

    private void printTableDivider(int width) {
        System.out.println("-".repeat(width));
    }

    private void addStore() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(23) + "Add New Store");
        System.out.println("=".repeat(60) + "\n");

        int storeID = MenuUtils.getNextId("store");
        
        System.out.print("Enter store name: ");
        String storeName = MenuUtils.getString();
        
        System.out.print("Enter store address: ");
        String address = MenuUtils.getString();
        
        String phoneNumber;
        do {
            System.out.print("Enter phone number (XXX-XXXX): ");
            phoneNumber = MenuUtils.getString();
            if (!phoneNumber.matches("\\d{3}-\\d{4}")) {
                System.out.println("\nError: Invalid phone number format. Please use XXX-XXXX format.");
            }
        } while (!phoneNumber.matches("\\d{3}-\\d{4}"));

        try {
            Store store = new Store(storeID, storeName, address, phoneNumber);
            storeDAO.addStore(store);
            System.out.println("\nSuccess: Store added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void deleteStore() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat(23) + "Delete Store");
        System.out.println("=".repeat(60) + "\n");

        ArrayList<Store> stores = storeDAO.getAllStores();
        if (stores.isEmpty()) {
            System.out.println("No stores available to delete.");
            return;
        }

        System.out.println("Available Stores:");
        System.out.println("-".repeat(50));
        for (Store store : stores) {
            System.out.printf("  %d. %-30s (%s)%n", 
                store.getStoreID(), 
                truncate(store.getStoreName(), 30),
                store.getPhoneNumber());
        }
        System.out.println("-".repeat(50));

        System.out.print("\nEnter Store ID to delete (0 to cancel): ");
        int storeID = MenuUtils.getInt(0, Integer.MAX_VALUE);
        
        if (storeID == 0) {
            System.out.println("\nInfo: Delete operation cancelled.");
            return;
        }

        Store storeToDelete = null;
        for (Store store : stores) {
            if (store.getStoreID() == storeID) {
                storeToDelete = store;
                break;
            }
        }

        if (storeToDelete == null) {
            System.out.println("\nError: Store not found with ID: " + storeID);
            return;
        }

        System.out.println("\nWarning: This action cannot be undone!");
        System.out.printf("Are you sure you want to delete store '%s'? (y/n): ", 
            storeToDelete.getStoreName());
        String confirm = MenuUtils.getString().toLowerCase();
        
        if (confirm.equals("y")) {
            if (storeDAO.deleteStore(storeID)) {
                System.out.println("\nSuccess: Store deleted successfully!");
            } else {
                System.out.println("\nError: Failed to delete store.");
            }
        } else {
            System.out.println("\nInfo: Delete operation cancelled.");
        }
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
} 