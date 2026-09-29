package za.co.molorane.javastartshere.ch10;

/**
 * Computes and validates JVM container memory ergonomics.
 *
 * Prevents Linux Cgroup Out-Of-Memory (OOM) killer terminations (Exit Code 137)
 * by allocating 70% to 75% of container RAM to the heap, reserving a mandatory
 * 25% to 30% cushion for off-heap structures (Metaspace, thread stacks, direct buffers, JIT cache).
 */
public record ContainerErgonomicsCalculator(
        long containerMemoryLimitMb,
        double maxRamPercentage
) {

    public static final double DEFAULT_RECOMMENDED_MAX_RAM_PERCENTAGE = 75.0;
    public static final double MAXIMUM_SAFE_RAM_PERCENTAGE = 80.0;

    public ContainerErgonomicsCalculator {
        if (containerMemoryLimitMb <= 0) {
            throw new IllegalArgumentException("Container memory limit must be strictly positive");
        }
        if (maxRamPercentage <= 0.0 || maxRamPercentage > 100.0) {
            throw new IllegalArgumentException("MaxRAMPercentage must be between 0.0 and 100.0");
        }
        if (maxRamPercentage > MAXIMUM_SAFE_RAM_PERCENTAGE) {
            throw new IllegalStateException(
                    "Unsafe MaxRAMPercentage (" + maxRamPercentage + "% > " + MAXIMUM_SAFE_RAM_PERCENTAGE
                            + "%). Risk of Linux cgroup OOM killer abort (Exit 137) due to off-heap starvation!"
            );
        }
    }

    public long calculateMaxHeapMemoryMb() {
        return Math.round(containerMemoryLimitMb * (maxRamPercentage / 100.0));
    }

    public long calculateOffHeapHeadroomMb() {
        return containerMemoryLimitMb - calculateMaxHeapMemoryMb();
    }

    public boolean isOffHeapHeadroomSufficient(long estimatedOffHeapRequirementMb) {
        return calculateOffHeapHeadroomMb() >= estimatedOffHeapRequirementMb;
    }
}
