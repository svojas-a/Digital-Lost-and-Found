package service;

import model.Item;
import model.claim.Claim;

public class AdminService {

    public void verifyClaim(Claim claim, boolean approve) {
        // Only logic, no printing
        // (actual status handled in ClaimService)
    }

    public void closeItem(Item item) {
        if (item != null) {
            item.updateStatus("Closed");
        }
    }

    public void generateReports() {
        // logic only (printing will be handled in View)
    }
}