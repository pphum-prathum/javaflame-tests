/* Copyright (C) Saruta Nakro - All Rights Reserved
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package com.github.beothorn.agent.recorder;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

public class FunctionCallRecorderWithValueCapturing_BCC_Test {

    @BeforeEach
    void setup() {
        System.out.println("Starting BCC test...");
    }

    @AfterEach
    void teardown() {
        System.out.println("Test completed.");
    }

    // ---------------- T1: Normal object input ----------------
    // C1: ≠ null, C2: Not array, C3: Non-array, C4: No error
    @Test
    public void t1_valueIsNormalString_returnsSameString() {
        String result = FunctionCallRecorderWithValueCapturing.getValueAsString("Hello");
        assertEquals("Hello", result);
    }

    // ---------------- T2: Exception during toString() ----------------
    // C1: ≠ null, C2: Array, C3: Non-array, C4: Has error
    @Test
    public void t2_valueThrowsException_returnsARG_TOSTRING_EXCEPTION() {
        Object badObject = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Fail to stringify");
            }
        };
        String result = FunctionCallRecorderWithValueCapturing.getValueAsString(badObject);
        assertTrue(result.startsWith("ARG_TOSTRING_EXCEPTION"));
    }

    // ---------------- T3: Empty array ----------------
    // C1: ≠ null, C2: Not array, C3: Empty array, C4: No error
    @Test
    public void t3_valueIsEmptyArray_returnsEmptyBracket() {
        int[] empty = {};
        String result = FunctionCallRecorderWithValueCapturing.getValueAsString(empty);
        assertEquals("[]", result);
    }

    // ---------------- T4: Object array ----------------
    // C1: ≠ null, C2: Array, C3: Object array, C4: No error
    @Test
    public void t4_valueIsObjectArray_returnsArrayToString() {
        String[] arr = {"A", "B"};
        String result = FunctionCallRecorderWithValueCapturing.getValueAsString(arr);
        assertEquals(Arrays.toString(arr), result);
    }

    // ---------------- T5: Primitive array ----------------
    // C1: ≠ null, C2: Not array, C3: Primitive array, C4: No error
    @Test
    public void t5_valueIsPrimitiveIntArray_returnsArrayToString() {
        int[] nums = {1, 2, 3};
        String result = FunctionCallRecorderWithValueCapturing.getValueAsString(nums);
        assertEquals(Arrays.toString(nums), result);
    }

    // ---------------- T6: Mixed-type array ----------------
    // C1: ≠ null, C2: Array, C3: Non-array, C4: No error
    @Test
    public void t6_valueIsMixedArray_returnsReadableString() {
        Object[] mixed = {1, "A", null};
        String result = FunctionCallRecorderWithValueCapturing.getValueAsString(mixed);
        assertEquals("[1, A, null]", result);
    }

    // ---------------- T7: Null input ----------------
    // C1: null, C2: Array, C3: Non-array, C4: No error
    @Test
    public void t7_valueIsNull_returnsNullString() {
        String result = FunctionCallRecorderWithValueCapturing.getValueAsString(null);
        assertEquals("null", result);
    }
}
