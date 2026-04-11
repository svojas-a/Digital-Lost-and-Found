package controller;

import model.claim.Claim;
import model.claim.Verification;
import model.claim.VerificationDecision;
import model.claim.VerificationMethod;
import service.claim.ClaimService;
import view.ClaimView;
import java.util.List;

/**
 * ClaimController: Orchestrates Claim operations between View and Service.
 * Follows MVC pattern - View calls controller methods, controller calls service.
 * 
 * RESPONSIBILITIES:
 *  - Handle user actions (request claim, submit verification, etc.)
 *  - Call appropriate ClaimService methods
 *  - Display results via ClaimView
 *  - Handle errors gracefully
 */
public class ClaimController {

    private final ClaimService claimService;
    private final ClaimView claimView;

    public ClaimController(ClaimService claimService, ClaimView claimView) {
        if (claimService == null || claimView == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.claimService = claimService;
        this.claimView = claimView;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CLAIM REQUEST FLOW
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Handle user requesting a claim (from MATCHED to CLAIM_REQUESTED).
     * 
     * @param claimId The claim ID
     * @param requestingUserName Name of user requesting
     */
    public void handleRequestClaim(int claimId, String requestingUserName) {
        try {
            Claim updatedClaim = claimService.requestClaim(claimId, requestingUserName);
            claimView.showClaimRequested(updatedClaim);
        } catch (Exception e) {
            claimView.showError("Failed to request claim: " + e.getMessage());
        }
    }

    /**
     * Handle starting verification (assign verifier).
     * 
     * @param claimId The claim ID
     * @param verifierName Name of verifier
     */
    public void handleStartVerification(int claimId, String verifierName) {
        try {
            Claim updatedClaim = claimService.startVerification(claimId, verifierName);
            claimView.showVerificationStarted(updatedClaim, verifierName);
        } catch (Exception e) {
            claimView.showError("Failed to start verification: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // VERIFICATION SUBMISSION
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Handle verifier submitting an approval decision.
     * 
     * @param claimId The claim ID
     * @param verifierId ID of verifier
     * @param verifierName Name of verifier
     * @param verificationMethod How was it verified
     * @param verificationNotes Detailed notes
     * @param confidenceLevel 0-100 confidence
     */
    public void handleApproveDecision(int claimId, String verifierId, String verifierName,
                                     String verificationMethod, String verificationNotes,
                                     int confidenceLevel) {
        try {
            Verification verification = new Verification(
                0,  // verificationId (not yet assigned)
                claimId,
                verifierId,
                verifierName,
                getCurrentTimestamp(),
                VerificationMethod.valueOf(verificationMethod),
                verificationNotes,
                VerificationDecision.APPROVED,
                confidenceLevel,
                null  // No rejection reason for approval
            );

            claimService.submitVerification(claimId, verification);
            claimView.showVerificationSubmitted(verification);

            // Auto-approve claim after decision submission
            Claim approvedClaim = claimService.approveClaim(claimId);
            claimView.showClaimApproved(approvedClaim);

        } catch (Exception e) {
            claimView.showError("Failed to process approval: " + e.getMessage());
        }
    }

    /**
     * Handle verifier submitting a rejection decision.
     * 
     * @param claimId The claim ID
     * @param verifierId ID of verifier
     * @param verifierName Name of verifier
     * @param verificationMethod How was it verified
     * @param rejectionReason Why was it rejected
     * @param verificationNotes Detailed notes
     * @param confidenceLevel 0-100 confidence
     */
    public void handleRejectDecision(int claimId, String verifierId, String verifierName,
                                    String verificationMethod, String rejectionReason,
                                    String verificationNotes, int confidenceLevel) {
        try {
            Verification verification = new Verification(
                0,  // verificationId (not yet assigned)
                claimId,
                verifierId,
                verifierName,
                getCurrentTimestamp(),
                VerificationMethod.valueOf(verificationMethod),
                verificationNotes,
                VerificationDecision.REJECTED,
                confidenceLevel,
                rejectionReason  // ← Required for rejection
            );

            claimService.submitVerification(claimId, verification);
            claimView.showVerificationSubmitted(verification);

            // Auto-reject claim after decision submission
            Claim rejectedClaim = claimService.rejectClaim(claimId);
            claimView.showClaimRejected(rejectedClaim);

        } catch (Exception e) {
            claimView.showError("Failed to process rejection: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Handle viewing a specific claim.
     * 
     * @param claimId The claim ID
     */
    public void handleViewClaim(int claimId) {
        try {
            Claim claim = claimService.getClaimById(claimId);
            claimView.showClaimDetails(claim);
        } catch (Exception e) {
            claimView.showError("Claim not found: " + e.getMessage());
        }
    }

    /**
     * Handle viewing all pending verifications (for verifier dashboard).
     */
    public void handleViewPendingVerifications() {
        try {
            List<Claim> pending = claimService.getPendingVerifications();
            claimView.showPendingVerificationsList(pending);
        } catch (Exception e) {
            claimView.showError("Failed to load pending verifications: " + e.getMessage());
        }
    }

    /**
     * Handle viewing all claims (admin view).
     */
    public void handleViewAllClaims() {
        try {
            List<Claim> allClaims = claimService.getAllClaims();
            claimView.showAllClaimsList(allClaims);
        } catch (Exception e) {
            claimView.showError("Failed to load claims: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // UTILITY
    // ══════════════════════════════════════════════════════════════════════

    private String getCurrentTimestamp() {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
