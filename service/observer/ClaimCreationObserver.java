package service.observer;

import service.MatchObserver;
import model.MatchResult;
import service.claim.ClaimService;
import java.util.List;

/**
 * Observer that listens to match events and AUTO-CREATES claims.
 * 
 * INTEGRATION POINT:
 *  - Implements MatchObserver (Person 2's interface)
 *  - Registered with SearchService by Person 3
 *  - When matches are found, automatically creates Claims
 * 
 * DESIGN:
 *  - Decoupled: SearchService doesn't need to know about Claims
 *  - Extensible: Person 4 can add notification observer similarly
 *  - Non-blocking: Observer execution doesn't affect search results
 */
public class ClaimCreationObserver implements MatchObserver {

    private final ClaimService claimService;

    public ClaimCreationObserver(ClaimService claimService) {
        if (claimService == null) {
            throw new IllegalArgumentException("ClaimService cannot be null");
        }
        this.claimService = claimService;
    }

    /**
     * Called when matches are found by SearchService.
     * 
     * ALGORITHM:
     *  1. Iterate through each MatchResult
     *  2. For each match:
     *     a. Create claim via ClaimService
     *     b. Update items to "Matched" status (if not already)
     *     c. Log action
     * 
     * @param matches List of matches found
     */
    @Override
    public void onMatchesFound(List<MatchResult> matches) {
        if (matches == null || matches.isEmpty()) {
            return;
        }

        System.out.println("\n[OBSERVER] ClaimCreationObserver triggered: " +
                         matches.size() + " match(es) detected");

        for (MatchResult match : matches) {
            try {
                // Auto-create claim from match
                var claim = claimService.createClaimFromMatch(match);

                // Ensure items are marked as "Matched"
                // NOTE: SearchService should do this BEFORE notify observers,
                // but we do it here as safety check
                if (!match.getLostItem().getStatus().equals("Matched")) {
                    match.getLostItem().updateStatus("Matched");
                }
                if (!match.getFoundItem().getStatus().equals("Matched")) {
                    match.getFoundItem().updateStatus("Matched");
                }

                System.out.println("[OBSERVER] ✓ Claim created: " + claim.getClaimReference());

            } catch (Exception e) {
                System.err.println("[OBSERVER] ✗ Failed to create claim for match: " +
                                 e.getMessage());
            }
        }

        System.out.println("[OBSERVER] Claim creation processing complete\n");
    }
}
