package data_access;

import gamestore.Employee;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmployeeDAO {
    private static final String FILE_PATH = Paths.get("src", "data", "employees.csv").toString();
    private static final String HEADER = "EmployeeID,Position,HourlyWage,HireDate,EmployeeName";
    
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.err.println("Error: Employee data file not found at: " + FILE_PATH);
            return employees;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip header
            if (line == null) {
                System.err.println("Warning: Employee file is empty");
                return employees;
            }
            
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = line.split(",");
                    if (values.length < 5) {
                        System.err.println("Warning: Skipping invalid line: " + line);
                        continue;
                    }
                    
                    int employeeID = Integer.parseInt(values[0].trim());
                    String position = values[1].trim();
                    double hourlyWage = Double.parseDouble(values[2].trim());
                    Date hireDate = dateFormat.parse(values[3].trim());
                    String employeeName = values[4].trim();
                
                    Employee employee = new Employee(employeeID, employeeName, position, hourlyWage, hireDate);
                    employees.add(employee);
                } catch (Exception e) {
                    System.err.println("Warning: Error parsing line: " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading employees file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return employees;
    }

    public boolean addEmployee(Employee employee) {
        ensureFileExists();
        ArrayList<Employee> employees = getAllEmployees();
        
        // Check if employee ID already exists
        for (Employee e : employees) {
            if (e.getEmployeeID() == employee.getEmployeeID()) {
                System.err.println("Error: Employee with ID " + employee.getEmployeeID() + " already exists");
                return false;
            }
        }
        
        // Add new employee
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(String.format("%n%d,%s,%.2f,%s,%s",
                employee.getEmployeeID(),
                employee.getPosition(),
                employee.getHourlyWage(),
                new SimpleDateFormat("yyyy-MM-dd").format(employee.getHireDate()),
                employee.getEmployeeName()));
            return true;
        } catch (IOException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        ArrayList<Employee> employees = getAllEmployees();
        boolean found = false;
        
        // Find and update employee
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeID() == employee.getEmployeeID()) {
                employees.set(i, employee);
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.err.println("Error: Employee with ID " + employee.getEmployeeID() + " not found");
            return false;
        }
        
        return writeAllEmployees(employees);
    }

    public boolean deleteEmployee(int employeeID) {
        ArrayList<Employee> employees = getAllEmployees();
        boolean removed = employees.removeIf(e -> e.getEmployeeID() == employeeID);
        
        if (!removed) {
            System.err.println("Error: Employee with ID " + employeeID + " not found");
            return false;
        }
        
        return writeAllEmployees(employees);
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
                    fw.write(HEADER);
                }
            } catch (IOException e) {
                System.err.println("Error creating employee file: " + e.getMessage());
            }
        }
    }

    private boolean writeAllEmployees(ArrayList<Employee> employees) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            // Write header
            fw.write(HEADER);
            
            // Write employees
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Employee e : employees) {
                fw.write(String.format("%n%d,%s,%.2f,%s,%s",
                    e.getEmployeeID(),
                    e.getPosition(),
                    e.getHourlyWage(),
                    dateFormat.format(e.getHireDate()),
                    e.getEmployeeName()));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing employees to file: " + e.getMessage());
            return false;
        }
    }
    
    public static void main(String[] args) {
        EmployeeDAO dao = new EmployeeDAO();
        ArrayList<Employee> employees = dao.getAllEmployees();
        
        System.out.println("All Employees:");
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }
} 