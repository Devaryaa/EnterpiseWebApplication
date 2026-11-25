import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab Task 3: Object Serialization and Deserialization
 *
 * - Employee class holds employee ID, name, department and salary.
 * - Multiple Employee objects are serialized to a file using ObjectOutputStream.
 * - The objects are read back (deserialized) using ObjectInputStream.
 */
public class EmployeeSerialization {

    private static final String DATA_FILE = "employees.ser";

    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1001, "Rahul Mehta", "Engineering", 85000.00));
        employees.add(new Employee(1002, "Anjali Singh", "Human Resources", 62000.00));
        employees.add(new Employee(1003, "Vikram Rao", "Finance", 74500.50));
        employees.add(new Employee(1004, "Kavya Nair", "Marketing", 58000.00));
        employees.add(new Employee(1005, "Arjun Gupta", "Engineering", 91000.75));

        System.out.println("=== Serializing employees using ObjectOutputStream ===");
        serializeEmployees(employees, DATA_FILE);

        System.out.println("\n=== Deserializing employees using ObjectInputStream ===");
        List<Employee> restored = deserializeEmployees(DATA_FILE);
        printEmployees(restored);
    }

    // ------------------------------------------------------------------
    // Serialization: write each Employee object to the file
    // ------------------------------------------------------------------
    static void serializeEmployees(List<Employee> employees, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeInt(employees.size());     // store the count first
            for (Employee emp : employees) {
                out.writeObject(emp);           // then each object
            }
            System.out.println(employees.size() + " employee objects serialized to " + fileName);
        } catch (IOException e) {
            System.err.println("Serialization failed: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Deserialization: read the Employee objects back from the file
    // ------------------------------------------------------------------
    static List<Employee> deserializeEmployees(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                Object obj = in.readObject();
                if (obj instanceof Employee) {
                    employees.add((Employee) obj);
                }
            }
            System.out.println(employees.size() + " employee objects deserialized from " + fileName);
        } catch (FileNotFoundException e) {
            System.err.println("Data file not found: " + fileName);
        } catch (EOFException e) {
            System.err.println("Unexpected end of file; data may be incomplete.");
        } catch (InvalidClassException e) {
            System.err.println("Class version mismatch: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Employee class not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Deserialization failed: " + e.getMessage());
        }
        return employees;
    }

    static void printEmployees(List<Employee> employees) {
        System.out.println();
        System.out.printf("%-6s %-15s %-17s %12s%n", "ID", "Name", "Department", "Salary");
        System.out.println("-------------------------------------------------------");
        double total = 0;
        for (Employee e : employees) {
            System.out.println(e);
            total += e.getSalary();
        }
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-40s %12.2f%n", "Total salary:", total);
    }
}

/**
 * Employee model. Implements Serializable so its objects can be written
 * to and read from a file with object streams.
 */
class Employee implements Serializable {

    // Version ID: keeps saved files readable if the class is recompiled
    private static final long serialVersionUID = 1L;

    private final int employeeId;
    private final String name;
    private final String department;
    private final double salary;

    Employee(int employeeId, String name, String department, double salary) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public int getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return String.format("%-6d %-15s %-17s %12.2f", employeeId, name, department, salary);
    }
}
