package model.claim;

/**
 * Enum for different verification methods a verifier can use.
 */
public enum VerificationMethod {
    MANUAL("Manual Review", "Verifier manually examines documents and details"),
    DOCUMENT("Document Proof", "Claim verified via ID proof, receipts, etc."),
    WITNESS("Witness Account", "Verified through eyewitness statements"),
    VIDEO("Video Evidence", "Verified through video surveillance or claimant video");

    private final String displayName;
    private final String description;

    VerificationMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
