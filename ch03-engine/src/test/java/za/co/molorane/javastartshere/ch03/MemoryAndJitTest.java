package za.co.molorane.javastartshere.ch03;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch03.memory.EscapeAnalysisDemo;
import za.co.molorane.javastartshere.ch03.memory.EscapeAnalysisDemo.Point;
import za.co.molorane.javastartshere.ch03.memory.MemoryAllocationBenchmark;
import za.co.molorane.javastartshere.ch03.memory.MemoryAllocationBenchmark.AuditTransaction;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 3: JVM Memory Pressure, Churn & Escape Analysis Tests")
class MemoryAndJitTest {

    @Test
    @DisplayName("Should produce identical audit log output with both slow churn and fast pre-sized builder")
    void shouldProduceConsistentReportOutput() {
        List<AuditTransaction> txList = List.of(
                new AuditTransaction("TX-101", 150.00, "CONFIRMED"),
                new AuditTransaction("TX-102", 49.99, "PENDING"),
                new AuditTransaction("TX-103", 999.00, "SETTLED")
        );

        String slowOutput = MemoryAllocationBenchmark.buildAuditRecordSlow(txList);
        String fastOutput = MemoryAllocationBenchmark.buildAuditRecordFast(txList);

        assertThat(fastOutput)
                .isNotBlank()
                .isEqualTo(slowOutput)
                .contains("TX-101", "150.0", "CONFIRMED");
    }

    @Test
    @DisplayName("Non-escaping Point records calculate distance cleanly without escaping stack frame")
    void nonEscapingObjectsExecuteWithoutLeak() {
        double distance = EscapeAnalysisDemo.calculateEuclideanDistance(0.0, 0.0, 3.0, 4.0);
        assertThat(distance).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Escaping Point records escape into external collection")
    void escapingObjectsAreCapturedBySink() {
        List<Point> sink = new ArrayList<>();
        EscapeAnalysisDemo.storeEscapedPoint(10.0, 20.0, sink);

        assertThat(sink)
                .hasSize(1)
                .first()
                .isEqualTo(new Point(10.0, 20.0));
    }
}
