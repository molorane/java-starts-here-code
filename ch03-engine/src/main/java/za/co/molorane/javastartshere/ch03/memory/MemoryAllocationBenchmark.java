package za.co.molorane.javastartshere.ch03.memory;

import java.util.List;

/**
 * Demonstrates JVM heap allocation pressure and memory churn.
 * <p>
 * Key Concepts:
 * <ul>
 *   <li><b>String Churn Anti-Pattern:</b> Using '+' inside loops repeatedly instantiates
 *       intermediate StringBuilder and byte[] arrays, flooding Eden space and triggering
 *       frequent GC minor collections.</li>
 *   <li><b>Pre-sized StringBuilder:</b> Allocating a buffer with calculated capacity
 *       avoids array resizing copies and reduces heap churn by over 90%.</li>
 * </ul>
 */
public class MemoryAllocationBenchmark {

    public record AuditTransaction(String id, double amount, String status) { }

    /**
     * Anti-pattern: String concatenation in a loop.
     * Generates O(N) temporary StringBuilder and String instances on the heap.
     */
    public static String buildAuditRecordSlow(List<AuditTransaction> transactions) {
        String report = "TRANSACTIONS:\n";
        for (AuditTransaction tx : transactions) {
            report += "ID: " + tx.id()
                    + " | Amount: " + tx.amount()
                    + " | Status: " + tx.status()
                    + "\n";
        }
        return report;
    }

    /**
     * Optimized pattern: Pre-sized StringBuilder.
     * Single continuous buffer allocation; zero intermediate heap garbage.
     */
    public static String buildAuditRecordFast(List<AuditTransaction> transactions) {
        // Average line estimate ~ 64 bytes
        StringBuilder report = new StringBuilder(transactions.size() * 64 + 16);
        report.append("TRANSACTIONS:\n");

        for (AuditTransaction tx : transactions) {
            report.append("ID: ").append(tx.id())
                    .append(" | Amount: ").append(tx.amount())
                    .append(" | Status: ").append(tx.status())
                    .append('\n');
        }
        return report.toString();
    }
}
