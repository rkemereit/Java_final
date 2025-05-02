package view;

import data_access.GameItemDAO;
import gamestore.GameGenre;
import gamestore.GameItem;
import gamestore.GamePlatform;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameItemMenu implements Menu {
    private static final String[] MENU_ITEMS = {
        "View All Game Items",
        "Add New Game Item",
        "Update Game Item",
        "Delete Game Item"
    };

    private static final String[] COLUMN_HEADERS = {
        "ID", "Title", "Publisher", "Release", "Price", "Platform", "Genre", "Retro"
    };

    private static final int[] COLUMN_WIDTHS = {
        4, 25, 15, 10, 8, 10, 10, 5
    };

    private final GameItemDAO gameItemDAO;

    public GameItemMenu() {
        gameItemDAO = new GameItemDAO();
    }

    @Override
    public void show() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println(" ".repeat(20) + "Game Item Management");
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
                    viewAllGameItems();
                    break;
                case 2:
                    addNewGameItem();
                    break;
                case 3:
                    updateGameItem();
                    break;
                case 4:
                    deleteGameItem();
                    break;
                case 0:
                    return;
            }
            MenuUtils.pressEnterToContinue();
        }
    }

    private void viewAllGameItems() {
        ArrayList<GameItem> gameItems = gameItemDAO.getAllGameItems();
        Collections.sort(gameItems, Comparator.comparing(GameItem::getTitle));

        System.out.println("\n" + "=".repeat(95));
        System.out.println(" ".repeat(40) + "Game Items");
        System.out.println("=".repeat(95));

        if (gameItems.isEmpty()) {
            System.out.println("\nNo game items found in the system.");
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
            COLUMN_WIDTHS[2] + "s | %-" + COLUMN_WIDTHS[3] + "s | %" + 
            COLUMN_WIDTHS[4] + "s | %-" + COLUMN_WIDTHS[5] + "s | %-" + 
            COLUMN_WIDTHS[6] + "s | %-" + COLUMN_WIDTHS[7] + "s |%n",
            COLUMN_HEADERS[0], COLUMN_HEADERS[1], COLUMN_HEADERS[2], 
            COLUMN_HEADERS[3], COLUMN_HEADERS[4], COLUMN_HEADERS[5], 
            COLUMN_HEADERS[6], COLUMN_HEADERS[7]);
        printTableDivider(totalWidth);

        // Print data rows
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (GameItem item : gameItems) {
            System.out.printf("| %"+COLUMN_WIDTHS[0]+"d | %-"+COLUMN_WIDTHS[1]+"s | %-"+
                COLUMN_WIDTHS[2]+"s | %-"+COLUMN_WIDTHS[3]+"s | %"+
                COLUMN_WIDTHS[4]+".2f | %-"+COLUMN_WIDTHS[5]+"s | %-"+
                COLUMN_WIDTHS[6]+"s | %-"+COLUMN_WIDTHS[7]+"s |%n",
                item.getItemID(),
                truncate(item.getTitle(), COLUMN_WIDTHS[1]),
                truncate(item.getPublisher(), COLUMN_WIDTHS[2]),
                dateFormat.format(item.getReleaseDate()),
                item.getRetailPrice(),
                truncate(item.getPlatform().toString(), COLUMN_WIDTHS[5]),
                truncate(item.getGenre().toString(), COLUMN_WIDTHS[6]),
                item.isRetro() ? "Yes" : "No"
            );
        }
        printTableDivider(totalWidth);
    }

    private void printTableDivider(int width) {
        System.out.println("-".repeat(width));
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    private void addNewGameItem() {
        System.out.println("\n=== Add New Game Item ===\n");
        
        System.out.print("Enter game title: ");
        String title = MenuUtils.getString();
        
        System.out.print("Enter publisher: ");
        String publisher = MenuUtils.getString();
        
        Date releaseDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        while (releaseDate == null) {
            System.out.print("Enter release date (YYYY-MM-DD): ");
            try {
                releaseDate = dateFormat.parse(MenuUtils.getString());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD");
            }
        }
        
        double retailPrice = 0;
        while (retailPrice <= 0) {
            System.out.print("Enter retail price: ");
            try {
                retailPrice = Double.parseDouble(MenuUtils.getString());
                if (retailPrice <= 0) {
                    System.out.println("Price must be greater than 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Please enter a number");
            }
        }

        System.out.println("\nAvailable Platforms:");
        for (GamePlatform platform : GamePlatform.values()) {
            System.out.println("- " + platform);
        }
        GamePlatform platform = null;
        while (platform == null) {
            System.out.print("Enter platform: ");
            try {
                platform = GamePlatform.valueOf(MenuUtils.getString().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid platform. Please choose from the list above");
            }
        }
        
        System.out.println("\nAvailable Genres:");
        for (GameGenre genre : GameGenre.values()) {
            System.out.println("- " + genre);
        }
        GameGenre genre = null;
        while (genre == null) {
            System.out.print("Enter genre: ");
            try {
                genre = GameGenre.valueOf(MenuUtils.getString().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid genre. Please choose from the list above");
            }
        }
        
        System.out.print("Is this a retro game? (y/n): ");
        boolean isRetro = MenuUtils.getString().toLowerCase().startsWith("y");

        try {
            int itemId = gameItemDAO.getNextGameId();
            GameItem newGame = new GameItem(itemId, title, publisher, releaseDate, retailPrice, platform, genre, isRetro);
            gameItemDAO.addGameItem(newGame);
            System.out.println("\nGame added successfully!");
        } catch (Exception e) {
            System.err.println("Error adding game: " + e.getMessage());
        }
    }

    private void updateGameItem() {
        System.out.println("\n=== Update Game Item ===\n");
        
        // First show all games
        viewAllGameItems();
        
        System.out.print("\nEnter the ID of the game to update: ");
        int itemId = MenuUtils.getInt(1, Integer.MAX_VALUE);
        
        // Find the game
        ArrayList<GameItem> games = gameItemDAO.getAllGameItems();
        GameItem gameToUpdate = null;
        for (GameItem game : games) {
            if (game.getItemID() == itemId) {
                gameToUpdate = game;
                break;
            }
        }
        
        if (gameToUpdate == null) {
            System.out.println("No game found with ID: " + itemId);
            return;
        }
        
        // Show current values and get updates
        System.out.println("\nCurrent values (press Enter to keep current value):");
        
        System.out.printf("Title [%s]: ", gameToUpdate.getTitle());
        String title = MenuUtils.getString();
        title = title.isEmpty() ? gameToUpdate.getTitle() : title;
        
        System.out.printf("Publisher [%s]: ", gameToUpdate.getPublisher());
        String publisher = MenuUtils.getString();
        publisher = publisher.isEmpty() ? gameToUpdate.getPublisher() : publisher;
        
        Date releaseDate = gameToUpdate.getReleaseDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        boolean validDate = false;
        while (!validDate) {
            System.out.printf("Release date [%s] (YYYY-MM-DD): ", dateFormat.format(releaseDate));
            String newDate = MenuUtils.getString();
            if (newDate.isEmpty()) {
                validDate = true;
            } else {
                try {
                    releaseDate = dateFormat.parse(newDate);
                    validDate = true;
                } catch (Exception e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD");
                }
            }
        }
        
        double retailPrice = gameToUpdate.getRetailPrice();
        boolean validPrice = false;
        while (!validPrice) {
            System.out.printf("Retail price [%.2f]: ", retailPrice);
            String newPrice = MenuUtils.getString();
            if (newPrice.isEmpty()) {
                validPrice = true;
            } else {
                try {
                    double tempPrice = Double.parseDouble(newPrice);
                    if (tempPrice > 0) {
                        retailPrice = tempPrice;
                        validPrice = true;
                    } else {
                        System.out.println("Price must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price format. Please enter a number");
                }
            }
        }

        GamePlatform platform = gameToUpdate.getPlatform();
        System.out.println("\nAvailable Platforms:");
        for (GamePlatform p : GamePlatform.values()) {
            System.out.println("- " + p);
        }
        boolean validPlatform = false;
        while (!validPlatform) {
            System.out.printf("Platform [%s]: ", platform);
            String newPlatform = MenuUtils.getString();
            if (newPlatform.isEmpty()) {
                validPlatform = true;
            } else {
                try {
                    platform = GamePlatform.valueOf(newPlatform.toUpperCase());
                    validPlatform = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid platform. Please choose from the list above");
                }
            }
        }
        
        GameGenre genre = gameToUpdate.getGenre();
        System.out.println("\nAvailable Genres:");
        for (GameGenre g : GameGenre.values()) {
            System.out.println("- " + g);
        }
        boolean validGenre = false;
        while (!validGenre) {
            System.out.printf("Genre [%s]: ", genre);
            String newGenre = MenuUtils.getString();
            if (newGenre.isEmpty()) {
                validGenre = true;
            } else {
                try {
                    genre = GameGenre.valueOf(newGenre.toUpperCase());
                    validGenre = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid genre. Please choose from the list above");
                }
            }
        }
        
        System.out.printf("Is this a retro game? (y/n) [%s]: ", gameToUpdate.isRetro() ? "y" : "n");
        String retro = MenuUtils.getString();
        boolean isRetro = retro.isEmpty() ? gameToUpdate.isRetro() : retro.toLowerCase().startsWith("y");
        
        try {
            GameItem updatedGame = new GameItem(itemId, title, publisher, releaseDate, retailPrice, platform, genre, isRetro);
            gameItemDAO.updateGameItem(updatedGame);
            System.out.println("\nGame updated successfully!");
        } catch (Exception e) {
            System.err.println("Error updating game: " + e.getMessage());
        }
    }
    
    private void deleteGameItem() {
        System.out.println("\n=== Delete Game Item ===\n");
        
        // First show all games
        viewAllGameItems();
        
        System.out.print("\nEnter the ID of the game to delete: ");
        int itemId = MenuUtils.getInt(1, Integer.MAX_VALUE);
        
        // Confirm deletion
        System.out.print("Are you sure you want to delete this game? (y/n): ");
        if (MenuUtils.getString().toLowerCase().startsWith("y")) {
            try {
                gameItemDAO.deleteGameItem(itemId);
            } catch (Exception e) {
                System.err.println("Error deleting game: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
} 