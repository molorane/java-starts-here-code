package za.co.molorane.javastartshere.ch04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch04.exceptions.ExceptionPhysics;
import za.co.molorane.javastartshere.ch04.exceptions.ExceptionPhysics.StacklessValidationException;
import za.co.molorane.javastartshere.ch04.exceptions.ExceptionPhysics.StandardBusinessException;
import za.co.molorane.javastartshere.ch04.strings.StringPoolInterning;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 4: Strings, String Pool & Exception Physics Tests")
class StringsAndExceptionsTest {

    @Test
    @DisplayName("Should demonstrate String Pool reference equality vs Heap allocation")
    void shouldVerifyStringPoolMechanics() {
        var result = StringPoolInterning.compareStringAllocation();

        assertThat(result.literalSameRef()).isTrue();
        assertThat(result.heapDifferentRef()).isTrue();
        assertThat(result.internedSameRef()).isTrue();
    }

    @Test
    @DisplayName("Stackless exceptions should have empty stack trace elements, avoiding native stack walk")
    void stacklessExceptionsShouldAvoidNativeStackWalk() {
        StandardBusinessException standard = new StandardBusinessException("Standard error");
        StacklessValidationException stackless = new StacklessValidationException("High throughput error");

        assertThat(standard.getStackTrace()).isNotEmpty();
        assertThat(stackless.getStackTrace()).isEmpty();
    }

    @Test
    @DisplayName("Try-with-resources should attach close() failure as a suppressed exception")
    void tryWithResourcesShouldCaptureSuppressedExceptions() {
        Throwable caught = ExceptionPhysics.executeWithSuppression(true, true);

        assertThat(caught).isNotNull();
        assertThat(caught.getMessage()).isEqualTo("Primary try-block operation failed");

        // Verify suppressed exception attached during close()
        Throwable[] suppressed = caught.getSuppressed();
        assertThat(suppressed).hasSize(1);
        assertThat(suppressed[0]).hasMessage("Resource close cleanup failed");
    }
}
