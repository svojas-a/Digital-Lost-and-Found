package service.claim;

import model.claim.*;
import model.Item;
import model.MatchResult;
import model.validator.ClaimValidator;
import java.util.List;
import java.util.Optional;

/**
 * ClaimService: Core business logic for Claims.
 * 
 * RESPONSIBILITIES:
 *  1. Create claims from matches
 *  2. Process claim requests (MATCHED → CLAIM_REQUESTED)
 *  3. Handle verification workflow (UNDER_VERIFICATION)
 *  4. Approve/Reject claims with verification records
 *  5. Update item statuses accordingly
 *  6. Maintain audit trail
 * 
 * DEPENDENCIES:
 *  - ClaimRepository: for persistence
 *  - ClaimValidator: for business rule validation
 *  - Item: for status updates
 */
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimValidator claimValidator;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
        this.claimValidator = new ClaimValidator(claimRepository);
    }

    // ══════════════════════════════════════════════════════════════════════
    // PRIMARY OPERATIONS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * AUTO-CREATE CLAIM from a MatchResult.
     * Called by ClaimCreationObserver when a match is detected.
     * 
     * PROCESS:
     *  1. Extract items + match metadata from MatchResult
     *  2. Create Claim with status = MATCHED
     *  3. Validate claim data
     *  4. Save to repository
     *  5. Return created claim
     * 
     * @param matchResult The matching result
     * @return The newly created Claim
     * @throws IllegalArgumentException if claim data invalid
     */
    public Claim createClaimFromMatch(MatchResult matchResult) {
        if (matchResult == null) {
            throw new IllegalArgumentException("MatchResult cannot be null");
        }

        // Generate claim ID and reference
        int claimId = claimRepository.getNextClaimId();
        String claimReference = claimRepository.generateClaimReference();

        // Create claim
        Claim claim = new Claim(claimId, matchResult, claimReference);

        // Validate before saving
        claimValidator.validateNewClaim(claim);

        // Save claim
        Claim savedClaim = claimRepository.save(claim);

        System.out.println("[CLAIM] Auto-created: " + savedClaim.getClaimReference() +
                         " (Lost Item #" + savedClaim.getLostItem().getItemId() +
                         " vs Found Item #" + savedClaim.getFoundItem().getItemId() + ")");

        return savedClaim;
    }

    /**
     * REQUEST CLAIM: Claimant submits their claim for verification.
     * Transition: MATCHED → CLAIM_REQUESTED
     * 
     * PROCESS:
     *  1. Find claim by ID
     *  2. Validate transition is allowed
     *  3. Update claim status
     *  4. Save updated claim
     *  5. Return updated claim
     * 
     * @param claimId The claim ID
     * @param requestingUserName Name of person requesting (for audit)
     * @return The updated claim
     * @throws IllegalArgumentException if claim not found
     * @throws IllegalStateException if transition invalid
     */
    public Claim requestClaim(int claimId, String requestingUserName) {
        Claim claim = getClaimById(claimId);

        // Validate transition
        claimValidator.validateClaimRequest(claim);

        // Update claim status
        String reason = "Claim requested by " + requestingUserName;
        claim.requestClaim(reason);

        // Save
        claimRepository.save(claim);

        System.out.println("[CLAIM] Status updated to CLAIM_REQUESTED: " + claim.getClaimReference());

        return claim;
    }

    /**
     * START VERIFICATION: Assign verifier to review claim.
     * Transition: CLAIM_REQUESTED → UNDER_VERIFICATION
     * 
     * PROCESS:
     *  1. Find claim
     *  2. Validate can start verification
     *  3. Update claim status
     *  4. Save
     * 
     * @param claimId The claim ID
     * @param verifierName Name of assigned verifier
     * @return Updated claim
     */
    public Claim startVerification(int claimId, String verifierName) {
        Claim claim = getClaimById(claimId);

        // Validate
        claimValidator.validateVerificationStart(claim);

        // Update
        claim.startVerification(verifierName);

        // Save
        claimRepository.save(claim);

        System.out.println("[CLAIM] Verification started by " + verifierName +
                         " for claim " + claim.getClaimReference());

        return claim;
    }

    /**
     * SUBMIT VERIFICATION: Verifier submits their decision (approval/rejection).
     * Does NOT yet transition to next state - just records verification.
     * 
     * @param claimId The claim ID
     * @param verification The Verification record with decision
     * @throws IllegalArgumentException if claim not found or not under verification
     */
    public void submitVerification(int claimId, Verification verification) {
        Claim claim = getClaimById(claimId);

        if (claim.getStatus() != ClaimStatus.UNDER_VERIFICATION) {
            throw new IllegalStateException(
                "Can only submit verification for claims in UNDER_VERIFICATION status. " +
                "Current: " + claim.getStatus());
        }

        if (verification == null) {
            throw new IllegalArgumentException("Verification cannot be null");
        }

        // Add verification record
        claim.addVerification(verification);

        // Save the claim with new verification
        claimRepository.save(claim);

        System.out.println("[CLAIM] Verification submitted for " + claim.getClaimReference() +
                         " - Decision: " + verification.getDecision());
    }

    /**
     * APPROVE CLAIM: Verifier approves, ownership transfer proceeds.
     * Transition: UNDER_VERIFICATION → CLAIM_APPROVED → CLOSED
     * Also updates item ownership and status.
     * 
     * PROCESS:
     *  1. Find claim
     *  2. Validate claim can be approved
     *  3. Get latest verification (must be APPROVED)
     *  4. Update claim status → CLAIM_APPROVED
     *  5. Update BOTH items:
     *     - Lost item: ownerName = finder
     *     - Lost item: status = "Claimed"
     *     - Found item: status = "Claimed"
     *  6. Transition claim → CLOSED
     *  7. Save everything
     * 
     * @param claimId The claim ID
     * @throws IllegalStateException if transition invalid
     */
    public Claim approveClaim(int claimId) {
        Claim claim = getClaimById(claimId);

        // Validate
        claimValidator.validateApproval(claim);

        // Check: Latest verification must be APPROVED
        Verification latestVerification = claim.getLatestVerification();
        if (latestVerification == null || 
            latestVerification.getDecision() != VerificationDecision.APPROVED) {
            throw new IllegalStateException(
                "Cannot approve claim without APPROVED verification");
        }

        // Update claim status
        claim.approveClaim("Approved based on verification");

        // ─── OWNERSHIP TRANSFER ───────────────────────────────────────

        Item lostItem = claim.getLostItem();
        Item foundItem = claim.getFoundItem();

        // Transfer ownership: set lost item's owner to the finder
        lostItem.setOwnerName(claim.getFinderName());
        lostItem.setContactInfo(claim.getFinderContact());
        lostItem.updateStatus("Claimed");

        // Mark found item as claimed
        foundItem.updateStatus("Claimed");

        // ───────────────────────────────────────────────────────────────

        // Transition to CLOSED
        claim.closeClaim();

        // Save claim
        claimRepository.save(claim);

        System.out.println("[CLAIM] APPROVED: " + claim.getClaimReference() +
                         " - Item ownership transferred to " + claim.getFinderName());

        return claim;
    }

    /**
     * REJECT CLAIM: Verifier rejects claim.
     * Transition: UNDER_VERIFICATION → CLAIM_REJECTED
     * Items remain available for other claims.
     * 
     * PROCESS:
     *  1. Find claim
     *  2. Validate can be rejected
     *  3. Get latest verification (must be REJECTED)
     *  4. Update claim status
     *  5. Items: Revert to "Reported" (allow re-matching)
     *  6. Save
     * 
     * @param claimId The claim ID
     * @return Updated claim
     */
    public Claim rejectClaim(int claimId) {
        Claim claim = getClaimById(claimId);

        // Validate
        claimValidator.validateRejection(claim);

        // Check: Latest verification must be REJECTED
        Verification latestVerification = claim.getLatestVerification();
        if (latestVerification == null ||
            latestVerification.getDecision() != VerificationDecision.REJECTED) {
            throw new IllegalStateException(
                "Cannot reject claim without REJECTED verification");
        }

        // Update claim status
        String rejectionReason = latestVerification.getRejectionReason();
        claim.rejectClaim("Rejected: " + rejectionReason);

        // ─── REVERT ITEM STATUS ───────────────────────────────────────

        Item lostItem = claim.getLostItem();
        Item foundItem = claim.getFoundItem();

        // Revert to "Reported" - items available for other claims
        lostItem.updateStatus("Reported");
        foundItem.updateStatus("Reported");

        // ───────────────────────────────────────────────────────────────

        // Save claim
        claimRepository.save(claim);

        System.out.println("[CLAIM] REJECTED: " + claim.getClaimReference() +
                         " - Reason: " + rejectionReason);

        return claim;
    }

    // ══════════════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Get a claim by ID.
     * 
     * @param claimId The claim ID
     * @return The claim
     * @throws IllegalArgumentException if not found
     */
    public Claim getClaimById(int claimId) {
        Optional<Claim> claim = claimRepository.findById(claimId);
        if (!claim.isPresent()) {
            throw new IllegalArgumentException("Claim not found: #" + claimId);
        }
        return claim.get();
    }

    /**
     * Get all claims for an item.
     * 
     * @param item The item
     * @return List of claims (may be empty)
     */
    public List<Claim> getClaimsByItem(Item item) {
        return claimRepository.findByItem(item);
    }

    /**
     * Get all claims with a specific status.
     * Useful for verifier queues.
     * 
     * @param status The status filter
     * @return List of matching claims
     */
    public List<Claim> getClaimsByStatus(String status) {
        return claimRepository.findByStatus(status);
    }

    /**
     * Get all claims awaiting verification.
     * 
     * @return List of claims in UNDER_VERIFICATION status
     */
    public List<Claim> getPendingVerifications() {
        return claimRepository.findByStatus("Under Verification");
    }

    /**
     * Get all claims.
     * 
     * @return List of all claims
     */
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
}
