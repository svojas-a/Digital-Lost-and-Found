package model.claim;

/**
 * Represents a single verification attempt/record for a Claim.
 * Immutable value object - once created, cannot be modified.
 * Allows multiple verifications for one claim (audit trail).
 */
public class Verification {

    private final int verificationId;
    private final int claimId;                    // Foreign key
    
    private final String verifierId;              // ID or name of verifier
    private final String verifierName;            // Display name
    
    private final String verificationDate;        // Format: "YYYY-MM-DD HH:mm:ss"
    private final VerificationMethod method;      // How was it verified?
    
    private final String notes;                   // Verifier's detailed notes
    private final VerificationDecision decision;  // APPROVED / REJECTED / PENDING
    private final int confidenceLevel;            // 0-100, independent of match score
    private final String rejectionReason;         // If REJECTED, why? (can be null)

    // Constructor: Full specification
    public Verification(int verificationId, int claimId,
                       String verifierId, String verifierName,
                       String verificationDate, VerificationMethod method,
                       String notes, VerificationDecision decision,
                       int confidenceLevel, String rejectionReason) {
        
        if (confidenceLevel < 0 || confidenceLevel > 100) {
            throw new IllegalArgumentException("Confidence level must be 0-100");
        }
        if (decision == VerificationDecision.REJECTED && rejectionReason == null) {
            throw new IllegalArgumentException("Rejection reason required for REJECTED decision");
        }

        this.verificationId = verificationId;
        this.claimId = claimId;
        this.verifierId = verifierId;
        this.verifierName = verifierName;
        this.verificationDate = verificationDate;
        this.method = method;
        this.notes = notes;
        this.decision = decision;
        this.confidenceLevel = confidenceLevel;
        this.rejectionReason = rejectionReason;
    }

    // ── Getters (no setters - immutable) ──────────────────────────────

    public int getVerificationId() { return verificationId; }
    public int getClaimId() { return claimId; }
    public String getVerifierId() { return verifierId; }
    public String getVerifierName() { return verifierName; }
    public String getVerificationDate() { return verificationDate; }
    public VerificationMethod getMethod() { return method; }
    public String getNotes() { return notes; }
    public VerificationDecision getDecision() { return decision; }
    public int getConfidenceLevel() { return confidenceLevel; }
    public String getRejectionReason() { return rejectionReason; }

    @Override
    public String toString() {
        return "Verification{" +
               "id=" + verificationId +
               ", verifier='" + verifierName + "'" +
               ", date='" + verificationDate + "'" +
               ", decision=" + decision +
               ", confidence=" + confidenceLevel + "%" +
               "}";
    }
}
