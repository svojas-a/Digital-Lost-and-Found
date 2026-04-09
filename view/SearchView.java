package view;

import model.Item;
import model.LostItem;
import model.FoundItem;
import model.MatchResult;

import java.util.List;

public class SearchView {

    private static final String DIVIDER =
            "=========================================================";

    public void showMessage(String message) {
        System.out.println("\n[INFO] " + message);
    }

    public void showSearchResults(List<Item> items) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  SEARCH RESULTS  (" + items.size() + " item(s) found)");
        System.out.println(DIVIDER);
        for (Item item : items) {
            printItemSummary(item);
        }
        System.out.println(DIVIDER);
    }

    public void showMatchResults(List<MatchResult> matches) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  MATCH RESULTS  (" + matches.size() + " potential match(es))");
        System.out.println(DIVIDER);

        for (MatchResult match : matches) {
            System.out.println("\n  [MATCH]  Confidence: " + match.getConfidenceScore() + "%");
            System.out.println("  Reason  : " + match.getMatchReason());
            System.out.println("  --- LOST -------------------------------------------");
            printItemSummary(match.getLostItem());
            System.out.println("  --- FOUND ------------------------------------------");
            printItemSummary(match.getFoundItem());
        }

        System.out.println("\n" + DIVIDER);
    }

    public void showSearchPrompt() {
        System.out.println("\n--- Search Items ---");
        System.out.print("  Keyword  (or leave blank): ");
    }

    public void showLocationPrompt() {
        System.out.print("  Location (or leave blank): ");
    }

    public void showTypePrompt() {
        System.out.print("  Type [lost / found / all]:  ");
    }

    private void printItemSummary(Item item) {
        String type     = (item instanceof LostItem) ? "LOST" : "FOUND";
        String location = "";

        if (item instanceof LostItem)  location = ((LostItem)  item).getLostLocation();
        if (item instanceof FoundItem) location = ((FoundItem) item).getFoundLocation();

        System.out.println("  [" + type + "] ID: " + item.getItemId() +
                           " | Name: "     + item.getName() +
                           " | Status: "   + item.getStatus() +
                           " | Location: " + (location != null ? location : "N/A"));
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            System.out.println("         Desc: " + item.getDescription());
        }
    }
}