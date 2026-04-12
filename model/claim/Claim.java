package model.claim;

import model.Item;
import model.MatchResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a claim between a lost item owner and a found item holder.
 * Tracks the entire lifecycle of the claim from match → verification → resolution.
 * 
 * KEY PROPERTIES:
 *  - claimId: Unique identifier (auto-generated)
 *  - status: Current state in lifecycle
 *  - matchResult: Reference to the MatchResult that triggered this claim
 *  - verifications: List of verification records (audit trail)
 *  - statusHistory: Tracks all status changes over time
 */
public class Claim {

    // ── Basic Fields ──────────────────────────────────────────────────────

    private int claimId;                          // Unique ID
    private String claimReference;                // Human-readable ref (e.g., "CLM-2024-001")
    
    // ── Core Relationships ────────────────────────────────────────────────

    private MatchResult matchResult;              // The matching that triggered this claim
    private Item lostItem;                        // From matchResult.getLostItem()
    private Item foundItem;                       // From matchResult.getFoundItem()

    // ── Claimant Info (Lost Item Owner) ───────────────────────────────────

    private String claimantName;                  // Name of person who lost item
    private String claimantContact;               // Their phone/email

    // ── Finder Info (Found Item Reporter) ─────────────────────────────────

    private String finderName;                    // Name of person who found item
    private String finderContact;                 // Their phone/email

    // ── Claim Status & History ────────────────────────────────────────────

    private ClaimStatus status;                   // Current status

    private String claimCreatedDate;              // ISO format: "2026-04-12 10:30:45"
    private String claimRequestedDate;            // When user clicked "Request Claim"
    private String verificationStartDate;         // When verifier started review
    private String verificationCompletedDate;     // When verifier submitted decision
    private String closedDate;                    // When claim finalized

    // ── Verification Data ─────────────────────────────────────────────────

    private List<Verification> verifications;     // All verification attempts (audit trail)
    private int matchConfidenceScore;             // From MatchResult (stored for reference)
    private String matchReason;                   // From MatchResult

    // ── Status Change Audit Trail ─────────────────────────────────────────

    private List<ClaimStatusChange> statusHistory; // Tracks all transitions

    // ── Constructor: Create from MatchResult ──────────────────────────────

