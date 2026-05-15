/* Copyright (C) 2025 Nimmida Maneewan (Earn) - All Rights Reserved
 * You may use, distribute and modify this code under the terms of the MIT license.
 */
package com.github.beothorn.agent.parser;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SUITE 10 — MBCC
 * Target method: ClassAndMethodMatcher.matcher(ElementMatcher<NamedElement>, ElementMatcher<MethodDescription>)
 */
class ClassAndMethodMatcherMBCCTest {

    // Fixtures
    static class Foo { void run(){} void stop(){} }
    static class Bar { void run(){} }

    private static NamedElement typeOf(Class<?> c) {
        return new TypeDescription.ForLoadedType(c);
    }
    private static MethodDescription methodOf(Class<?> c, String name) {
        try {
            Method m = c.getDeclaredMethod(name);
            return new ForLoadedMethod(m);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    // TR1 (Base): cm=named(Foo), mm=named("run")
    @Test
    void T1_base_exactClass_and_exactMethod() {
        ElementMatcher.Junction<NamedElement> cm = named(Foo.class.getName());
        ElementMatcher.Junction<MethodDescription> mm = named("run");

        ClassAndMethodMatcher pair = ClassAndMethodMatcher.matcher(cm, mm);

        assertTrue(pair.classMatcher.matches(typeOf(Foo.class)));
        assertFalse(pair.classMatcher.matches(typeOf(Bar.class)));

        assertTrue(pair.methodMatcher.matches(methodOf(Foo.class, "run")));
        assertFalse(pair.methodMatcher.matches(methodOf(Foo.class, "stop")));
    }

    // TR2 (vary C1): cm=any(), mm=named("run")
    @Test
    void T2_varyC1_classMatcher_any_matches_any_class() {
        ElementMatcher.Junction<NamedElement> cm = any();
        ElementMatcher.Junction<MethodDescription> mm = named("run");

        ClassAndMethodMatcher pair = ClassAndMethodMatcher.matcher(cm, mm);

        assertTrue(pair.classMatcher.matches(typeOf(Foo.class)));
        assertTrue(pair.classMatcher.matches(typeOf(Bar.class)));

        assertTrue(pair.methodMatcher.matches(methodOf(Foo.class, "run")));
        assertFalse(pair.methodMatcher.matches(methodOf(Foo.class, "stop")));
    }

    // TR3 (vary C2): cm=named(Foo), mm=nameStartsWith("ru")
    @Test
    void T3_varyC2_methodMatcher_prefix_nameStartsWith() {
        ElementMatcher.Junction<NamedElement> cm = named(Foo.class.getName());
        ElementMatcher.Junction<MethodDescription> mm = nameStartsWith("ru");

        ClassAndMethodMatcher pair = ClassAndMethodMatcher.matcher(cm, mm);

        assertTrue(pair.classMatcher.matches(typeOf(Foo.class)));
        assertFalse(pair.classMatcher.matches(typeOf(Bar.class)));

        assertTrue(pair.methodMatcher.matches(methodOf(Foo.class, "run")));
        assertFalse(pair.methodMatcher.matches(methodOf(Foo.class, "stop")));
    }

    // TR4 (vary C3): cm=named(Foo), mm=not(named("run"))
    @Test
    void T4_varyC3_methodMatcher_negated_not_named_run() {
        ElementMatcher.Junction<NamedElement> cm = named(Foo.class.getName());
        ElementMatcher.Junction<MethodDescription> mm = not(named("run"));

        ClassAndMethodMatcher pair = ClassAndMethodMatcher.matcher(cm, mm);

        assertTrue(pair.classMatcher.matches(typeOf(Foo.class)));
        assertFalse(pair.classMatcher.matches(typeOf(Bar.class)));

        assertFalse(pair.methodMatcher.matches(methodOf(Foo.class, "run")));
        assertTrue(pair.methodMatcher.matches(methodOf(Foo.class, "stop")));
    }

    // TR5 (vary C4): cm=namedIgnoreCase("FOO..."), mm=namedIgnoreCase("RUN")
    @Test
    void T5_varyC4_caseInsensitive_on_both_matchers() {
        String fooUpper = Foo.class.getName().toUpperCase();
        ElementMatcher.Junction<NamedElement> cm = namedIgnoreCase(fooUpper);
        ElementMatcher.Junction<MethodDescription> mm = namedIgnoreCase("RUN");

        ClassAndMethodMatcher pair = ClassAndMethodMatcher.matcher(cm, mm);

        assertTrue(pair.classMatcher.matches(typeOf(Foo.class)));
        assertFalse(pair.classMatcher.matches(typeOf(Bar.class)));

        assertTrue(pair.methodMatcher.matches(methodOf(Foo.class, "run")));
    }
}
