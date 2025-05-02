package view;

import java.io.*;
import java.util.Scanner;

public class MenuUtils {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String ID_FILE_PATH = "data/next_ids.txt";

    public static void printMenu(String title, String[] items, boolean showZeroOption) {
        System.out.println("\n" + title);
        System.out.println("=".repeat(title.length()));
        
        for (int i = 0; i < items.length; i++) {
            System.out.printf("%d. %s%n", i + 1, items[i]);
        }
        
        if (showZeroOption) {
            System.out.println("0. Return to Previous Menu");
        }
        System.out.print("\nEnter your choice: ");
    }

    public static int getInt(int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    public static int getInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public static double getDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public static String getString() {
        return scanner.nextLine().trim();
    }

    public static String getStringOrDefault(String defaultValue) {
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    public static double getDoubleOrDefault(double defaultValue) {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default value.");
            return defaultValue;
        }
    }

    public static int getNextId(String type) {
        int nextId = 1;
        File file = new File(ID_FILE_PATH);
        
        // Create directory if it doesn't exist
        file.getParentFile().mkdirs();
        
        // Read existing IDs
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts[0].equals(type)) {
                        nextId = Integer.parseInt(parts[1]);
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading next ID: " + e.getMessage());
            }
        }

        // Write updated ID
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(type + ":" + (nextId + 1));
        } catch (IOException e) {
            System.err.println("Error writing next ID: " + e.getMessage());
        }

        return nextId;
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
} 