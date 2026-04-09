package model;
public class FoundItem extends Item {

    private String foundLocation;
    private String dateFound;

    public FoundItem(int itemId, String name, String description, String dateReported,
                     String ownerName, String contactInfo, String location,
                     String foundLocation, String dateFound) {

        super(itemId, name, description, dateReported, ownerName, contactInfo, location);

        this.foundLocation = foundLocation;
        this.dateFound = dateFound;
    }
    public String getFoundLocation() {
        return foundLocation;
    }

    public String getDateFound() {
        return dateFound;
    }

    @Override
    public void displayItem() {
        super.displayItem();
        System.out.println("Found Location: " + foundLocation);
        System.out.println("Date Found: " + dateFound);
        System.out.println("======================");
    }
}