package controller;

import service.AdminService;
import service.NotificationService;
import service.ReportService;
import view.ConsoleView;

import model.Item;

import java.util.ArrayList;
import java.util.List;
import model.claim.Claim;

public class SystemController {

    private AdminService adminService = new AdminService();
    private NotificationService notificationService = new NotificationService();
    private ReportService reportService = new ReportService();
    private ConsoleView view = new ConsoleView();
    private List<Claim> claims = new ArrayList<>();

    // When match found
    public void onMatchFound(String user) {
    view.showSection("MATCH FOUND");

    notificationService.sendNotification(user, "Match found for your item!"); // ✔ STORE

    view.showNotification(user, "Match found for your item!"); // ✔ DISPLAY
   }

    // When claim submitted
    public void onClaimSubmitted(String admin) {
    view.showSection("CLAIM SUBMITTED");

    notificationService.sendNotification(admin, "New claim submitted!");
    view.showNotification(admin, "New claim submitted!");

    //  AUTO ADD CLAIM
    claims.add(null);  // simulate claim creation
}
    public void markNotificationAsRead(int index) {
        String msg = notificationService.markAsRead(index);
        view.showAdminAction("Marked as Read: " + msg);  // ✔ View handles print

    
    }

    // FIXED: Removed Claim parameter
    public void onClaimApproved(Item item, String user) {

    view.showSection("CLAIM APPROVED");

    view.showAdminAction("Admin verified and approved claim");

    // ✔ STORE notification
    notificationService.sendNotification(user, "Your claim has been approved!");

    // ✔ DISPLAY notification
    view.showNotification(user, "Your claim has been approved!");

    if (item != null) {
        item.updateStatus("Closed");   // ✔ FIXED
        view.showItemClosed(item.getItemId());
    }
}

    // Reporting
    public void generateSystemReport(List<Item> items, List<Claim> claims) {
        String report = reportService.generateReport(items, claims);
        reportService.exportReport(report);
    }
}