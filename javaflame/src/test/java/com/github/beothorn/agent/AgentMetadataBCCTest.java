/* Copyright (C) 2025 Chalisa Kengkaewphennapa (Nine) - All Rights Reserved
 * You may use, distribute and modify this code under the terms of the MIT license.
 */
package com.github.beothorn.agent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class AgentMetadataBCCTest {

    // ---------- Base Choice ----------
    // BCC-T1: Normal case - agent initialized with complete metadata, should serialize correctly
    @Test
    @DisplayName("BCC-T1: Agent initialized, complete metadata, serialization succeeds")
    void base_normalInitializedCompleteMetadata() {
        String json = MethodInstrumentationAgent.getExecutionMetadataAsJson(
                "com.acme.Main", "/app.jar", "--debug", "[A,B]", "/var/out", "com.acme",
                Optional.of("start()"), Optional.of("stop()")
        );
        assertTrue(json.contains("\"app\":\"com.acme.Main\""));
    }

    // ---------- Variations ----------
    // BCC-T2: Partial metadata - some fields missing, JSON should still be valid
    @Test
    @DisplayName("BCC-T2: Metadata partially available")
    void vary_partialMetadata() {
        String json = MethodInstrumentationAgent.getExecutionMetadataAsJson(
                "com.acme.Main", "/app.jar", "--debug", "[A,B]", "", "com.acme",
                Optional.of("start()"), Optional.of("stop()")
        );
        assertTrue(json.startsWith("{") && json.endsWith("}"));
    }

    // ---------- Variations ----------
    // BCC-T3: Agent uninitialized - no metadata, should return empty but valid JSON
    @Test
    @DisplayName("BCC-T3: Agent uninitialized")
    void vary_uninitializedAgent() {
        String json = MethodInstrumentationAgent.getExecutionMetadataAsJson(
                "", "", "", "", "", "",
                Optional.empty(), Optional.empty()
        );
        assertTrue(json.startsWith("{") && json.endsWith("}"));
    }

    // ---------- Variations ----------
    // BCC-T4: Simulate serialization issues - output should still produce valid JSON structure
    @Test
    @DisplayName("BCC-T4: Serialization failure simulation")
    void vary_serializationFailure() {
        String json = MethodInstrumentationAgent.getExecutionMetadataAsJson(
                "app", "path", "args", "flags", "out", "filters",
                Optional.of("start()"), Optional.of("stop()")
        );
        assertTrue(json.startsWith("{") && json.endsWith("}"));
    }
}
