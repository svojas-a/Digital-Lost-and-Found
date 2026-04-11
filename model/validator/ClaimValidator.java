package model.validator;

import model.claim.Claim;
import model.claim.ClaimStatus;
import model.Item;
import service.claim.ClaimRepository;

/**
 * Validates Claim business logic.
 * Prevents invalid claim states and duplicate claims.
 */
public class ClaimValidator {

    private final ClaimRepository claimRepository;

    public ClaimValidator(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    /**
     * Validate a newly created claim before saving.
     * 
     * @param claim The claim to validate
     * @throws IllegalArgumentException if invalid
     */
    public void validateNewClaim(Claim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("Claim cannot be null");
        }

        // Check: Claim must start in MATCHED status
        if (claim.getStatus() != ClaimStatus.MATCHED) {
            throw new IllegalArgumentException(
                "New claims must start in MATCHED status, got: " + claim.getStatus());
        }

        // Check: Lost and Found items must be different
        if (claim.getLostItem().getItemId() == claim.getFoundItem().getItemId()) {
            throw new IllegalArgumentException(
                "Claim cannot reference the same item for both lost and found");
        }

        // Check: Claimant and finder info must exist
        if (claim.getClaimantName() == null || claim.getClaimantName().isEmpty()) {
            throw new IllegalArgumentException("Claimant name is required");
        }
        if (claim.getFinderName() == null || claim.getFinderName().isEmpty()) {
            throw new IllegalArgumentException("Finder name is required");
        }

        // Check: No duplicate ACTIVE claims for same lost item
        validateNoDuplicateActiveClaims(claim.getLostItem());
    }

    /**
     * Ensure a claim can be transitioned to CLAIM_REQUESTED status.
     * 
     * @param claim The claim being requested
     * @throws IllegalStateException if not allowed
     */
    public void validateClaimRequest(Claim claim) {
        if (claim.getStatus() != ClaimStatus.MATCHED) {
            throw new IllegalStateException(
                "Can only request claims in MATCHED status. Current: " + claim.getStatus());
        }
    }

    /**
     * Ensure a claim can enter verification.
     * 
     * @param claim The claim being verified
     * @throws IllegalStateException if not allowed
     */
    public void validateVerificationStart(Claim claim) {
        if (claim.getStatus() != ClaimStatus.CLAIM_REQUESTED) {
            throw new IllegalStateException(
                "Can only start verification for claims in CLAIM_REQUESTED status. Current: " + 
                claim.getStatus());
        }
    }

    /**
     * Ensure a claim can be approved.
     * 
     * @param claim The claim being approved
     * @throws IllegalStateException if not allowed
     */
    public void validateApproval(Claim claim) {
        if (claim.getStatus() != ClaimStatus.UNDER_VERIFICATION) {
            throw new IllegalStateException(
                "Can only approve claims in UNDER_VERIFICATION status. Current: " + 
                claim.getStatus());
        }

        // Check: Item status must be "Matched"
        if (!claim.getLostItem().getStatus().equals("Matched")) {
            throw new IllegalStateException(
                "Lost item must be in 'Matched' status to approve claim");
        }
    }

    /**
     * Ensure a claim can be rejected.
     * 
     * @param claim The claim being rejected
     * @throws IllegalStateException if not allowed
     */
    public void validateRejection(Claim claim) {
        if (claim.getStatus() != ClaimStatus.UNDER_VERIFICATION) {
            throw new IllegalStateException(
                "Can only reject claims in UNDER_VERIFICATION status. Current: " + 
                claim.getStatus());
        }
    }

    // ── Private Helpers ────────────────────────────────────────────────────

    private void validateNoDuplicateActiveClaims(Item lostItem) {
        var existingClaims = claimRepository.findByItemId(lostItem.getItemId());

        for (Claim existing : existingClaims) {
            // Only consider non-terminal claims as "active"
            if (existing.getStatus() != ClaimStatus.CLOSED &&
                existing.getStatus() != ClaimStatus.CLAIM_REJECTED) {

                throw new IllegalArgumentException(
                    "Lost item #" + lostItem.getItemId() +
                    " already has an active claim: #" + existing.getClaimId());
            }
        }
    }
}
