package model;

/**
 * Model: Holds the filter criteria used for searching items.
 * Follows the Single Responsibility Principle — only carries search parameters.
 */
public class SearchCriteria {

    private String keyword;    // matches against name or description
    private String location;   // matches against lost/found location
    private String itemType;   // "lost", "found", or "all"

    public SearchCriteria(String keyword, String location, String itemType) {
        this.keyword  = (keyword  != null) ? keyword.trim().toLowerCase()  : "";
        this.location = (location != null) ? location.trim().toLowerCase() : "";
        this.itemType = (itemType != null) ? itemType.trim().toLowerCase() : "all";
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getKeyword()  { return keyword;  }
    public String getLocation() { return location; }
    public String getItemType() { return itemType; }

    public boolean hasKeyword()  { return !keyword.isEmpty();  }
    public boolean hasLocation() { return !location.isEmpty(); }

    @Override
    public String toString() {
        return "SearchCriteria{keyword='" + keyword +
               "', location='" + location +
               "', type='" + itemType + "'}";
    }
}