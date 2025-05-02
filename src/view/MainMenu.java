package view;

public class MainMenu {
    private GameItemMenu gameItemMenu;
    private CustomerMenu customerMenu;
    private EmployeeMenu employeeMenu;
    private TransactionMenu transactionMenu;
    private StoreMenu storeMenu;
    private final String[] menuItems = {
        "Game Items",
        "Customers",
        "Employees",
        "Transactions",
        "Store Management",
        "Exit"
    };

    public MainMenu() {
        gameItemMenu = new GameItemMenu();
        customerMenu = new CustomerMenu();
        employeeMenu = new EmployeeMenu();
        transactionMenu = new TransactionMenu();
        storeMenu = new StoreMenu();
    }

    public void show() {
        while (true) {
            MenuUtils.printMenu("Game Store Management System", menuItems, false);
            int choice = MenuUtils.getInt(1, menuItems.length);

            switch (choice) {
                case 1:
                    gameItemMenu.show();
                    break;
                case 2:
                    customerMenu.show();
                    break;
                case 3:
                    employeeMenu.show();
                    break;
                case 4:
                    transactionMenu.show();
                    break;
                case 5:
                    storeMenu.show();
                    break;
                case 6:
                    System.out.println("\nThank you for using the Game Store Management System!");
                    return;
            }
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Starting Game Store Management System...");
            MainMenu mainMenu = new MainMenu();
            mainMenu.show();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            System.err.println("Please contact system administrator.");
            e.printStackTrace();
        } finally {
            System.out.println("\nClosing Game Store Management System.");
        }
    }
}