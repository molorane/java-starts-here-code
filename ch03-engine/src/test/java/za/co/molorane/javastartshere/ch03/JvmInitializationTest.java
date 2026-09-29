package za.co.molorane.javastartshere.ch03;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch03.jvm.ClassInitializationSequence;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 3: JVM Class Loading & Initialization Sequence Tests")
class JvmInitializationTest {

    @Test
    @DisplayName("Should execute <clinit> static blocks before <init> instance blocks in parent-then-child order")
    void shouldExecuteInCanonicalJvmOrder() {
        List<String> sequence = ClassInitializationSequence.captureInitializationSequence();

        // Verifies the exact sequence emitted by the JVM specification:
        // 1. Parent <clinit> static block (during class loading & linking)
        // 2. Child <clinit> static block
        // 3. Parent <init> instance block
        // 4. Parent constructor
        // 5. Child <init> instance block
        // 6. Child constructor
        assertThat(sequence).containsExactly(
                "1. Parent <clinit> static block",
                "2. Child <clinit> static block",
                "3. Parent <init> instance block",
                "4. Parent constructor",
                "5. Child <init> instance block",
                "6. Child constructor"
        );
    }
}
