import model.*;
public class Main {
    public static void main(String[] args) {

        Item item1 = ItemFactory.createItem(
                "lost",
                1,
                "Wallet",
                "Black leather wallet",
                "2026-04-01",
                "",
                "",
                "Library",
                "Library",
                "2026-03-30"
        );

        Item item2 = ItemFactory.createItem(
                "found",
                2,
                "Phone",
                "iPhone 13",
                "2026-04-01",
                "",
                "",
                "Cafeteria",
                "Cafeteria",
                "2026-04-01"
        );

        item1.displayItem();
        item2.displayItem();

        item1.updateStatus("Searching");

        System.out.println("After status update:");
        item1.displayItem();
    }
}