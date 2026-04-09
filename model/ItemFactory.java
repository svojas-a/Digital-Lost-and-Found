package model;
public class ItemFactory {

    public static Item createItem(String type, int itemId, String name, String description,
                                  String dateReported, String ownerName, String contactInfo,
                                  String location, String extraLocation, String extraDate) {

        if (type.equalsIgnoreCase("lost")) {
            return new LostItem(itemId, name, description, dateReported,
                    ownerName, contactInfo, location,
                    extraLocation, extraDate);

        } else if (type.equalsIgnoreCase("found")) {
            return new FoundItem(itemId, name, description, dateReported,
                    ownerName, contactInfo, location,
                    extraLocation, extraDate);
        }

        return null;
    }
}