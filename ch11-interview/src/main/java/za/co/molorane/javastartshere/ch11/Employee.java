package za.co.molorane.javastartshere.ch11;

import java.util.Comparator;
import java.util.Objects;

/**
 * Demonstrates interview mastery from Chapter 11:
 * - Natural ordering via {@link Comparable} based on primary key {@code id}.
 * - Proper {@link #equals(Object)} and {@link #hashCode()} contract.
 * - Declarative, zero-boxing multi-column {@link Comparator} chaining mimicking SQL ORDER BY.
 */
public class Employee implements Comparable<Employee> {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String department;
    private final String city;
    private final double salary;

    public Employee(Long id, String firstName, String lastName, String department, String city, double salary) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "firstName cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "lastName cannot be null");
        this.department = Objects.requireNonNull(department, "department cannot be null");
        this.city = Objects.requireNonNull(city, "city cannot be null");
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDepartment() {
        return department;
    }

    public String getCity() {
        return city;
    }

    public double getSalary() {
        return salary;
    }

    /**
     * Natural ordering by primary key ID.
     */
    @Override
    public int compareTo(Employee other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee other)) return false;
        return Objects.equals(id, other.id) &&
                Double.compare(salary, other.salary) == 0 &&
                Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName) &&
                Objects.equals(department, other.department) &&
                Objects.equals(city, other.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, department, city, salary);
    }

    /**
     * Mimics SQL:
     * {@code SELECT * FROM employee ORDER BY department, lastName, firstName, salary DESC;}
     * Zero nested if-else statements, zero autoboxing for primitive double salary.
     */
    public static final Comparator<Employee> SQL_STYLE_COMPARATOR = Comparator
            .comparing(Employee::getDepartment)
            .thenComparing(Employee::getLastName)
            .thenComparing(Employee::getFirstName)
            .thenComparing(Comparator.comparingDouble(Employee::getSalary).reversed());
}
