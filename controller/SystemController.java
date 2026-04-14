package controller;

import service.AdminService;
import service.NotificationService;
import service.ReportService;
import view.ConsoleView;

import model.Item;
import model.claim.Claim;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SystemController {

    private AdminService adminService = new AdminService();
    private NotificationService notificationService = new NotificationService();
    private ReportService reportService = new ReportService();
    private ConsoleView view = new ConsoleView();

    private List<Claim> claims = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

    // ================================================
    // ADD LOST ITEM  — called from Server.java
    // parts: [name, category, person, location, desc]
    // Returns true if a match was found
    // ================================================
    public boolean addLostItem(String name, String[] parts) {

        String category = parts.length > 1 ? parts[1] : "Other";
        String person   = parts.length > 2 ? parts[2] : "Unknown";
        String location = parts.length > 3 ? parts[3] : "Unknown";
        String desc     = parts.length > 4 ? parts[4] : "";

        view.showSection("NEW LOST ITEM");

        Item lostItem = new Item(
            items.size() + 1,
            name,
            "Lost item — " + desc,
            LocalDate.now().toString(),
            "Reported",
            location,
            person
        );

        items.add(lostItem);
        System.out.println(lostItem);

        return checkForMatches(lostItem);
    }

    // ================================================
    // ADD FOUND ITEM — called from Server.java
    // Returns true if a match was found
    // ================================================
    public boolean addFoundItem(String name, String[] parts) {

        String category = parts.length > 1 ? parts[1] : "Other";
        String person   = parts.length > 2 ? parts[2] : "Unknown";
        String location = parts.length > 3 ? parts[3] : "Unknown";
        String desc     = parts.length > 4 ? parts[4] : "";

        view.showSection("NEW FOUND ITEM");

        Item foundItem = new Item(
            items.size() + 1,
            name,
            "Found item — " + desc,
            LocalDate.now().toString(),
            "Reported",
            location,
            person
        );

        items.add(foundItem);
        System.out.println(foundItem);

        return checkForMatches(foundItem);
    }

    // ================================================
    // SUBMIT CLAIM — new endpoint
    // ================================================
    public void submitClaim(String itemName, String claimerName, String contact, String proof) {

        view.showSection("CLAIM SUBMITTED");

        System.out.println("  Item    : " + itemName);
        System.out.println("  Claimer : " + claimerName);
        System.out.println("  Contact : " + contact);
        System.out.println("  Proof   : " + proof);

        notificationService.sendNotification("Admin",
            "New claim for '" + itemName + "' by " + claimerName);
        view.showNotification("Admin",
            "New claim for '" + itemName + "' by " + claimerName);

        // Find the matching found item and mark it claimed
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName) && item.getStatus().equals("Reported")) {
                onClaimApproved(item, claimerName);
                return;
            }
        }

        // If item not found locally, still log the claim
        claims.add(null);
    }

    // ================================================
    // MATCHING LOGIC
    // Returns true if at least one match found
    // ================================================
    private boolean checkForMatches(Item newItem) {

        boolean matchFound = false;

        for (Item item : items) {
            if (item == newItem) continue;
            if (item.getName().equalsIgnoreCase(newItem.getName())) {
                matchFound = true;
                onMatchFound(item.getOwner());
                onClaimSubmitted("Admin");
                onClaimApproved(item, newItem.getOwner());
            }
        }
        return matchFound;
    }

    // ================================================
    // EVENTS
    // ================================================
    public void onMatchFound(String user) {
        view.showSection("MATCH FOUND");
        notificationService.sendNotification(user, "Match found for your item!");
        view.showNotification(user, "Match found for your item!");
    }

    public void onClaimSubmitted(String admin) {
        view.showSection("CLAIM SUBMITTED");
        notificationService.sendNotification(admin, "New claim submitted!");
        view.showNotification(admin, "New claim submitted!");
        claims.add(null);
    }

    public void onClaimApproved(Item item, String user) {
        view.showSection("CLAIM APPROVED");
        view.showAdminAction("Admin verified and approved claim");
        notificationService.sendNotification(user, "Your claim has been approved!");
        view.showNotification(user, "Your claim has been approved!");
        if (item != null) {
            item.updateStatus("Closed");
            view.showItemClosed(item.getItemId());
        }
    }

    // ================================================
    // REPORT
    // ================================================
    public void generateSystemReport() {
        String report = reportService.generateReport(items, claims);
        reportService.exportReport(report);
    }

    public void markNotificationAsRead(int index) {
        String msg = notificationService.markAsRead(index);
        view.showAdminAction("Marked as Read: " + msg);
    }
}