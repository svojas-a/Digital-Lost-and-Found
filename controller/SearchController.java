package controller;
import controller.SystemController;

import model.Item;
import model.MatchResult;
import model.SearchCriteria;
import service.SearchService;
import view.SearchView;

import java.util.List;

/**
 * Controller Layer: SearchController
 *
 * Sits between the View (user input/output) and the Service (business logic).
 * Follows MVC: the View never calls the Service directly.
 *
 * Design Principles:
 *   • Single Responsibility — only orchestrates; no business logic here
 *   • Dependency Inversion  — depends on SearchService and SearchView abstractions
 */
public class SearchController {

    private final SearchService service;
    private final SearchView    view;
    private final SystemController systemController = new SystemController();

    public SearchController(SearchService service, SearchView view) {
        this.service = service;
        this.view    = view;
    }

    // ── Controller actions (called by View or external triggers) ─────────────

    /**
     * Handle a search request from the user.
     *
     * @param keyword  text to match against name/description (may be empty)
     * @param location location to filter by (may be empty)
     * @param type     "lost", "found", or "all"
     */
    public void handleSearch(String keyword, String location, String type) {
        SearchCriteria criteria = new SearchCriteria(keyword, location, type);
        List<Item> results = service.searchItems(criteria);

        if (results.isEmpty()) {
            view.showMessage("No items found for: " + criteria);
        } else {
            view.showSearchResults(results);
        }
    }

    /**
     * Trigger the full matching engine across all items in the store.
     * Results are displayed and observers are notified automatically.
     */
    public void handleFindAllMatches() {
        List<MatchResult> matches = service.findAllMatches();

        if (matches.isEmpty()) {
            view.showMessage("No matches found at this time.");
        } else {
            view.showMatchResults(matches);
            systemController.onMatchFound("User");
        }
    }

    /**
     * Trigger matching for a single newly-reported lost item.
     *
     * @param lostItem the newly added LostItem
     */
    public void handleFindMatchesForItem(Item lostItem) {
        List<MatchResult> matches = service.findMatchesForLostItem(lostItem);

        if (matches.isEmpty()) {
            view.showMessage("No matches found for item: " + lostItem.getName());
        } else {
            view.showMatchResults(matches);
            systemController.onMatchFound("User");
        }
    }
}