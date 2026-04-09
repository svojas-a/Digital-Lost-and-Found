package model;

import model.Item;

/**
 * Model: Represents a single match between a LostItem and a FoundItem.
 * Carries a confidence score (0–100) and the reason the match was made.
 *
 * Design: Immutable value object (no setters after construction).
 */
public class MatchResult {

    private final Item lostItem;
    private final Item foundItem;
    private final int  confidenceScore; // 0 – 100
    private final String matchReason;

    public MatchResult(Item lostItem, Item foundItem,
                       int confidenceScore, String matchReason) {
        this.lostItem        = lostItem;
        this.foundItem       = foundItem;
        this.confidenceScore = confidenceScore;
        this.matchReason     = matchReason;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Item   getLostItem()        { return lostItem;        }
    public Item   getFoundItem()       { return foundItem;       }
    public int    getConfidenceScore() { return confidenceScore; }
    public String getMatchReason()     { return matchReason;     }

    @Override
    public String toString() {
        return "MatchResult{" +
               "lost='"  + lostItem.getItemId()  + "'" +
               ", found='" + foundItem.getItemId() + "'" +
               ", score=" + confidenceScore +
               ", reason='" + matchReason + "'}";
    }
}