    public Claim(int claimId, MatchResult matchResult, String claimReference) {
        
        if (matchResult == null) {
            throw new IllegalArgumentException("matchResult cannot be null");
        }

        this.claimId = claimId;
        this.claimReference = claimReference;
        this.matchResult = matchResult;
        
        // Extract item references
        this.lostItem = matchResult.getLostItem();
        this.foundItem = matchResult.getFoundItem();

        // Extract claimant info (lost item owner)
        this.claimantName = lostItem.getOwnerName() != null ? lostItem.getOwnerName() : "";
        this.claimantContact = lostItem.getContactInfo() != null ? lostItem.getContactInfo() : "";

        // Extract finder info (found item owner)
        this.finderName = foundItem.getOwnerName() != null ? foundItem.getOwnerName() : "";
        this.finderContact = foundItem.getContactInfo() != null ? foundItem.getContactInfo() : "";

        // Store match metadata
        this.matchConfidenceScore = matchResult.getConfidenceScore();
        this.matchReason = matchResult.getMatchReason();

        // Initialize status
        this.status = ClaimStatus.MATCHED;  // START: Auto-created claims always start here
        this.claimCreatedDate = getCurrentTimestamp();

        // Initialize collections
        this.verifications = new ArrayList<>();
        this.statusHistory = new ArrayList<>();

        // Record initial status change
        recordStatusChange(ClaimStatus.MATCHED, "Auto-created from match detection");
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public int getClaimId() { return claimId; }
    public String getClaimReference() { return claimReference; }
    public MatchResult getMatchResult() { return matchResult; }
    public Item getLostItem() { return lostItem; }
    public Item getFoundItem() { return foundItem; }
    public String getClaimantName() { return claimantName; }
    public String getClaimantContact() { return claimantContact; }
    public String getFinderName() { return finderName; }
    public String getFinderContact() { return finderContact; }
    public ClaimStatus getStatus() { return status; }
    public String getClaimCreatedDate() { return claimCreatedDate; }
    public String getClaimRequestedDate() { return claimRequestedDate; }
    public String getVerificationStartDate() { return verificationStartDate; }
    public String getVerificationCompletedDate() { return verificationCompletedDate; }
    public String getClosedDate() { return closedDate; }
    public List<Verification> getVerifications() { return new ArrayList<>(verifications); }
    public int getMatchConfidenceScore() { return matchConfidenceScore; }
    public String getMatchReason() { return matchReason; }
    public List<ClaimStatusChange> getStatusHistory() { return new ArrayList<>(statusHistory); }

    // ── Verification Operations ───────────────────────────────────────────

    /**
     * Add a verification record to this claim.
     * Called when verifier submits their decision.
     */
    public void addVerification(Verification verification) {
        if (verification == null) {
            throw new IllegalArgumentException("Verification cannot be null");
        }
        verifications.add(verification);
    }

    /**
     * Get the most recent verification (if any).
     */
    public Verification getLatestVerification() {
        if (verifications.isEmpty()) {
            return null;
        }
        return verifications.get(verifications.size() - 1);
    }

    // ── Status Transition Operations ──────────────────────────────────────

    /**
     * Transition claim to CLAIM_REQUESTED status.
     * Called when claimant explicitly submits their claim.
     * 
     * @param reason Audit trail reason
     * @throws IllegalStateException if transition is invalid
     */
    public void requestClaim(String reason) {
        if (status != ClaimStatus.MATCHED) {
            throw new IllegalStateException(
                "Cannot request claim from status: " + status + 
                ". Must be in MATCHED status.");
        }
        this.status = ClaimStatus.CLAIM_REQUESTED;
        this.claimRequestedDate = getCurrentTimestamp();
        recordStatusChange(ClaimStatus.CLAIM_REQUESTED, reason);
    }

    /**
     * Transition claim to UNDER_VERIFICATION status.
     * Called when admin/system assigns verifier to review claim.
     * 
     * @param verifierName Name of assigned verifier
     */
    public void startVerification(String verifierName) {
        if (status != ClaimStatus.CLAIM_REQUESTED) {
            throw new IllegalStateException(
                "Cannot start verification from status: " + status + 
                ". Must be in CLAIM_REQUESTED status.");
        }
        this.status = ClaimStatus.UNDER_VERIFICATION;
        this.verificationStartDate = getCurrentTimestamp();
        recordStatusChange(ClaimStatus.UNDER_VERIFICATION, 
            "Verification assigned to " + verifierName);
    }

    /**
     * Approve this claim (verification passed).
     * Status: UNDER_VERIFICATION → CLAIM_APPROVED → CLOSED
     * 
     * @param reason Audit trail reason
     */
    public void approveClaim(String reason) {
        if (status != ClaimStatus.UNDER_VERIFICATION) {
            throw new IllegalStateException(
                "Cannot approve claim from status: " + status + 
                ". Must be UNDER_VERIFICATION.");
        }
        this.status = ClaimStatus.CLAIM_APPROVED;
        this.verificationCompletedDate = getCurrentTimestamp();
        recordStatusChange(ClaimStatus.CLAIM_APPROVED, reason);

        // Note: Final transition to CLOSED happens in ClaimService.approveClaim()
        // after updating item ownership
    }

    /**
     * Reject this claim (verification failed).
     * Status: UNDER_VERIFICATION → CLAIM_REJECTED
     * 
     * @param rejectionReason Why was it rejected?
     */
    public void rejectClaim(String rejectionReason) {
        if (status != ClaimStatus.UNDER_VERIFICATION) {
            throw new IllegalStateException(
                "Cannot reject claim from status: " + status + 
                ". Must be UNDER_VERIFICATION.");
        }
        this.status = ClaimStatus.CLAIM_REJECTED;
        this.verificationCompletedDate = getCurrentTimestamp();
        recordStatusChange(ClaimStatus.CLAIM_REJECTED, rejectionReason);
    }

    /**
     * Close this claim (final step after successful approval).
     * Status: CLAIM_APPROVED → CLOSED
     */
    public void closeClaim() {
        if (status != ClaimStatus.CLAIM_APPROVED) {
            throw new IllegalStateException(
                "Cannot close claim from status: " + status + 
                ". Must be CLAIM_APPROVED.");
        }
        this.status = ClaimStatus.CLOSED;
        this.closedDate = getCurrentTimestamp();
        recordStatusChange(ClaimStatus.CLOSED, "Ownership transfer complete");
    }

    // ── Audit Trail ───────────────────────────────────────────────────────

    private void recordStatusChange(ClaimStatus newStatus, String reason) {
        ClaimStatusChange change = new ClaimStatusChange(
            this.claimId,
            this.status != null ? this.status : ClaimStatus.MATCHED,
            newStatus,
            getCurrentTimestamp(),
            reason
        );
        this.statusHistory.add(change);
    }

    // ── Utility ───────────────────────────────────────────────────────────

    private String getCurrentTimestamp() {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "Claim{" +
               "id=" + claimId +
               ", status=" + status +
               ", claimant='" + claimantName + "'" +
               ", lostItemId=" + lostItem.getItemId() +
               ", foundItemId=" + foundItem.getItemId() +
               "}";
    }

    // ── Inner Class: Status Change Record ─────────────────────────────────

    public static class ClaimStatusChange {
        private final int claimId;
        private final ClaimStatus fromStatus;
        private final ClaimStatus toStatus;
        private final String timestamp;
        private final String reason;

        public ClaimStatusChange(int claimId, ClaimStatus fromStatus, ClaimStatus toStatus,
                                String timestamp, String reason) {
            this.claimId = claimId;
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.timestamp = timestamp;
            this.reason = reason;
        }

        public ClaimStatus getFromStatus() { return fromStatus; }
        public ClaimStatus getToStatus() { return toStatus; }
        public String getTimestamp() { return timestamp; }
        public String getReason() { return reason; }

        @Override
        public String toString() {
            return timestamp + ": " + fromStatus + " → " + toStatus + " (" + reason + ")";
        }
    }
}
