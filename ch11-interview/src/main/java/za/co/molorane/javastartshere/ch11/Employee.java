package za.co.molorane.javastartshere.ch11;

import java.util.Comparator;
import java.util.Objects;

/**
 * Demonstrates interview mastery:
 * - Proper equals and hashCode contract adhering to mathematical reflexivity, symmetry, and transitivity.
 * - Declarative, zero-boxing multi-field Comparator chains.
 */
public class Employee {

    private final String department;
    private final String name;
    private final double salary;

    public Employee(String department, String name, double salary) {
        this.department = Objects.requireNonNull(department, "department cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Double.compare(employee.salary, salary) == 0 &&
               department.equals(employee.department) &&
               name.equals(employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(department, name, salary);
    }

    public static final Comparator<Employee> COMPARATOR = Comparator
            .comparing(Employee::getDepartment)
            .thenComparing(Comparator.comparingDouble(Employee::getSalary).reversed())
            .thenComparing(Employee::getName);
}
