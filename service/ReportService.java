package service;

import java.util.List;
import model.Item;
import model.claim.Claim;

public class ReportService {

    public String generateReport(List<Item> items, List<Claim> claims) {
        String report = "\n===== SYSTEM REPORT =====\n";
        report += "Total Items: " + items.size() + "\n";
        report += "Total Claims: " + claims.size() + "\n";

        return report; 
    }

    public void exportReport(String report) {
        System.out.println(report);  // ✔ print only once
        System.out.println("Exporting Report...");
    }
}