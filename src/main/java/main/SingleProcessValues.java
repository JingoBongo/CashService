package main;

public class SingleProcessValues {
    private static int idCounter = 0;
    public String id;
    public String name;
    private String salary;
    private String daysOff;
    public String status;

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDaysOff() {
        return daysOff;
    }

    public void setDaysOff(String daysOff) {
        this.daysOff = daysOff;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SingleProcessValues(String name){
        this.name = name;
        this.id = MainRunner.servicePort+ String.valueOf(idCounter++);
    }
    public static void setIdCounter(int idCounter) {
        SingleProcessValues.idCounter = idCounter;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
