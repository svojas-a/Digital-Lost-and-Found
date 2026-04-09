import controller.SearchController;
import model.Item;
import model.LostItem;
import model.FoundItem;
import model.MatchResult;
import service.BasicMatchingStrategy;
import service.MatchObserver;
import service.SearchService;
import view.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchMain {

    public static void main(String[] args) {

        // 1. Sample item store
        List<Item> itemStore = buildSampleStore();

        // 2. MVC setup
        SearchService service = new SearchService(itemStore, new BasicMatchingStrategy());
        SearchView view = new SearchView();
        SearchController controller = new SearchController(service, view);

        // 3. Observer (Person 4 hook)
        service.addObserver(new MatchObserver() {
            @Override
            public void onMatchesFound(List<MatchResult> matches) {
                System.out.println("\n[OBSERVER] " + matches.size() +
                        " match(es) detected — Notification System triggered.");
            }
        });

        // 4. Search by keyword
        System.out.println("\n===== SEARCH: keyword 'wallet' =====");
        controller.handleSearch("wallet", "", "all");

        // 5. Search by location
        System.out.println("\n===== SEARCH: location 'library' =====");
        controller.handleSearch("", "library", "found");

        // 6. Run matching engine
        System.out.println("\n===== RUN MATCHING ENGINE =====");
        controller.handleFindAllMatches();

        // 7. Match for new item
        System.out.println("\n===== MATCH FOR NEW LOST ITEM =====");

        LostItem newItem = new LostItem(
                4,
                "Blue Backpack",
                "navy blue backpack with laptop sleeve",
                "2024-04-08",
                "Swathi",
                "9999999999",
                "Campus",
                "Library",
                "2024-04-07"
        );

        newItem.updateStatus("Reported");
        controller.handleFindMatchesForItem(newItem);
    }

    // Sample data
    private static List<Item> buildSampleStore() {

        List<Item> store = new ArrayList<>();

        // Lost Items
        LostItem l1 = new LostItem(
                1,
                "Black Wallet",
                "black leather wallet with student ID inside",
                "2024-04-01",
                "Swathi",
                "9876543210",
                "Campus",
                "Main Gate",
                "2024-03-31"
        );

        LostItem l2 = new LostItem(
                2,
                "Silver Watch",
                "silver wristwatch with metal strap",
                "2024-04-03",
                "Rahul",
                "9123456780",
                "Campus",
                "Cafeteria",
                "2024-04-02"
        );

        LostItem l3 = new LostItem(
                3,
                "Blue Backpack",
                "blue backpack containing notebooks and charger",
                "2024-04-06",
                "Ananya",
                "9988776655",
                "Campus",
                "Library",
                "2024-04-05"
        );

        // Found Items
        FoundItem f1 = new FoundItem(
                101,
                "Wallet",
                "found a black leather wallet near gate",
                "2024-04-02",
                "Security",
                "9000000001",
                "Campus",
                "Main Gate",
                "2024-04-01"
        );

        FoundItem f2 = new FoundItem(
                102,
                "Watch",
                "silver watch found near cafeteria exit",
                "2024-04-04",
                "Staff",
                "9000000002",
                "Campus",
                "Cafeteria",
                "2024-04-03"
        );

        FoundItem f3 = new FoundItem(
                103,
                "Backpack",
                "navy blue backpack found in library",
                "2024-04-07",
                "Student",
                "9000000003",
                "Campus",
                "Library",
                "2024-04-06"
        );

        // Add all to store
        store.add(l1);
        store.add(l2);
        store.add(l3);
        store.add(f1);
        store.add(f2);
        store.add(f3);

        return store;
    }
}