import controller.ClaimController;
import model.*;
import model.claim.Claim;
import model.claim.VerificationMethod;
import service.*;
import service.claim.ClaimService;
import service.impl.InMemoryClaimRepository;
import service.observer.ClaimCreationObserver;
import view.ClaimView;

import java.util.ArrayList;
import java.util.List;

/**
 * ClaimMain: Demo of the complete Claim + Verification System (Person 3).
 * 
 * FLOW DEMONSTRATED:
 * 1. Create sample items (lost + found)
 * 2. Setup search service with claim observer
 * 3. Run matching engine
 * 4. Claims auto-created for matches
 * 5. Claimant requests claim
 * 6. Admin assigns verifier
 * 7. Verifier approves/rejects
 * 8. Items transitioned + ownership transferred
 */
public class ClaimMain {

    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  CLAIM + VERIFICATION SYSTEM DEMO (Person 3)               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 0: SETUP
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("[SETUP] Initializing system...");
        
        // Create sample items
        List<Item> itemStore = buildSampleStore();
        
        // Setup Search Service (Person 2)
        SearchService searchService = new SearchService(itemStore, new BasicMatchingStrategy());
        
        // Setup Claim System (Person 3)
        var claimRepository = new InMemoryClaimRepository();
        var claimService = new ClaimService(claimRepository);
        var claimController = new ClaimController(claimService, new ClaimView());
        
        // Register ClaimCreationObserver to listen to match events
        searchService.addObserver(new ClaimCreationObserver(claimService));
        
        System.out.println("[SETUP] ✓ System ready\n");

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 1: RUN MATCHING ENGINE (Triggers auto-claim creation)
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 1: MATCHING ENGINE");
        System.out.println("═══════════════════════════════════════════════════════════════");

        List<MatchResult> matches = searchService.findAllMatches();
        System.out.println("\n[MATCH ENGINE] Found " + matches.size() + " potential matches");

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 2: VIEW CREATED CLAIMS
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 2: VIEW ALL CLAIMS (AUTO-CREATED FROM MATCHES)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        claimController.handleViewAllClaims();

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 3: CLAIMANT REQUESTS FIRST CLAIM
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 3: CLAIMANT SUBMITS CLAIM REQUEST");
        System.out.println("═══════════════════════════════════════════════════════════════");

        System.out.println("\n[ACTION] Claimant 'Swathi' requests Claim #1...");
        claimController.handleRequestClaim(1, "Swathi");

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 4: ADMIN ASSIGNS VERIFIER
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 4: ADMIN ASSIGNS VERIFIER");
        System.out.println("═══════════════════════════════════════════════════════════════");

        System.out.println("\n[ACTION] Admin assigns 'Officer John' as verifier...");
        claimController.handleStartVerification(1, "Officer John");

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 5: VERIFIER APPROVES CLAIM
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 5: VERIFIER APPROVES CLAIM #1");
        System.out.println("═══════════════════════════════════════════════════════════════");

        System.out.println("\n[ACTION] Verifier 'Officer John' approves claim after document verification...");
        claimController.handleApproveDecision(
            1,                           // claimId
            "V001",                      // verifierId
            "Officer John",              // verifierName
            "DOCUMENT",                  // verificationMethod
            "Student ID match confirmed. Wallet reported on matching date. Approve.",  // notes
            95                           // confidenceLevel
        );

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 6: VIEW APPROVED CLAIM DETAILS
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 6: VIEW CLAIM #1 DETAILS (APPROVED & CLOSED)");
        System.out.println("═══════════════════════════════════════════════════════════════");

        claimController.handleViewClaim(1);

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 7: PROCESS SECOND CLAIM - REJECTION SCENARIO
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 7: PROCESS SECOND CLAIM - REJECTION");
        System.out.println("═══════════════════════════════════════════════════════════════");

        System.out.println("\n[ACTION] Claimant 'Rahul' requests Claim #2...");
        claimController.handleRequestClaim(2, "Rahul");

        System.out.println("\n[ACTION] Admin assigns 'Officer Maria' as verifier...");
        claimController.handleStartVerification(2, "Officer Maria");

        System.out.println("\n[ACTION] Verifier 'Officer Maria' REJECTS claim - insufficient documentation...");
        claimController.handleRejectDecision(
            2,                                    // claimId
            "V002",                               // verifierId
            "Officer Maria",                      // verifierName
            "MANUAL",                             // verificationMethod
            "No supporting documents provided. Unable to verify watch ownership claim.",  // rejectionReason
            "Claimant provided only verbal description. No receipts, warranty, or ID proof.",  // notes
            35                                    // confidenceLevel
        );

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 8: VIEW REJECTED CLAIM DETAILS
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 8: VIEW CLAIM #2 DETAILS (REJECTED)");
        System.out.println("═══════════════════════════════════════════════════════════════");

        claimController.handleViewClaim(2);

        // ─────────────────────────────────────────────────────────────────────
        // PHASE 9: VIEW ALL CLAIMS SUMMARY
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 9: FINAL CLAIM SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════════════");

        claimController.handleViewAllClaims();

        // ─────────────────────────────────────────────────────────────────────
        // SUMMARY
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO COMPLETE                                             ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  ✓ Claim #1: APPROVED - Ownership transferred to finder    ║");
        System.out.println("║  ✗ Claim #2: REJECTED - Item available for re-matching    ║");
        System.out.println("║  ✓ Full lifecycle demonstrated: Match → Claim → Verify    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SAMPLE DATA
    // ─────────────────────────────────────────────────────────────────────────

    private static List<Item> buildSampleStore() {
        List<Item> store = new ArrayList<>();

        // Lost Items
        LostItem l1 = new LostItem(
                1,
                "Black Wallet",
                "black leather wallet with student ID inside",
                "2026-04-01",
                "Swathi",
                "9876543210",
                "Campus",
                "Main Gate",
                "2026-03-31"
        );

        LostItem l2 = new LostItem(
                2,
                "Silver Watch",
                "silver wristwatch with metal strap",
                "2026-04-03",
                "Rahul",
                "9123456780",
                "Campus",
                "Cafeteria",
                "2026-04-02"
        );

        LostItem l3 = new LostItem(
                3,
                "Blue Backpack",
                "blue backpack containing notebooks and charger",
                "2026-04-06",
                "Ananya",
                "9988776655",
                "Campus",
                "Library",
                "2026-04-05"
        );

        // Found Items
        FoundItem f1 = new FoundItem(
                101,
                "Wallet",
                "found a black leather wallet near gate",
                "2026-04-02",
                "Security",
                "9000000001",
                "Campus",
                "Main Gate",
                "2026-04-01"
        );

        FoundItem f2 = new FoundItem(
                102,
                "Watch",
                "silver watch found near cafeteria exit",
                "2026-04-04",
                "Staff",
                "9000000002",
                "Campus",
                "Cafeteria",
                "2026-04-03"
        );

        FoundItem f3 = new FoundItem(
                103,
                "Backpack",
                "navy blue backpack found in library",
                "2026-04-07",
                "Student",
                "9000000003",
                "Campus",
                "Library",
                "2026-04-06"
        );

        // Add all to store
        store.add(l1);
        store.add(l2);
        store.add(l3);
        store.add(f1);
        store.add(f2);
        store.add(f3);

        return store;
    }
}
