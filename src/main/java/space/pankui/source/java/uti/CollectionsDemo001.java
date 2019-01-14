package space.pankui.source.java.uti;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author pankui
 * @date 2019-01-14
 * <pre>
 *   java 集合包里算法
 * </pre>
 */
public class CollectionsDemo001 {

    public static void main(String[] args) {
        Employee[] staffs = new Employee[]{
                new Employee("Steve", 5500, LocalDate.of(1986, 7, 13)),
                new Employee("Jack", 7000, LocalDate.of(1986, 7, 5)),
                new Employee("Karl", 5000, LocalDate.of(1985, 11, 2)),
                new Employee("Jason", 7000, LocalDate.of(1980, 8, 12)),
        };
        //before sort
        System.out.println("Original:	" + Arrays.toString(staffs));

        //create a backup arraylist and  shuffled it
        ArrayList<Employee> bkList1 = new ArrayList<Employee>(Arrays.asList(staffs));
        Collections.shuffle(bkList1);
        System.out.println("Shuffled:	" + bkList1);


        //sorted it by natural ordering(compare date)
        ArrayList<Employee> bkList2 = new ArrayList<Employee>(Arrays.asList(staffs));
        Collections.sort(bkList2);
        System.out.println("date order:	" + bkList2);

        //sorted it by name via providing a Comparator
        ArrayList<Employee> bkList3 = new ArrayList<Employee>(Arrays.asList(staffs));
        Collections.sort(bkList3, new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                // TODO Auto-generated method stub
                return e1.getName().compareTo(e2.getName());
            }
        });
        System.out.println("name order:	" + bkList3);

        //sort based on bkList3
        //thus we get sorted by name and then by salary
        //check if it is a stable sort
        Collections.sort(bkList3, new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {

                return Double.valueOf(e1.getSalary()).
                        compareTo(Double.valueOf(e2.getSalary()));
            }
        });
        System.out.println("salary order:	" + bkList3);


    }
}

/**
 * a class to descript employee
 * origin by the book 《Core Java,Volume I:Fundamentals》
 *
 * @version 1.1 2013-08-07
 */
class Employee implements Comparable<Employee> {
    /**
     * @param name   name to set
     * @param salary salary to set
     * @param hireDay   month day  to create a GregorianCalendar
     */
    public Employee(String name, double salary,LocalDate hireDay) {
        this.name = name;
        this.salary = salary;
        this.hireDay = hireDay;
        setId();
    }

    public void raiseSalary(double percent) {
        double raise = salary * percent / 100;
        salary += raise;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getHireday() {
        return hireDay;
    }

    public void setHireday(LocalDate hireDay) {
        this.hireDay = hireDay;
    }

    public int getId() {
        return id;
    }

    private void setId() {
        this.id = nextId;
        nextId++;
    }

    /**
     * sort by hireDay
     */
    @Override
    public int compareTo(Employee o) {
        // TODO Auto-generated method stub
        return this.hireDay.compareTo(o.getHireday());
    }

    @Override
    public String toString() {

        return "[" + name + "," + getSalary() + "," + getHireday() + "]";
    }

    private String name;
    private double salary;
    private LocalDate hireDay;
    private int id;
    private static int nextId = 1;
}
