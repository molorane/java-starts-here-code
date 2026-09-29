package za.co.molorane.javastartshere.ch09;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Programmatic JVM thread and deadlock diagnostics.
 * Mirrors the forensic analysis performed by {@code jcmd <PID> Thread.print}.
 */
public class ThreadDeadlockDetector {

    private final ThreadMXBean threadMXBean;

    public ThreadDeadlockDetector() {
        this(ManagementFactory.getThreadMXBean());
    }

    public ThreadDeadlockDetector(ThreadMXBean threadMXBean) {
        this.threadMXBean = threadMXBean;
    }

    public record DeadlockReport(
            long threadId,
            String threadName,
            Thread.State threadState,
            String lockName,
            String lockOwnerName,
            long lockOwnerId,
            List<String> stackTrace
    ) {}

    /**
     * Inspects the JVM for circular monitor or synchronizer deadlocks.
     *
     * @return a list of {@link DeadlockReport} entries describing the stuck threads, or empty if healthy
     */
    public List<DeadlockReport> detectDeadlocks() {
        long[] deadlockedIds = threadMXBean.findDeadlockedThreads();
        if (deadlockedIds == null || deadlockedIds.length == 0) {
            return Collections.emptyList();
        }

        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(deadlockedIds, true, true);
        List<DeadlockReport> reports = new ArrayList<>();

        for (ThreadInfo info : threadInfos) {
            if (info == null) {
                continue;
            }
            List<String> stack = new ArrayList<>();
            for (StackTraceElement element : info.getStackTrace()) {
                stack.add(element.toString());
            }

            reports.add(new DeadlockReport(
                    info.getThreadId(),
                    info.getThreadName(),
                    info.getThreadState(),
                    info.getLockName(),
                    info.getLockOwnerName(),
                    info.getLockOwnerId(),
                    stack
            ));
        }

        return reports;
    }

    /**
     * Formats a diagnostic thread dump summary string in {@code jcmd} style.
     */
    public String generateDeadlockDiagnosticText() {
        List<DeadlockReport> reports = detectDeadlocks();
        if (reports.isEmpty()) {
            return "No Java-level deadlocks detected.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(reports.size()).append(" deadlocked thread(s):\n");
        sb.append("===================================================\n");
        for (DeadlockReport report : reports) {
            sb.append(String.format("\"%s\" #%d [%s]\n", report.threadName(), report.threadId(), report.threadState()));
            sb.append(String.format("   waiting to lock: %s\n", report.lockName()));
            sb.append(String.format("   held by: \"%s\" #%d\n", report.lockOwnerName(), report.lockOwnerId()));
            for (String line : report.stackTrace()) {
                sb.append("      at ").append(line).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
