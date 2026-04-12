package model.validator;

import java.util.*;

/**
 * Validates Item status transitions.
 * Enforces the strict status flow defined in requirements:
 * Reported → Searching → Matched → Claimed → Closed
 */
public class ItemStatusValidator {

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = new HashMap<>();

    static {
        // Define valid transitions
        ALLOWED_TRANSITIONS.put("Reported", new HashSet<>(Arrays.asList("Searching", "Matched")));
        ALLOWED_TRANSITIONS.put("Searching", new HashSet<>(Arrays.asList("Matched", "Reported")));
        ALLOWED_TRANSITIONS.put("Matched", new HashSet<>(Arrays.asList("Claimed", "Reported")));
        ALLOWED_TRANSITIONS.put("Claimed", new HashSet<>(Arrays.asList("Closed")));
        ALLOWED_TRANSITIONS.put("Closed", new HashSet<>());  // Terminal state
    }

    /**
     * Check if a status transition is valid.
     * 
     * @param fromStatus Current status
     * @param toStatus Desired status
     * @return true if transition is allowed, false otherwise
     */
    public static boolean isValidTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }

        if (fromStatus.equals(toStatus)) {
            return true;  // No-op transition always valid
        }

        Set<String> allowed = ALLOWED_TRANSITIONS.get(fromStatus);
        return allowed != null && allowed.contains(toStatus);
    }

    /**
     * Get all allowed next states for a given current status.
     * 
     * @param currentStatus The current status
     * @return Set of valid next states
     */
    public static Set<String> getAllAllowedTransitions(String currentStatus) {
        return new HashSet<>(ALLOWED_TRANSITIONS.getOrDefault(currentStatus, new HashSet<>()));
    }

    /**
     * Check if a status is a terminal state (no further transitions allowed).
     * 
     * @param status The status to check
     * @return true if terminal
     */
    public static boolean isTerminalState(String status) {
        Set<String> allowed = ALLOWED_TRANSITIONS.get(status);
        return allowed != null && allowed.isEmpty();
    }
}
