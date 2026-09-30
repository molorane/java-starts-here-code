package za.co.molorane.javastartshere.ch03.memory;

import java.util.List;

/**
 * Demonstrates C2 JIT Escape Analysis and Scalar Replacement.
 * <p>
 * Key Concepts:
 * <ul>
 *   <li><b>Non-Escaping Object:</b> When an object reference does not escape the method stack frame
 *       (not returned, not stored in an external field or collection, not passed to another thread),
 *       the C2 JIT compiler dissolves the object via <b>Scalar Replacement</b>. Its fields are mapped
 *       directly to CPU registers or the method stack frame, completely eliminating heap allocation.</li>
 *   <li><b>Global Escape:</b> When an object reference escapes into a shared collection or return value,
 *       the JVM must allocate it on the heap with a 12-to-16 byte object header and track it for GC.</li>
 * </ul>
 */
public class EscapeAnalysisDemo {

    /**
     * Coordinate record used for geometric calculations.
     */
    public record Point(double x, double y) { }

    /**
     * Non-escaping method:
     * The Point instances p1 and p2 exist solely within this method stack frame.
     * Under C2 JIT compilation, Escape Analysis marks them as NoEscape, and Scalar Replacement
     * unpacks 'x' and 'y' directly into CPU registers. Zero heap allocation occurs.
     */
    public static double calculateEuclideanDistance(double x1, double y1, double x2, double y2) {
        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);
        double dx = p1.x() - p2.x();
        double dy = p1.y() - p2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Escaping method:
     * The Point reference escapes the method scope by being stored in an external sink list.
     * Escape Analysis marks it as GlobalEscape, requiring full heap allocation.
     */
    public static void storeEscapedPoint(double x, double y, List<Point> sink) {
        Point p = new Point(x, y);
        sink.add(p); // Escapes to external caller!
    }
}
