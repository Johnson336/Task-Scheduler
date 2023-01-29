package wgu.c195.schedulingsystem.components;

import java.time.LocalDateTime;

/**
 * Class to hold data about a Timestamp. Used in all other component classes.
 */
public class Timestamp {
    protected LocalDateTime create_date;
    protected String created_by;
    protected LocalDateTime last_update;
    protected String last_updated_by;

    /**
     * Basic constructor. Initializes everything to default values. Create and update times are now() and created
     * and last modified are System.
     */
    public Timestamp() {
        LocalDateTime now = LocalDateTime.now();
        this.create_date = now;
        this.created_by = "System";
        this.last_update = now;
        this.last_updated_by = "System";
    }

    /**
     * Basic constructor to set the create and update text to a specific user.
     * @param name User who created this object.
     */
    public Timestamp(String name) {
        LocalDateTime now = LocalDateTime.now();
        this.create_date = now;
        this.created_by = name;
        this.last_update = now;
        this.last_updated_by = name;
    }

    /**
     * Full constructor for building fully-fleshed Timestamps.
     * @param create Creation time.
     * @param created_by Created by username.
     * @param update Update time.
     * @param updated_by Updated by username.
     */
    public Timestamp(LocalDateTime create, String created_by, LocalDateTime update, String updated_by) {
        this.create_date = create;
        this.created_by = created_by;
        this.last_update = update;
        this.last_updated_by = updated_by;
    }

    /**
     * Function to 'touch' an element, updating the update time.
     */
    public void touch() {
        // Whenever this element is touched, update the timestamp to be time now.
        this.last_update = LocalDateTime.now();
        // Update the last user to touch this element.
        // Defaulting to System
        this.last_updated_by = "System";
    }

    /**
     * Function for a user to 'touch' an element, updating the time as well as last updated user.
     * @param name Username who is updating this element.
     */
    public void touch(String name) {
        // Touch this element by a user
        this.last_update = LocalDateTime.now();
        this.last_updated_by = name;
    }

    public void setCreateDate(LocalDateTime d) {
        this.create_date = d;
    }
    public void setCreatedBy(String s) {
        this.created_by = s;
    }
    public void setUpdatedDate(LocalDateTime d) {
        this.last_update = d;
    }
    public void setUpdatedBy(String s) {
        this.last_updated_by = s;
    }

    public LocalDateTime getCreateDate() {
        return this.create_date;
    }
    public String getCreatedBy() {
        return this.created_by;
    }
    public LocalDateTime getUpdatedDate() {
        return this.last_update;
    }
    public String getUpdatedBy() {
        return this.last_updated_by;
    }
}
