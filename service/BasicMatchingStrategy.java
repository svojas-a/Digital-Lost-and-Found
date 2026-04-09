package service;

import model.Item;
import model.LostItem;
import model.FoundItem;
import model.MatchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy: Basic keyword + location matching.
 *
 * Scoring rubric:
 *   +50  — location match (case-insensitive substring)
 *   +30  — name match
 *   +20  — description keyword overlap
 *
 * Any pair scoring >= 30 is returned as a potential match.
 */
public class BasicMatchingStrategy implements MatchingStrategy {

    private static final int THRESHOLD = 30;

    @Override
    public List<MatchResult> findMatches(List<Item> lostItems,
                                         List<Item> foundItems) {
        List<MatchResult> results = new ArrayList<>();

        for (Item lost : lostItems) {
            for (Item found : foundItems) {
                int score = 0;
                StringBuilder reason = new StringBuilder();

                String lostLoc  = getLocation(lost).toLowerCase();
                String foundLoc = getLocation(found).toLowerCase();

                // ── Location score ───────────────────────────────────────────
                if (!lostLoc.isEmpty() && !foundLoc.isEmpty()) {
                    if (lostLoc.equals(foundLoc)) {
                        score += 50;
                        reason.append("Exact location match. ");
                    } else if (lostLoc.contains(foundLoc) ||
                               foundLoc.contains(lostLoc)) {
                        score += 30;
                        reason.append("Partial location match. ");
                    }
                }

                // ── Name score ────────────────────────────────────────────────
                String lostName  = lost.getName()  != null ? lost.getName().toLowerCase()  : "";
                String foundName = found.getName() != null ? found.getName().toLowerCase() : "";

                if (!lostName.isEmpty() && !foundName.isEmpty()) {
                    if (lostName.equals(foundName)) {
                        score += 30;
                        reason.append("Exact name match. ");
                    } else if (lostName.contains(foundName) ||
                               foundName.contains(lostName)) {
                        score += 15;
                        reason.append("Partial name match. ");
                    }
                }

                // ── Description keyword overlap ───────────────────────────────
                String lostDesc  = lost.getDescription()  != null ? lost.getDescription().toLowerCase()  : "";
                String foundDesc = found.getDescription() != null ? found.getDescription().toLowerCase() : "";

                int overlap = keywordOverlap(lostDesc, foundDesc);
                if (overlap >= 2) {
                    score += 20;
                    reason.append("Description overlap (" + overlap + " keywords). ");
                } else if (overlap == 1) {
                    score += 10;
                    reason.append("Weak description overlap. ");
                }

                // ── Threshold check ───────────────────────────────────────────
                if (score >= THRESHOLD) {
                    results.add(new MatchResult(lost, found,
                            Math.min(score, 100),
                            reason.toString().trim()));
                }
            }
        }

        // Sort by score descending
        results.sort((a, b) -> b.getConfidenceScore() - a.getConfidenceScore());
        return results;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getLocation(Item item) {
        if (item instanceof LostItem)  return ((LostItem)  item).getLostLocation()  != null
                                              ? ((LostItem)  item).getLostLocation()  : "";
        if (item instanceof FoundItem) return ((FoundItem) item).getFoundLocation() != null
                                              ? ((FoundItem) item).getFoundLocation() : "";
        return item.getDescription() != null ? item.getDescription() : "";
    }

    /**
     * Counts how many meaningful words (length > 3) appear in both strings.
     */
    private int keywordOverlap(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return 0;
        String[] wordsA = a.split("\\W+");
        int count = 0;
        for (String word : wordsA) {
            if (word.length() > 3 && b.contains(word)) {
                count++;
            }
        }
        return count;
    }
}