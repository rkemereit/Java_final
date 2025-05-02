package data_access;

import gamestore.GameGenre;
import gamestore.GameItem;
import gamestore.GamePlatform;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameItemDAO {
    private static final String FILE_PATH = Paths.get("src", "data", "game_items.csv").toString();
    
    public ArrayList<GameItem> getAllGameItems() {
        ArrayList<GameItem> gameItems = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Path filePath = Paths.get(FILE_PATH);
        if (!Files.exists(filePath)) {
            System.err.println("Error: Game items data file not found at: " + FILE_PATH);
            return gameItems;
        }
        
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line = br.readLine(); // Skip header
            if (line == null) {
                System.err.println("Warning: Game items file is empty");
                return gameItems;
            }
            
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = line.split(",");
                    if (values.length < 8) {
                        System.err.println("Warning: Skipping invalid line: " + line);
                        continue;
                    }
                    
                    int itemID = Integer.parseInt(values[0].trim());
                    String title = values[1].trim();
                    String publisher = values[2].trim();
                    Date releaseDate = dateFormat.parse(values[3].trim());
                    double retailPrice = Double.parseDouble(values[4].trim());
                    GamePlatform platform = GamePlatform.valueOf(values[5].trim());
                    GameGenre genre = GameGenre.valueOf(values[6].trim());
                    boolean isRetro = Boolean.parseBoolean(values[7].trim());
                    
                    GameItem item = new GameItem(itemID, title, publisher, releaseDate, 
                                               retailPrice, platform, genre, isRetro);
                    gameItems.add(item);
                } catch (Exception e) {
                    System.err.println("Warning: Error parsing line: " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading game items file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return gameItems;
    }

    public int getNextGameId() {
        ArrayList<GameItem> items = getAllGameItems();
        int maxId = 0;
        
        for (GameItem item : items) {
            maxId = Math.max(maxId, item.getItemID());
        }
        
        return maxId + 1;
    }
    
    public void addGameItem(GameItem item) {
        // Check if ID already exists
        ArrayList<GameItem> existingItems = getAllGameItems();
        for (GameItem existing : existingItems) {
            if (existing.getItemID() == item.getItemID()) {
                throw new IllegalArgumentException("Game item ID already exists: " + item.getItemID());
            }
        }

        // Create directory if it doesn't exist
        Path filePath = Paths.get(FILE_PATH);
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
            return;
        }
        
        // Create file with header if it doesn't exist
        if (!Files.exists(filePath)) {
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write("itemID,title,publisher,releaseDate,retailPrice,platform,genre,isRetro\n");
            } catch (IOException e) {
                System.err.println("Error creating game items file: " + e.getMessage());
                return;
            }
        }
        
        // Append the new game item
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Add a newline before writing if the file doesn't end with one
            String content = new String(Files.readAllBytes(filePath));
            if (!content.isEmpty() && !content.endsWith("\n")) {
                writer.write("\n");
            }
            
            writer.write(String.format("%d,%s,%s,%s,%.2f,%s,%s,%b",
                item.getItemID(),
                escapeCSV(item.getTitle()),
                escapeCSV(item.getPublisher()),
                dateFormat.format(item.getReleaseDate()),
                item.getRetailPrice(),
                item.getPlatform(),
                item.getGenre(),
                item.isRetro()
            ));
        } catch (IOException e) {
            System.err.println("Error writing game item: " + e.getMessage());
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        // If the value contains comma, quote, or newline, wrap it in quotes and escape existing quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    public void updateGameItem(GameItem updatedItem) {
        ArrayList<GameItem> allItems = getAllGameItems();
        boolean found = false;
        
        Path originalPath = Paths.get(FILE_PATH);
        Path tempPath = Paths.get(FILE_PATH + ".tmp");
        
        try {
            // Write to temporary file
            try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
                // Write header
                writer.write("itemID,title,publisher,releaseDate,retailPrice,platform,genre,isRetro\n");
                
                // Write all items, replacing the updated one
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (GameItem item : allItems) {
                    if (item.getItemID() == updatedItem.getItemID()) {
                        // Write updated item
                        writer.write(String.format("%d,%s,%s,%s,%.2f,%s,%s,%b%n",
                            updatedItem.getItemID(),
                            escapeCSV(updatedItem.getTitle()),
                            escapeCSV(updatedItem.getPublisher()),
                            dateFormat.format(updatedItem.getReleaseDate()),
                            updatedItem.getRetailPrice(),
                            updatedItem.getPlatform(),
                            updatedItem.getGenre(),
                            updatedItem.isRetro()
                        ));
                        found = true;
                    } else {
                        // Write existing item
                        writer.write(String.format("%d,%s,%s,%s,%.2f,%s,%s,%b%n",
                            item.getItemID(),
                            escapeCSV(item.getTitle()),
                            escapeCSV(item.getPublisher()),
                            dateFormat.format(item.getReleaseDate()),
                            item.getRetailPrice(),
                            item.getPlatform(),
                            item.getGenre(),
                            item.isRetro()
                        ));
                    }
                }
            }
            
            if (!found) {
                Files.deleteIfExists(tempPath);
                throw new IllegalArgumentException("Game item not found with ID: " + updatedItem.getItemID());
            }
            
            // Replace original file with updated file
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Game item updated successfully!");
            
        } catch (IOException e) {
            System.err.println("Error updating game item: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignore cleanup errors
            }
        }
    }
    
    public void deleteGameItem(int itemId) {
        ArrayList<GameItem> allItems = getAllGameItems();
        boolean found = false;
        
        Path originalPath = Paths.get(FILE_PATH);
        Path tempPath = Paths.get(FILE_PATH + ".tmp");
        
        try {
            // Write to temporary file
            try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
                // Write header
                writer.write("itemID,title,publisher,releaseDate,retailPrice,platform,genre,isRetro\n");
                
                // Write all items except the one to delete
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (GameItem item : allItems) {
                    if (item.getItemID() != itemId) {
                        writer.write(String.format("%d,%s,%s,%s,%.2f,%s,%s,%b%n",
                            item.getItemID(),
                            escapeCSV(item.getTitle()),
                            escapeCSV(item.getPublisher()),
                            dateFormat.format(item.getReleaseDate()),
                            item.getRetailPrice(),
                            item.getPlatform(),
                            item.getGenre(),
                            item.isRetro()
                        ));
                    } else {
                        found = true;
                    }
                }
            }
            
            if (!found) {
                Files.deleteIfExists(tempPath);
                throw new IllegalArgumentException("Game item not found with ID: " + itemId);
            }
            
            // Replace original file with updated file
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Game item deleted successfully!");
            
        } catch (IOException e) {
            System.err.println("Error deleting game item: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignore cleanup errors
            }
        }
    }
    
    public static void main(String[] args) {
        GameItemDAO dao = new GameItemDAO();
        ArrayList<GameItem> gameItems = dao.getAllGameItems();
        
        System.out.println("All Game Items:");
        for (GameItem item : gameItems) {
            System.out.println(item);
        }
    }
} 