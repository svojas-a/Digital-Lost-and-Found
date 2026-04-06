public class LostItem extends Item {

    private String lostLocation;
    private String dateLost;

    public LostItem(int itemId, String name, String description, String dateReported,
                    String ownerName, String contactInfo, String location,
                    String lostLocation, String dateLost) {

        super(itemId, name, description, dateReported, ownerName, contactInfo, location);

        this.lostLocation = lostLocation;
        this.dateLost = dateLost;
    }

    @Override
    public void displayItem() {
        super.displayItem();
        System.out.println("Lost Location: " + lostLocation);
        System.out.println("Date Lost: " + dateLost);
        System.out.println("======================");
    }
}