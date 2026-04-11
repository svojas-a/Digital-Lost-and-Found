package view;

import model.claim.Claim;
import model.claim.Verification;
import java.util.List;

/**
 * ClaimView: Displays claim information to console.
 * Display-only - no input parsing (that's controller's job).
 */
public class ClaimView {

    private static final String DIVIDER = 
        "==========================================================";

    public void showClaimCreated(Claim claim) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ✓ CLAIM AUTO-CREATED");
        System.out.println(DIVIDER);
        printClaimSummary(claim);
        System.out.println("  Status: " + claim.getStatus().getDisplayName());
        System.out.println("  Next: Await user to confirm claim request");
        System.out.println(DIVIDER);
    }

    public void showClaimRequested(Claim claim) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ✓ CLAIM REQUEST SUBMITTED");
        System.out.println(DIVIDER);
        printClaimSummary(claim);
        System.out.println("  Status: " + claim.getStatus().getDisplayName());
        System.out.println("  Next: Awaiting verifier assignment");
        System.out.println(DIVIDER);
    }

    public void showVerificationStarted(Claim claim, String verifierName) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ✓ VERIFICATION STARTED");
        System.out.println(DIVIDER);
        printClaimSummary(claim);
        System.out.println("  Verifier: " + verifierName);
        System.out.println("  Status: " + claim.getStatus().getDisplayName());
        System.out.println("  Next: Verifier submits decision");
        System.out.println(DIVIDER);
    }

    public void showVerificationSubmitted(Verification verification) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ✓ VERIFICATION SUBMITTED");
        System.out.println(DIVIDER);
        System.out.println("  Verifier: " + verification.getVerifierName());
        System.out.println("  Date: " + verification.getVerificationDate());
        System.out.println("  Method: " + verification.getMethod().getDisplayName());
        System.out.println("  Decision: " + verification.getDecision().getDisplayName());
        System.out.println("  Confidence: " + verification.getConfidenceLevel() + "%");
        System.out.println("  Notes: " + verification.getNotes());
        if (verification.getRejectionReason() != null) {
            System.out.println("  Rejection Reason: " + verification.getRejectionReason());
        }
        System.out.println(DIVIDER);
    }

    public void showClaimApproved(Claim claim) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ✅ CLAIM APPROVED");
        System.out.println(DIVIDER);
        printClaimSummary(claim);
        System.out.println("  Status: " + claim.getStatus().getDisplayName());
        System.out.println("  Ownership transferred to: " + claim.getFinderName());
        System.out.println("  ✓ Case closed");
        System.out.println(DIVIDER);
    }

    public void showClaimRejected(Claim claim) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ❌ CLAIM REJECTED");
        System.out.println(DIVIDER);
        printClaimSummary(claim);
        System.out.println("  Status: " + claim.getStatus().getDisplayName());
        if (claim.getLatestVerification() != null) {
            System.out.println("  Reason: " + 
                claim.getLatestVerification().getRejectionReason());
        }
        System.out.println("  ℹ Items remain available for other claims");
        System.out.println(DIVIDER);
    }

    public void showClaimDetails(Claim claim) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  CLAIM DETAILS");
        System.out.println(DIVIDER);
        printClaimSummary(claim);
        
        System.out.println("\n  Timeline:");
        System.out.println("    Created: " + claim.getClaimCreatedDate());
        if (claim.getClaimRequestedDate() != null) {
            System.out.println("    Requested: " + claim.getClaimRequestedDate());
        }
        if (claim.getVerificationStartDate() != null) {
            System.out.println("    Verification Started: " + claim.getVerificationStartDate());
        }
        if (claim.getVerificationCompletedDate() != null) {
            System.out.println("    Verification Completed: " + claim.getVerificationCompletedDate());
        }
        if (claim.getClosedDate() != null) {
            System.out.println("    Closed: " + claim.getClosedDate());
        }

        System.out.println("\n  Verifications (" + claim.getVerifications().size() + "):");
        for (Verification v : claim.getVerifications()) {
            System.out.println("    - " + v);
        }

        System.out.println("\n  Status History:");
        for (Claim.ClaimStatusChange change : claim.getStatusHistory()) {
            System.out.println("    - " + change);
        }

        System.out.println(DIVIDER);
    }

    public void showPendingVerificationsList(List<Claim> claims) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  PENDING VERIFICATIONS (" + claims.size() + ")");
        System.out.println(DIVIDER);

        if (claims.isEmpty()) {
            System.out.println("  No pending verifications");
        } else {
            for (Claim claim : claims) {
                System.out.println("  [" + claim.getClaimReference() + "] " +
                                 claim.getClaimantName() + " → " + claim.getFinderName());
                System.out.println("    Confidence: " + claim.getMatchConfidenceScore() + "%");
                System.out.println("    Lost Item: " + claim.getLostItem().getName());
            }
        }

        System.out.println(DIVIDER);
    }

    public void showAllClaimsList(List<Claim> claims) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ALL CLAIMS (" + claims.size() + ")");
        System.out.println(DIVIDER);

        if (claims.isEmpty()) {
            System.out.println("  No claims yet");
        } else {
            for (Claim claim : claims) {
                System.out.println("  [" + claim.getClaimReference() + "] " +
                                 claim.getClaimantName() + " → " + claim.getFinderName() +
                                 " | Status: " + claim.getStatus().getDisplayName());
            }
        }

        System.out.println(DIVIDER);
    }

    public void showError(String errorMessage) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  ❌ ERROR");
        System.out.println(DIVIDER);
        System.out.println("  " + errorMessage);
        System.out.println(DIVIDER);
    }

    // ── Private Helpers ────────────────────────────────────────────────────

    private void printClaimSummary(Claim claim) {
        System.out.println("  ID: " + claim.getClaimId());
        System.out.println("  Reference: " + claim.getClaimReference());
        System.out.println("  Claimant: " + claim.getClaimantName() + 
                          " (" + claim.getClaimantContact() + ")");
        System.out.println("  Finder: " + claim.getFinderName() + 
                          " (" + claim.getFinderContact() + ")");
        System.out.println("  Lost Item: #" + claim.getLostItem().getItemId() + " - " + 
                          claim.getLostItem().getName());
        System.out.println("  Found Item: #" + claim.getFoundItem().getItemId() + " - " + 
                          claim.getFoundItem().getName());
        System.out.println("  Match Confidence: " + claim.getMatchConfidenceScore() + "%");
    }
}
