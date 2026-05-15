/* Copyright (C) 2025 Phanphum Prathumsuwan - All Rights Reserved
 * You may use, distribute and modify this code under the terms of the MIT license.
 */
package com.github.beothorn.agent.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;

import static com.github.beothorn.agent.parser.Token.*;
import static com.github.beothorn.agent.parser.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * All-Combination coverage (2 × 2 × 2 = 8)
 * Dimensions:
 *   - leftNegated: false / true
 *   - leftGrouped: false / true
 *   - op: AND / OR
 *
 * This systematically combines negation, grouping, and binary operators.
 * Covers Parser.parseBinaryOperand(), Parser.parseUnaryOperand(), and Parser.parseUntilClose().
 */
class Parser_AllComb_NegGroupOpTest {

    /** Simple record to hold one combination. */
    record Row(boolean leftNegated, boolean leftGrouped, String op) {}

    /** The 8 total combinations (2×2×2). */
    static Stream<Row> rows() {
        return Stream.of(
                new Row(false, false, "AND"),
                new Row(true,  false, "AND"),
                new Row(false, true,  "AND"),
                new Row(true,  true,  "AND"),
                new Row(false, false, "OR"),
                new Row(true,  false, "OR"),
                new Row(false, true,  "OR"),
                new Row(true,  true,  "OR")
        );
    }

    /** Builds a token deque for a given combination. */
    private static Deque<Token> make(Row r) {
        var list = new java.util.ArrayList<Token>();

        // Optional negation and grouping on the left-hand expression
        if (r.leftNegated) list.add(not());
        if (r.leftGrouped) list.add(openParen());
        list.add(string("foo"));
        if (r.leftGrouped) list.add(closeParen());

        // Operator under test
        list.add("AND".equals(r.op) ? and() : or());

        // Right-hand expression always simple
        list.add(string("bar"));
        return new ArrayDeque<>(list);
    }

    @ParameterizedTest
    @MethodSource("rows")
    void astShape_matchesCombination(Row r) throws Exception {
        var ast = Parser.parse(make(r));

        // Root: operator
        assertEquals("AND".equals(r.op) ? OPERATOR_AND : OPERATOR_OR, ast.token.type,
                "Root should match the chosen operator");

        // Right: simple string leaf
        assertEquals(STRING_VALUE, ast.children[1].token.type);
        assertEquals("bar", ast.children[1].token.value);

        // Left: depends on negation
        if (!r.leftNegated) {
            // no negation — direct string leaf
            assertEquals(STRING_VALUE, ast.children[0].token.type);
            assertEquals("foo", ast.children[0].token.value);
        } else {
            // negation — NOT node whose single child is the string leaf
            assertEquals(OPERATOR_NOT, ast.children[0].token.type);
            assertEquals(1, ast.children[0].children.length);
            assertEquals(STRING_VALUE, ast.children[0].children[0].token.type);
            assertEquals("foo", ast.children[0].children[0].token.value);
        }

        // Grouping does not alter AST token types,
        // but grouped cases still exercise parseUntilClose() internally.
    }
}
