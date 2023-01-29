package wgu.c195.schedulingsystem.components;

/**
 * Class to hold data about a ReportItem
 */
public class ReportItem {
    private String month;
    private String type;
    private int quantity;
    public ReportItem() {

    }
    public ReportItem(String month, String type, int quantity) {
        this.month = month;
        this.type = type;
        this.quantity = quantity;
    }
    // Setters
    public void setMonth(String s) {
        this.month = s;
    }
    public void setType(String t) {
        this.type = t;
    }
    public void setQuantity(int i) {
        this.quantity = i;
    }
    // Getters
    public String getMonth() {
        return this.month;
    }
    public String getType() {
        return this.type;
    }
    public int getQuantity() {
        return this.quantity;
    }
}
