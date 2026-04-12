package model.claim;

/**
 * Enum representing all possible states in a Claim lifecycle.
 * 
 * State transitions follow this pattern:
 * MATCHED → CLAIM_REQUESTED → UNDER_VERIFICATION → (CLAIM_APPROVED / CLAIM_REJECTED)
 * CLAIM_APPROVED → CLOSED
 */
public enum ClaimStatus {
    MATCHED("Matched", 
            "Automatic match found. Awaiting user action to request claim."),
    
    CLAIM_REQUESTED("Claim Requested", 
            "User has initiated a claim request. Awaiting verification assignment."),
    
    UNDER_VERIFICATION("Under Verification", 
            "Verifier is reviewing the claim and supporting documents."),
    
    CLAIM_APPROVED("Claim Approved", 
            "Verification successful. Ownership transfer approved. Transitioning to CLOSED."),
    
    CLAIM_REJECTED("Claim Rejected", 
            "Verification failed. Claim denied. Item remains available."),
    
    CLOSED("Closed", 
            "Claim resolved. Item ownership transferred or dispute closed.");

    private final String displayName;
    private final String description;

    ClaimStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
