package za.co.molorane.javastartshere.ch03.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the JVM class initialization lifecycle (&lt;clinit&gt; vs &lt;init&gt;).
 * <p>
 * Key Concepts (Chapter 3 Topic 2):
 * <ul>
 *   <li><b>Class Initialization (&lt;clinit&gt;):</b> Executed once when the class is first loaded
 *       and linked. Executes static variable initializers and static initialization blocks
 *       in textual order, from parent class to child class.</li>
 *   <li><b>Instance Initialization (&lt;init&gt;):</b> Executed every time a new instance is created via {@code new}.
 *       Executes parent instance blocks and constructor, then child instance blocks and constructor.</li>
 * </ul>
 */
public class ClassInitializationSequence {

    public static final List<String> EXECUTION_LOG = new ArrayList<>();

    public static class Parent {
        static {
            EXECUTION_LOG.add("1. Parent <clinit> static block");
        }

        {
            EXECUTION_LOG.add("3. Parent <init> instance block");
        }

        public Parent() {
            EXECUTION_LOG.add("4. Parent constructor");
        }
    }

    public static class Child extends Parent {
        static {
            EXECUTION_LOG.add("2. Child <clinit> static block");
        }

        {
            EXECUTION_LOG.add("5. Child <init> instance block");
        }

        public Child() {
            EXECUTION_LOG.add("6. Child constructor");
        }
    }

    /**
     * Triggers child class initialization and instantiation to capture the JVM sequence.
     */
    public static List<String> captureInitializationSequence() {
        EXECUTION_LOG.clear();
        new Child();
        return List.copyOf(EXECUTION_LOG);
    }
}
