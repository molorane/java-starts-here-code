package za.co.molorane.javastartshere.ch03;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch03.concurrency.VirtualThreadPinningMitigation;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 3: Virtual Thread Pinning & Lock Mitigation Tests")
class VirtualThreadPinningTest {

    @Test
    @DisplayName("Should execute 200 concurrent tasks safely with ReentrantLock without carrier pinning")
    void shouldExecuteConcurrentTasksWithLock() throws InterruptedException {
        int tasks = 200;
        int completed = VirtualThreadPinningMitigation.runConcurrentLockTasks(tasks);

        assertThat(completed).isEqualTo(tasks);
    }
}
