
import model.*;
import model.claim.Claim;
import model.claim.Verification;
import controller.SystemController;
import model.claim.VerificationDecision;
import model.claim.VerificationMethod;

import java.util.*;

import service.SearchService;
import service.BasicMatchingStrategy;
import service.MatchingStrategy;

import service.claim.ClaimService;
import service.impl.InMemoryClaimRepository;

public class Main {
    public static void main(String[] args) {

        // ===============================
        // 1. CREATE ITEMS (Person 1)
        // ===============================
        Item item1 = ItemFactory.createItem(
                "lost",
                1,
                "Wallet",
                "Black leather wallet",
                "2026-04-01",
                "",
                "",
                "Library",
                "Library",
                "2026-03-30"
        );

        // 🔥 IMPORTANT: Make items SIMILAR so match happens
        Item item2 = ItemFactory.createItem(
                "found",
                2,
                "Wallet", // same name
                "Black wallet found near library", // similar description
                "2026-04-01",
                "",
                "",
                "Library", // same location
                "Library",
                "2026-04-01"
        );

        System.out.println("=== ITEMS CREATED ===");
        item1.displayItem();
        item2.displayItem();

        item1.setOwnerName("User1");
        item1.setContactInfo("9876543210");

        item2.setOwnerName("Finder1");   //  THIS FIXES ERROR
        item2.setContactInfo("9999999999");


        // ===============================
        // 2. CREATE ITEM STORE
        // ===============================
        List<Item> itemStore = new ArrayList<>();
        itemStore.add(item1);
        itemStore.add(item2);


        // ===============================
        // 3. SEARCH (Person 2)
        // ===============================
        MatchingStrategy strategy = new BasicMatchingStrategy();

        SearchService searchService = new SearchService(itemStore, strategy);

        List<MatchResult> matches = searchService.findAllMatches();


        // ===============================
        // 4. CONTROLLER (Person 4)
        // ===============================
        SystemController systemController = new SystemController();

        if (!matches.isEmpty()) {
            for (MatchResult match : matches) {
                systemController.onMatchFound(item1.getOwnerName());
            }
        }


        // ===============================
        // 5. CLAIM (Person 3)
        // ===============================
        InMemoryClaimRepository repo = new InMemoryClaimRepository();
        ClaimService claimService = new ClaimService(repo);

        List<Claim> claims = new ArrayList<>();

        for (MatchResult match : matches) {

    Claim claim = claimService.createClaimFromMatch(match);
    claims.add(claim);

    // Step 1: request claim
    claimService.requestClaim(claim.getClaimId(), "User1");

    // Step 2: start verification
    claimService.startVerification(claim.getClaimId(), "Admin");

    // 🔥 Step 3: CREATE PROPER VERIFICATION OBJECT
    Verification verification = new Verification(
        1,                                      // verificationId (dummy)
        claim.getClaimId(),                     // claimId
        "V1",                                   // verifierId
        "Admin",                                // verifierName
        java.time.LocalDateTime.now().toString(), // date
        VerificationMethod.MANUAL,              // method
        "Valid claim",                          // notes
        VerificationDecision.APPROVED,          // 🔥 IMPORTANT
        95,                                     // confidence
        null                                    // rejectionReason
    );

    // 🔥 Step 4: ADD VERIFICATION
    claim.addVerification(verification);

    // Step 5: approve claim
    //claimService.approveClaim(claim.getClaimId());
}


        // ===============================
        // 6. CLAIM EVENTS (Person 4)
        // ===============================
        if (!claims.isEmpty()) {

    Claim approvedClaim = claims.get(0);   // 🔥 get real claim

   systemController.onClaimSubmitted("Admin");

    System.out.println("\n=== ADMIN DECISION ===");
    System.out.println("Admin approved the claim manually.");

    claimService.approveClaim(approvedClaim.getClaimId());

    systemController.onClaimApproved(
        item1,
        approvedClaim.getClaimantName()
);
}


        // ===============================
        // 7. FINAL STATUS
        // ===============================
        System.out.println("\n=== FINAL ITEM STATUS ===");
        item1.displayItem();


        // ===============================
        // 8. REPORT (Person 4)
        // ===============================
        systemController.generateSystemReport();
    }
}

