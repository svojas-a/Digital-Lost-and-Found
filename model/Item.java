package model;
public class Item {
    protected int itemId;
    protected String name;
    protected String description;
    protected String dateReported;
    protected String status;
    protected String ownerName;
    protected String contactInfo;
    protected String location;

    public Item(int itemId, String name, String description, String dateReported,
                String ownerName, String contactInfo, String location) {

        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.dateReported = dateReported;
        this.ownerName = ownerName;
        this.contactInfo = contactInfo;
        this.location = location;

        this.status = "Reported";
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public void displayItem() {
        System.out.println("ID: " + itemId);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Date: " + dateReported);
        System.out.println("Status: " + status);
        System.out.println("Owner: " + ownerName);
        System.out.println("Contact: " + contactInfo);
        System.out.println("Location: " + location);
        System.out.println("----------------------");
    }

    public int getItemId() {
        return itemId;
    }
    public String getOwner() {
        return this.ownerName;
    }

    public String getStatus() {
        return status;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    // 🔧 MODIFICATION START: Add getter for ownerName (needed by Claim class)
    public String getOwnerName() {
        return ownerName;
    }
    // 🔧 MODIFICATION END

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    // 🔧 MODIFICATION START: Add getter for contactInfo (needed by Claim class)
    public String getContactInfo() {
        return contactInfo;
    }
    // 🔧 MODIFICATION END

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }
}