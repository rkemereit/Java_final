package data_access;

import gamestore.Store;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class StoreDAO {
    private static final String FILE_PATH = Paths.get("src", "data", "stores.csv").toString();
    private static final String HEADER = "StoreID,StoreName,Address,PhoneNumber";
    
    public ArrayList<Store> getAllStores() {
        ArrayList<Store> stores = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.err.println("Error: Store data file not found at: " + FILE_PATH);
            return stores;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip header
            if (line == null) {
                System.err.println("Warning: Store file is empty");
                return stores;
            }
            
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = line.split(",");
                    if (values.length < 4) {
                        System.err.println("Warning: Skipping invalid line: " + line);
                        continue;
                    }
                    
                    int storeID = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    String address = values[2].trim();
                    String phone = values[3].trim();
                    
                    Store store = new Store(storeID, name, address, phone);
                    stores.add(store);
                } catch (Exception e) {
                    System.err.println("Warning: Error parsing line: " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading stores file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stores;
    }

    public void addStore(Store store) {
        ensureFileExists();
        ArrayList<Store> stores = getAllStores();
        
        // Check if store ID already exists
        for (Store s : stores) {
            if (s.getStoreID() == store.getStoreID()) {
                System.err.println("Error: Store with ID " + store.getStoreID() + " already exists");
                return;
            }
        }
        
        stores.add(store);
        if (!saveToFile(stores)) {
            System.err.println("Failed to save store to file");
        }
    }

    public boolean deleteStore(int storeID) {
        ArrayList<Store> stores = getAllStores();
        boolean removed = stores.removeIf(s -> s.getStoreID() == storeID);
        
        if (removed) {
            return saveToFile(stores);
        }
        return false;
    }

    private boolean saveToFile(ArrayList<Store> stores) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            // Write header
            fw.write(HEADER + "\n");
            
            // Write stores
            for (Store store : stores) {
                fw.write(String.format("%d,%s,%s,%s%n",
                    store.getStoreID(),
                    store.getStoreName().replace(",", ";"),  // Escape commas in text
                    store.getAddress().replace(",", ";"),    // Escape commas in text
                    store.getPhoneNumber()));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing stores to file: " + e.getMessage());
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
                    fw.write(HEADER + "\n");  // Add newline after header
                }
            } catch (IOException e) {
                System.err.println("Error creating stores file: " + e.getMessage());
            }
        }
    }
} 