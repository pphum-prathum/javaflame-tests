/* Copyright (C) Pran Tantipiwatanaskul - All Rights Reserved
* You may use, distribute and modify this code under the terms of the MIT
license.
*/

package com.github.beothorn.agent.parser;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElementMatcherFromExpressionMBCC_Test {

    // Mock classes used as sample targets for matching
    static class OrderService {}
    static class PaymentClient {}
    static class FooBar {}

    // Helper method to convert class into a ByteBuddy TypeDescription.
    // Allows the test to simulate how the agent matches class names dynamically.
    private static TypeDescription typeOf(Class<?> c) {
        return new TypeDescription.ForLoadedType(c);
    }

    // TR1: Single valid identifier
    // Expected: Matches only the class containing the identifier “Order”
    @Test
    void tr1_baseCase() throws CompilationException {
        ElementMatcherFromExpression m = ElementMatcherFromExpression.forExpression("Order");
        ElementMatcher.Junction<NamedElement> mm = m.getClassMatcher();
        assertTrue(mm.matches(typeOf(OrderService.class)));
        assertFalse(mm.matches(typeOf(PaymentClient.class)));
        assertFalse(mm.matches(typeOf(FooBar.class)));
    }

    // TR2: Empty input (zero identifiers)
    // Expected: NullPointerException due to invalid input
    @Test
    void tr2_zeroIdentifier() throws CompilationException {
        assertThrows(NullPointerException.class,
                () -> ElementMatcherFromExpression.forExpression(""));
    }

    // TR3: Two identifiers joined with logical OR operator 
    // Expected: Matches if class name contains “Order” or “Payment”
    @Test
    void tr3_twoIdentifier() throws CompilationException {
        ElementMatcherFromExpression m = ElementMatcherFromExpression.forExpression("Order||Payment");
        ElementMatcher.Junction<NamedElement> mm = m.getClassMatcher();
        assertTrue(mm.matches(typeOf(OrderService.class)));
        assertTrue(mm.matches(typeOf(PaymentClient.class)));
        assertFalse(mm.matches(typeOf(FooBar.class)));
    }

    // TR4: Case mismatch
    // Expected: Not match due to case sensitivity
    @Test
    void tr4_caseMismatch() throws CompilationException {
        ElementMatcherFromExpression m = ElementMatcherFromExpression.forExpression("orderservice");
        ElementMatcher.Junction<NamedElement> mm = m.getClassMatcher();
        assertFalse(mm.matches(typeOf(OrderService.class)));
        assertFalse(mm.matches(typeOf(PaymentClient.class)));
        assertFalse(mm.matches(typeOf(FooBar.class)));
    }

    // TR5: Invalid expression with space
    // Expected: CompilationException due to whitespace not allowed
    @Test
    void tr5_invalidSpace() {
        assertThrows(CompilationException.class,
                () -> ElementMatcherFromExpression.forExpression("Order || Payment"));
    }

    // TR6: Invalid operator structure (“Order||” has trailing operator)
    // Expected: CompilationException due to malformed expression
    @Test
    void tr6_invalidOperatorStructure() {
        assertThrows(CompilationException.class,
                () -> ElementMatcherFromExpression.forExpression("Order||"));
    }
}
