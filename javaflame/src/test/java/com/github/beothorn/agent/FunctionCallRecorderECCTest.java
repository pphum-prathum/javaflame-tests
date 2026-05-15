package com.github.beothorn.agent;

import com.github.beothorn.agent.recorder.FunctionCallRecorder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.text.FieldPosition;

public class FunctionCallRecorderECCTest {

    @BeforeEach
    void start(){
        FunctionCallRecorder.shouldPrintQualified = false;
        System.out.println("Starting Test");
    }

    @AfterEach
    void stop(){System.out.println("Stopping Test");}

    /**
     * Test Suite: FunctionCallRecorderECC_Test
     * Technique: Each Choice Coverage
     * Target Method: getClassNameFor(Method method)
     */

    @Test
    void testQualifiedName_UserDefinedClass() throws Exception {
        FunctionCallRecorder.shouldPrintQualified = true;
        Method method = CommandLine.class.getMethod("validateArguments", String.class);

        String className = FunctionCallRecorder.getClassNameFor(method);

        Assertions.assertEquals("com.github.beothorn.agent.CommandLine",className);
    }

    @Test
    void testSimpleName_BuiltInClass() throws Exception {
        Method method = java.text.DecimalFormat.class.getMethod("format", double.class, StringBuffer.class, FieldPosition.class);

        String className = FunctionCallRecorder.getClassNameFor(method);

        Assertions.assertEquals("DecimalFormat",className);
    }

}
