package view;

import data_access.EmployeeDAO;
import gamestore.Employee;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class EmployeeMenu {
    private EmployeeDAO employeeDAO;
    private final String[] menuItems = {
        "View All Employees",
        "Add New Employee",
        "Update Employee",
        "Delete Employee",
        "Return to Main Menu"
    };

    private static final String[] COLUMN_HEADERS = {
        "ID", "Name", "Position", "Wage", "Hire Date"
    };

    private static final int[] COLUMN_WIDTHS = {
        6, 30, 20, 10, 12
    };

    public EmployeeMenu() {
        employeeDAO = new EmployeeDAO();
    }

    public void show() {
        while (true) {
            MenuUtils.printMenu("Employee Management", menuItems, false);
            int choice = MenuUtils.getInt(1, menuItems.length);

            switch (choice) {
                case 1:
                    viewAllEmployees();
                    break;
                case 2:
                    addEmployee();
                    break;
                case 3:
                    updateEmployee();
                    break;
                case 4:
                    deleteEmployee();
                    break;
                case 5:
                    return;
            }
            MenuUtils.pressEnterToContinue();
        }
    }

    private void viewAllEmployees() {
        ArrayList<Employee> employees = employeeDAO.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("\nNo employees found.");
            return;
        }

        // Sort employees by name
        Collections.sort(employees, Comparator.comparing(Employee::getEmployeeName));

        System.out.println("\nEmployees List");
        System.out.println("=============");
        
        // Print table header
        printTableHeader();

        // Print each employee in table format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Employee employee : employees) {
            System.out.printf("| %4d | %-28s | %-18s | %8.2f | %10s |%n",
                employee.getEmployeeID(),
                truncate(employee.getEmployeeName(), 28),
                truncate(employee.getPosition(), 18),
                employee.getHourlyWage(),
                dateFormat.format(employee.getHireDate())
            );
        }

        // Print table footer
        printTableFooter();
    }

    private void printTableHeader() {
        // Print top border
        printTableBorder();
        
        // Print column headers
        System.out.print("|");
        for (int i = 0; i < COLUMN_HEADERS.length; i++) {
            System.out.printf(" %-" + COLUMN_WIDTHS[i] + "s |", COLUMN_HEADERS[i]);
        }
        System.out.println();
        
        // Print bottom border
        printTableBorder();
    }

    private void printTableFooter() {
        printTableBorder();
    }

    private void printTableBorder() {
        System.out.print("+");
        for (int width : COLUMN_WIDTHS) {
            System.out.print("-".repeat(width + 2) + "+");
        }
        System.out.println();
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    private void addEmployee() {
        System.out.println("\nAdd New Employee");
        System.out.println("===============");
        
        int employeeID = MenuUtils.getInt("Enter Employee ID: ");
        System.out.print("Enter Employee Name: ");
        String employeeName = MenuUtils.getString();
        System.out.print("Enter Position: ");
        String position = MenuUtils.getString();
        double hourlyWage = MenuUtils.getDouble("Enter Hourly Wage: ");
        Date hireDate = getDateInput("Enter Hire Date (yyyy-MM-dd): ");

        try {
            Employee newEmployee = new Employee(employeeID, employeeName, position, hourlyWage, hireDate);
            if (employeeDAO.addEmployee(newEmployee)) {
                System.out.println("\nEmployee added successfully!");
            } else {
                System.out.println("\nFailed to add employee.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        System.out.println("\nUpdate Employee");
        System.out.println("===============");
        
        int employeeID = MenuUtils.getInt("Enter Employee ID to update: ");
        
        ArrayList<Employee> employees = employeeDAO.getAllEmployees();
        Employee existingEmployee = null;
        
        for (Employee emp : employees) {
            if (emp.getEmployeeID() == employeeID) {
                existingEmployee = emp;
                break;
            }
        }
        
        if (existingEmployee == null) {
            System.out.println("\nEmployee not found.");
            return;
        }

        System.out.println("\nCurrent employee details:");
        printTableHeader();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.printf("| %4d | %-28s | %-18s | %8.2f | %10s |%n",
            existingEmployee.getEmployeeID(),
            truncate(existingEmployee.getEmployeeName(), 28),
            truncate(existingEmployee.getPosition(), 18),
            existingEmployee.getHourlyWage(),
            dateFormat.format(existingEmployee.getHireDate())
        );
        printTableFooter();

        System.out.println("\nEnter new details (press Enter to keep current value):");

        System.out.print("Enter Employee Name [" + existingEmployee.getEmployeeName() + "]: ");
        String employeeName = MenuUtils.getStringOrDefault(existingEmployee.getEmployeeName());

        System.out.print("Enter Position [" + existingEmployee.getPosition() + "]: ");
        String position = MenuUtils.getStringOrDefault(existingEmployee.getPosition());

        System.out.print("Enter Hourly Wage [" + existingEmployee.getHourlyWage() + "]: ");
        double hourlyWage = MenuUtils.getDoubleOrDefault(existingEmployee.getHourlyWage());

        System.out.print("Enter Hire Date [" + dateFormat.format(existingEmployee.getHireDate()) + "]: ");
        Date hireDate = getDateOrDefault(existingEmployee.getHireDate());

        try {
            Employee updatedEmployee = new Employee(employeeID, employeeName, position, hourlyWage, hireDate);
            if (employeeDAO.updateEmployee(updatedEmployee)) {
                System.out.println("\nEmployee updated successfully!");
            } else {
                System.out.println("\nFailed to update employee.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        System.out.println("\nDelete Employee");
        System.out.println("===============");
        
        int employeeID = MenuUtils.getInt("Enter Employee ID to delete: ");
        
        if (employeeDAO.deleteEmployee(employeeID)) {
            System.out.println("\nEmployee deleted successfully!");
        } else {
            System.out.println("\nEmployee not found or could not be deleted.");
        }
    }

    private Date getDateInput(String prompt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        while (date == null) {
            System.out.print(prompt);
            String input = MenuUtils.getString();
            try {
                date = dateFormat.parse(input);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd");
            }
        }
        return date;
    }

    private Date getDateOrDefault(Date defaultDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String input = MenuUtils.getString();
        if (input.trim().isEmpty()) {
            return defaultDate;
        }
        try {
            return dateFormat.parse(input);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Using existing value.");
            return defaultDate;
        }
    }
} 