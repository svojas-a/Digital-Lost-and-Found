package model.claim;

/**
 * Represents the possible outcomes of a verification process.
 */
public enum VerificationDecision {
    APPROVED("Approved", "Verification successful. Claim is approved."),
    REJECTED("Rejected", "Verification failed. Claim is rejected."),
    PENDING("Pending", "Verification not yet completed.");

    private final String displayName;
    private final String description;

    VerificationDecision(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
