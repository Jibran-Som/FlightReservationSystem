//CustomDate.java
// Entity class for custom date representation
// Provides methods for date manipulation and formatting
package model;

public class CustomDate {
    private int day;
    private int month;
    private int year;

    // === Constructors ===
    public CustomDate() {
        this.day = 1;
        this.month = 1;
        this.year = 1970;
    }

    public CustomDate(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }


    // === Getters ===
    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }


    // === Setters ===
    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }


    // === Other Methods ===    
    public String toSQLDate() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static CustomDate StringToDate(String date){
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        return new CustomDate(day, month, year);
    }

    @Override
    public String toString() {
        return String.format("%02d-%02d-%04d", day, month, year);
    }



}
