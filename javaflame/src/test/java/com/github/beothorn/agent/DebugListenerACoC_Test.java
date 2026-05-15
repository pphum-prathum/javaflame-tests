/* Copyright (C) Saruta Nakro - All Rights Reserved
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package com.github.beothorn.agent.transformer;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DebugListenerACoC_Test {

    private File tempDir;

    @BeforeEach
    void setup() throws IOException {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "debug_test_" + System.nanoTime());
        tempDir.mkdirs();
    }

    @AfterEach
    void cleanup() {
        if (tempDir.exists()) {
            for (File f : Objects.requireNonNull(tempDir.listFiles())) f.delete();
            tempDir.delete();
        }
    }

    private Set<String> makeSet(String... names) {
        return new HashSet<>(Arrays.asList(names));
    }

    // ---- C1=T / C2=any → skip everything ----
    @Test
    public void t1_emptySet_shouldDoNothing() {
        Set<String> emptySet = new HashSet<>();
        assertDoesNotThrow(() -> DebugListener.writeDebugFile(emptySet, tempDir.getAbsolutePath(), "file1.txt"));
        assertFalse(new File(tempDir, "file1.txt").exists());
    }

    // ---- C1=F / C2=F / C3=F ----
    @Test
    public void t2_nonEmptySet_newFile_success() {
        Set<String> set = makeSet("A", "B");
        DebugListener.writeDebugFile(set, tempDir.getAbsolutePath(), "newFile.txt");
        File f = new File(tempDir, "newFile.txt");
        assertTrue(f.exists());
        assertTrue(f.length() > 0);
    }

    // ---- C1=F / C2=T / C3=F ----
    @Test
    public void t3_nonEmptySet_existingFile_success() throws IOException {
        File existing = new File(tempDir, "exist.txt");
        try (FileWriter fw = new FileWriter(existing)) { fw.write("old\n"); }
        Set<String> set = makeSet("C1", "C2");
        DebugListener.writeDebugFile(set, tempDir.getAbsolutePath(), "exist.txt");
        assertTrue(existing.exists());
    }

    // ---- C1=F / C2=T / C3=T ----
    @Test
    public void t4_existingFile_writeError_shouldCatchException() {
        Set<String> set = makeSet("X");
        String invalidPath = "Z:/invalid_path_does_not_exist"; // force IOException
        assertDoesNotThrow(() ->
                DebugListener.writeDebugFile(set, invalidPath, "fail.txt")
        );
    }

    // ---- C1=F / C2=F / C3=T ----
    @Test
    public void t5_newFile_writeError_shouldCatchException() {
        Set<String> set = makeSet("Y");
        String invalidDir = "?:/unreachable";
        assertDoesNotThrow(() ->
                DebugListener.writeDebugFile(set, invalidDir, "fail.txt")
        );
    }

    // ---- C1=F / C2=T / C3=F multiple entries ----
    @Test
    public void t6_existingFile_multipleLines_success() throws IOException {
        File existing = new File(tempDir, "multi.txt");
        try (FileWriter fw = new FileWriter(existing)) { fw.write("line1\n"); }
        Set<String> set = makeSet("L1", "L2", "L3");
        DebugListener.writeDebugFile(set, tempDir.getAbsolutePath(), "multi.txt");
        assertTrue(existing.length() > 0);
    }

    // ---- C1=F / C2=F / C3=F empty dir ----
    @Test
    public void t7_newFile_inEmptyDir_success() {
        Set<String> set = makeSet("Hello");
        DebugListener.writeDebugFile(set, tempDir.getAbsolutePath(), "newDir.txt");
        assertTrue(new File(tempDir, "newDir.txt").exists());
    }

    // ---- C1=F / C2=T / C3=F clearSetAfterWrite ----
    @Test
    public void t8_clearSetAfterWrite_shouldBeEmpty() throws IOException {
        File f = new File(tempDir, "clear.txt");
        f.createNewFile();
        Set<String> set = makeSet("AAA", "BBB");
        DebugListener.writeDebugFile(set, tempDir.getAbsolutePath(), "clear.txt");
        assertTrue(set.isEmpty());
    }
}
