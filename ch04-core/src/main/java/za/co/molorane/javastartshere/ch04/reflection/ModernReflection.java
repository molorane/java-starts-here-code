package za.co.molorane.javastartshere.ch04.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates Modern Reflection, Record Components, and MethodHandles.
 * <p>
 * Key Concepts (Chapter 4 Topic 6):
 * <ul>
 *   <li><b>MethodHandles:</b> Type-safe, high-performance runtime invocation mechanism that shares
 *       the JVM JIT optimization pathways, bypassing traditional reflection boxing and access checks.</li>
 *   <li><b>Record Reflection:</b> Native Java API for inspecting record components without guessing
 *       getter naming conventions.</li>
 * </ul>
 */
public class ModernReflection {

    public record UserProfile(String username, String role, boolean active) {}

    /**
     * Extracts record component names using modern Record reflection APIs.
     */
    public static List<String> extractRecordFieldNames(Class<?> recordClass) {
        if (!recordClass.isRecord()) {
            throw new IllegalArgumentException("Class must be a Java Record");
        }
        return Arrays.stream(recordClass.getRecordComponents())
                .map(RecordComponent::getName)
                .toList();
    }

    /**
     * Invokes a method using high-performance MethodHandle lookup.
     */
    public static String invokeViaMethodHandle(UserProfile profile) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle handle = lookup.findVirtual(UserProfile.class, "username", MethodType.methodType(String.class));
        return (String) handle.invokeExact(profile);
    }
}
