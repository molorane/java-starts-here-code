package za.co.molorane.javastartshere.ch03;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 3: The Java Engine Tests")
class EngineTest {

    @Test
    @DisplayName("Should execute 10,000 virtual threads concurrently with minimal overhead")
    void shouldExecuteThousandsOfVirtualThreads() throws InterruptedException {
        int tasks = 10_000;
        int completed = VirtualThreadEngine.executeConcurrentTasks(tasks);
        assertThat(completed).isEqualTo(tasks);
    }
}
