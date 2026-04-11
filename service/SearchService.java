package service;

import model.Item;
import model.LostItem;
import model.FoundItem;
import model.SearchCriteria;
import model.MatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer: SearchService
 *
 * Responsibilities:
 *   1. Filter items by SearchCriteria  (searchLostItems / searchFoundItems)
 *   2. Detect matches using a pluggable MatchingStrategy  (Strategy Pattern)
 *   3. Notify registered observers when matches are found  (Observer Pattern)
 *
 * Design Principles applied:
 *   • Single Responsibility — only handles search/match logic
 *   • Open/Closed           — new strategies via MatchingStrategy, no edits here
 *   • Dependency Inversion  — depends on MatchingStrategy abstraction, not a concrete class
 */
public class SearchService {

    // Strategy (injected, swappable)
    private MatchingStrategy matchingStrategy;

    // Observer list
    private final List<MatchObserver> observers = new ArrayList<>();

    // In-memory item store (handed off by Person 1's storage; injected here)
    private final List<Item> itemStore;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param itemStore        reference to Person 1's item list / repository
     * @param matchingStrategy the algorithm to use for matching
     */
    public SearchService(List<Item> itemStore, MatchingStrategy matchingStrategy) {
        if (itemStore == null)        throw new IllegalArgumentException("itemStore cannot be null");
        if (matchingStrategy == null) throw new IllegalArgumentException("matchingStrategy cannot be null");
        this.itemStore        = itemStore;
        this.matchingStrategy = matchingStrategy;
    }

    // ── Strategy setter (allows hot-swapping the algorithm at runtime) ────────

    public void setMatchingStrategy(MatchingStrategy strategy) {
        this.matchingStrategy = strategy;
    }

    // ── Observer management ───────────────────────────────────────────────────

    public void addObserver(MatchObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MatchObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(List<MatchResult> matches) {
        for (MatchObserver obs : observers) {
            obs.onMatchesFound(matches);
        }
    }

    // ── Public Search API ─────────────────────────────────────────────────────

    /**
     * Search lost items using the provided criteria.
     */
    public List<Item> searchLostItems(SearchCriteria criteria) {
        return itemStore.stream()
                .filter(item -> item instanceof LostItem)
                .filter(item -> matchesCriteria(item, criteria))
                .collect(Collectors.toList());
    }

    /**
     * Search found items using the provided criteria.
     */
    public List<Item> searchFoundItems(SearchCriteria criteria) {
        return itemStore.stream()
                .filter(item -> item instanceof FoundItem)
                .filter(item -> matchesCriteria(item, criteria))
                .collect(Collectors.toList());
    }

    /**
     * Search all items (lost + found) by criteria.
     * Respects criteria.getItemType() to narrow to "lost" or "found".
     */
    public List<Item> searchItems(SearchCriteria criteria) {
        String type = criteria.getItemType();
        if ("lost".equals(type))  return searchLostItems(criteria);
        if ("found".equals(type)) return searchFoundItems(criteria);

        // "all"
        List<Item> results = new ArrayList<>();
        results.addAll(searchLostItems(criteria));
        results.addAll(searchFoundItems(criteria));
        return results;
    }

    /**
     * Run the matching engine across ALL lost vs found items in the store.
     * Notifies observers with the result.
     *
     * @return list of MatchResult sorted by confidence score (highest first)
     */
    public List<MatchResult> findAllMatches() {
        List<Item> lost  = itemStore.stream()
                .filter(i -> i instanceof LostItem)
                .collect(Collectors.toList());

        List<Item> found = itemStore.stream()
                .filter(i -> i instanceof FoundItem)
                .collect(Collectors.toList());

        List<MatchResult> matches = matchingStrategy.findMatches(lost, found);

        if (!matches.isEmpty()) {
            // 🔧 MODIFICATION START: Update items to "Matched" status before notifying
            for (MatchResult match : matches) {
                match.getLostItem().updateStatus("Matched");
                match.getFoundItem().updateStatus("Matched");
            }
            // 🔧 MODIFICATION END
            
            notifyObservers(matches);
        }

        return matches;
    }

    /**
     * Find matches only for a specific lost item (useful after a new item is reported).
     */
    public List<MatchResult> findMatchesForLostItem(Item lostItem) {
        List<Item> lostList  = List.of(lostItem);
        List<Item> foundList = itemStore.stream()
                .filter(i -> i instanceof FoundItem)
                .collect(Collectors.toList());

        List<MatchResult> matches = matchingStrategy.findMatches(lostList, foundList);

        if (!matches.isEmpty()) {
            // 🔧 MODIFICATION START: Update items to "Matched" status before notifying
            for (MatchResult match : matches) {
                match.getLostItem().updateStatus("Matched");
                match.getFoundItem().updateStatus("Matched");
            }
            // 🔧 MODIFICATION END
            
            notifyObservers(matches);
        }

        return matches;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean matchesCriteria(Item item, SearchCriteria criteria) {
        if (criteria.hasKeyword()) {
            String kw   = criteria.getKeyword();
            String name = item.getName()        != null ? item.getName().toLowerCase()        : "";
            String desc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
            if (!name.contains(kw) && !desc.contains(kw)) {
                return false;
            }
        }

        if (criteria.hasLocation()) {
            String loc = getItemLocation(item).toLowerCase();
            if (!loc.contains(criteria.getLocation())) {
                return false;
            }
        }

        return true;
    }

    private String getItemLocation(Item item) {
        if (item instanceof LostItem) {
            String l = ((LostItem) item).getLostLocation();
            return l != null ? l : "";
        }
        if (item instanceof FoundItem) {
            String l = ((FoundItem) item).getFoundLocation();
            return l != null ? l : "";
        }
        return "";
    }
}