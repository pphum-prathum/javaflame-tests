/* Copyright (C) 2025 Chalisa Kengkaewphennapa (Nine) - All Rights Reserved
 * You may use, distribute and modify this code under the terms of the MIT license.
 */
package com.github.beothorn.agent.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * ECC (Each-Choice Coverage) over Token factories - minimal test cases
 * Each @Test contains only one assertion. Setup/teardown included.
 */
class TokenECCTest {

    private Token tokenA;
    private Token tokenB;

    @BeforeEach
    void setUp() {
        // Setup placeholder tokens; actual tokens created in each test
        tokenA = null;
        tokenB = null;
    }

    @AfterEach
    void tearDown() {
        // Clear references
        tokenA = null;
        tokenB = null;
    }

    @Test
    @DisplayName("ECC-1: and() equals and()")
    void and_equals_and() {
        // Test case to check if two `and()` tokens are equal.
        // This validates that the equality check for two identical `and()` tokens returns true.
        tokenA = Token.and();
        tokenB = Token.and();
        assertEquals(tokenA, tokenB); // Assert that both tokens are equal
    }

    @Test
    @DisplayName("ECC-2: string('foo') != string('bar')")
    void string_not_equal() {
        // Test case to check if two `string()` tokens with different values are not equal.
        // This ensures that the equality check for two different `string()` tokens returns false.
        tokenA = Token.string("foo");
        tokenB = Token.string("bar");
        assertNotEquals(tokenA, tokenB); // Assert that the tokens are not equal due to different values
    }
}

