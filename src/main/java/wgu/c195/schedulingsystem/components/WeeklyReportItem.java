package wgu.c195.schedulingsystem.components;

/**
 * Class to hold data about a Weekly Report Item.
 */
public class WeeklyReportItem {
    private int year;
    private int week;
    private int quantity;
    public WeeklyReportItem() {

    }
    public WeeklyReportItem(int year, int week, int quantity) {
        this.year = year;
        this.week = week;
        this.quantity = quantity;
    }
    // Setters
    public void setYear(int s) {
        this.year = s;
    }
    public void setWeek(int t) {
        this.week = t;
    }
    public void setQuantity(int i) {
        this.quantity = i;
    }
    // Getters
    public int getYear() {
        return this.year;
    }
    public int getWeek() {
        return this.week;
    }
    public int getQuantity() {
        return this.quantity;
    }
}