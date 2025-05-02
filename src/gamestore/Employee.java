package gamestore;

import java.io.Serializable;
import java.util.Date;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int employeeID;
    private String employeeName;
    private String position;
    private double hourlyWage;
    private Date hireDate;

    public Employee(int employeeID, String employeeName, String position, double hourlyWage, Date hireDate) {
        if (employeeID <= 0) throw new IllegalArgumentException("Employee ID must be positive");
        if (employeeName == null || employeeName.trim().isEmpty()) throw new IllegalArgumentException("Employee name cannot be null or empty");
        if (position == null || position.trim().isEmpty()) throw new IllegalArgumentException("Position cannot be null or empty");
        if (hourlyWage < 0) throw new IllegalArgumentException("Hourly wage cannot be negative");
        if (hireDate == null) throw new IllegalArgumentException("Hire date cannot be null");

        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.position = position;
        this.hourlyWage = hourlyWage;
        this.hireDate = new Date(hireDate.getTime()); // Defensive copy
    }

    // Copy constructor
    public Employee(Employee other) {
        this.employeeID = other.employeeID;
        this.employeeName = other.employeeName;
        this.position = other.position;
        this.hourlyWage = other.hourlyWage;
        this.hireDate = new Date(other.hireDate.getTime()); // Deep copy of Date
    }

    public int getEmployeeID() { return employeeID; }
    public String getEmployeeName() { return employeeName; }
    public String getPosition() { return position; }
    public double getHourlyWage() { return hourlyWage; }
    public Date getHireDate() { return new Date(hireDate.getTime()); } // Return defensive copy

    public void setEmployeeID(int employeeID) {
        if (employeeID <= 0) throw new IllegalArgumentException("Employee ID must be positive");
        this.employeeID = employeeID;
    }

    public void setEmployeeName(String employeeName) {
        if (employeeName == null || employeeName.trim().isEmpty()) throw new IllegalArgumentException("Employee name cannot be null or empty");
        this.employeeName = employeeName;
    }

    public void setPosition(String position) {
        if (position == null || position.trim().isEmpty()) throw new IllegalArgumentException("Position cannot be null or empty");
        this.position = position;
    }

    public void setHourlyWage(double hourlyWage) {
        if (hourlyWage < 0) throw new IllegalArgumentException("Hourly wage cannot be negative");
        this.hourlyWage = hourlyWage;
    }

    public void setHireDate(Date hireDate) {
        if (hireDate == null) throw new IllegalArgumentException("Hire date cannot be null");
        this.hireDate = new Date(hireDate.getTime()); // Defensive copy
    }

    @Override
    public String toString() {
        return String.format("%s - %s (ID: %d)", employeeName, position, employeeID);
    }
} 