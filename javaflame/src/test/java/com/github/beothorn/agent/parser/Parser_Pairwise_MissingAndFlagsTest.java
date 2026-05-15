/* Copyright (C) 2025 Phanphum Prathumsuwan - All Rights Reserved
 * You may use, distribute and modify this code under the terms of the MIT license.
 */
package com.github.beothorn.agent.parser;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static com.github.beothorn.agent.parser.Token.*;
import static org.junit.jupiter.api.Assertions.*;

class Parser_Pairwise_MissingAndFlagsTest {

    private static Deque<Token> dq(Token... ts) {
        return new ArrayDeque<>(List.of(ts));
    }

    // ---------- ERROR PATHS MISSING FROM ParserTest ----------

    /**
     * PWC Test: T1 (Error Path)
     * Purpose: Checks the parser throws an error and provides a helpful message when the
     * function matcher (##) token is present but is immediately followed by EOF (missing RHS).
     * PWC Coverage: C1(Yes/RHS Required) x C2(Missing/Invalid) x C3(Balanced/N/A)
     */
    @Test
    void functionMatcher_noNextToken_afterHash_throwsHelpfulMessage() {
        var tokens = dq(functionMatcher()); // ## <EOF>
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertTrue(ex.getMessage().startsWith("No expression after function matcher start"),
                "Expected 'No expression after function matcher start ##' but got: " + ex.getMessage());
    }

    /**
     * PWC Test: T2 (Error Path)
     * Purpose: Checks the parser throws an error when a function name token is followed
     * by an argument (string) instead of the required open parenthesis '('.
     * PWC Coverage: C1(Yes/RHS Required) x C2(Missing/Invalid) x C3(Balanced/N/A)
     */
    @Test
    void functionCall_withoutOpenParen_throws() {
        var tokens = dq(function("startsWith"), string("bar")); // startsWith bar
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertTrue(ex.getMessage().startsWith("Open parenthesis required after function start"),
                "Expected 'Open parenthesis required after function start ...' but got: " + ex.getMessage());
    }

    /**
     * PWC Test: T3 (Error Path)
     * Purpose: Checks the parser throws an error when a function name token is
     * immediately followed by EOF (missing open parenthesis and arguments).
     * PWC Coverage: C1(Yes/RHS Required) x C2(Missing/Invalid) x C3(Balanced/N/A)
     */
    @Test
    void functionCall_noNextToken_afterFunctionStart_throws() {
        var tokens = dq(function("endsWith")); // endsWith <EOF>
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertTrue(ex.getMessage().startsWith("No expression after function start"),
                "Expected 'No expression after function start ...' but got: " + ex.getMessage());
    }

    /**
     * PWC Test: T4 (Error Path)
     * Purpose: Checks the parser throws an error when the logical AND operator (&&)
     * is at the end of the input stream (missing RHS).
     * PWC Coverage: C1(Yes/RHS Required) x C2(Missing/Invalid) x C3(Balanced/N/A)
     */
    @Test
    void and_missingRightHandSide_throws() {
        var tokens = dq(string("foo"), and()); // foo &&
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertTrue(ex.getMessage().startsWith("No expression after operand &&"));
    }

    /**
     * PWC Test: T5 (Error Path)
     * Purpose: Checks the parser throws an error when the logical OR operator (||)
     * is at the end of the input stream (missing RHS).
     * PWC Coverage: C1(Yes/RHS Required) x C2(Missing/Invalid) x C3(Balanced/N/A)
     */
    @Test
    void or_missingRightHandSide_throws() {
        var tokens = dq(string("foo"), or()); // foo ||
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertTrue(ex.getMessage().startsWith("No expression after operand ||"));
    }

    /**
     * PWC Test: T6 (Error Path)
     * Purpose: Checks the parser throws an error when an unexpected closing parenthesis
     * is encountered (stray close parenthesis).
     * PWC Coverage: C1(No/RHS Not Req) x C2(Missing/Invalid) x C3(Unclosed/Stray)
     */
    @Test
    void strayCloseParen_throwsUnexpectedCloseParenthesis() {
        var tokens = dq(closeParen()); // )
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertEquals("Unexpected close parenthesis.", ex.getMessage());
    }

    /**
     * PWC Test: T7 (Error Path)
     * Purpose: Checks the parser throws an error when two consecutive string literals
     * are encountered without a logical operator between them (bad sequencing).
     * PWC Coverage: C1(No/RHS Not Req) x C2(Missing/Invalid) x C3(Balanced/N/A)
     */
    @Test
    void consecutiveStrings_throwsCantHaveTwoConsecutiveStrings() {
        var tokens = dq(string("foo"), string("bar")); // foo bar
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertEquals("Can't have two consecutive strings or strings without logic", ex.getMessage());
    }

    /**
     * PWC Test: T8 (Error Path)
     * Purpose: Checks the parser throws an error when an open parenthesis is present
     * but the matching close parenthesis is missing at EOF (unclosed parenthesis).
     * PWC Coverage: C1(Yes/RHS Required) x C2(Present/Valid) x C3(Unclosed/Stray)
     */
    @Test
    void unclosedParenthesis_throws() {
        var tokens = dq(openParen(), string("x")); // ( x
        var ex = assertThrows(CompilationException.class, () -> Parser.parse(tokens));
        assertEquals("Unclosed parenthesis", ex.getMessage());
    }

    /**
     * PWC Test: T9 (Edge Case / Contract)
     * Purpose: Verifies the documented contract that parsing an empty token deque
     * correctly returns null and does not throw an exception.
     * PWC Coverage: C1(No) x C2(Missing/EOF) x C3(Balanced/N/A)
     */
    @Test
    void emptyDeque_returnsNull() throws CompilationException {
        var tokens = dq(); // empty
        assertNull(Parser.parse(tokens));
    }

    /**
     * PWC Test: T10 (Positive Control / Flag Propagation)
     * Purpose: Verifies a valid expression containing the function matcher (##)
     * is parsed correctly, and critically, the "method-expression" flag is
     * propagated to the right subtree (the function call).
     * PWC Coverage: C1(Yes/RHS Required) x C2(Present/Valid) x C3(Balanced/N/A)
     */
    @Test
    void functionMatcher_flagsRightSubtreeAsMethodExpression() throws Exception {
        var tokens = dq(string("foo"), functionMatcher(), function("endsWith"), openParen(), string("bar"), closeParen());
        var ast = Parser.parse(tokens); // root is FUNCTION_MATCHER
        assertTrue(ast.children[1].containsMethodExpression(), "Expected method-expression flag on right subtree");
    }
}
