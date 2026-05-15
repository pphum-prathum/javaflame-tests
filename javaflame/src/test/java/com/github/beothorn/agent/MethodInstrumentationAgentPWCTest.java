package com.github.beothorn.agent;

import com.github.beothorn.agent.recorder.FunctionCallRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockInputStream extends InputStream {
    private final byte[] data;
    private int position = 0;
    private final IOException readException;
    private final IOException closeException;

    // Flag to track if close was called, useful for verification in successful tests
    public boolean isCloseCalled = false;

    // Constructor for T1, T2, T3 (Successful Read/Write path)
    public MockInputStream(byte[] data, IOException closeException) {
        this.data = data;
        this.readException = null;
        this.closeException = closeException;
    }

    // Constructor for T4, T5 (Read/Write Failure path)
    public MockInputStream(IOException readException, IOException closeException) {
        this.data = new byte[0]; // No data to read
        this.readException = readException;
        this.closeException = closeException;
    }

    /**
     * Overrides the main read method used by the SUT.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (readException != null) {
            // F1: R-FAIL
            throw readException;
        }

        if (position >= data.length) return -1; // End of stream

        int bytesToRead = Math.min(len, data.length - position);
        System.arraycopy(data, position, b, off, bytesToRead);
        position += bytesToRead;
        return bytesToRead;
    }

    @Override
    public int read() throws IOException {
        return -1; // Not used by the SUT, but required to implement abstract class
    }

    /**
     * Overrides the close method to inject F3 behavior.
     */
    @Override
    public void close() throws IOException {
        isCloseCalled = true;
        // F3: C-FAIL
        if (closeException != null) throw closeException;
    }
}

public class MethodInstrumentationAgentPWCTest {

    @BeforeEach
    void setup() {
        FunctionCallRecorder.shouldPrintQualified = false;
    }

    @Test
    void testT1_ReadSuccess_SmallData_CloseSuccess() throws IOException {
        // F2: S-SMALL (Data size < 4096)
        byte[] expectedData = new byte[]{0x01, 0x02, 0x03, 0x04};
        // F3: C-OK (null close exception)
        MockInputStream mockStream = new MockInputStream(expectedData, null);

        byte[] result = MethodInstrumentationAgent.readAllBytes(mockStream);

        // F1: R-OK
        assertArrayEquals(expectedData, result, "T1: Should return the correct small data.");
        assertTrue(mockStream.isCloseCalled, "T1: Close() should have been called.");
    }

    /**
     * T2: R-OK, S-SMALL, C-FAIL (Successful read, but close fails)
     * Expected: Throws the IOException from close() because the main read was successful (exception == null).
     */
    @Test
    void testT2_ReadSuccess_SmallData_CloseFails() {
        // F2: S-SMALL (Data size < 4096)
        byte[] data = new byte[]{0x05, 0x06};
        IOException closeEx = new IOException("T2: Close failed after successful read.");
        // F3: C-FAIL
        MockInputStream mockStream = new MockInputStream(data, closeEx);

        // F1: R-OK
        IOException thrown = assertThrows(IOException.class, () -> MethodInstrumentationAgent.readAllBytes(mockStream),
                "T2: Must throw the IOException from close() on success path."
        );

        // Verify the exception thrown is the close exception
        assertEquals(closeEx.getMessage(), thrown.getMessage(),
                "T2: The thrown exception must be the close exception."
        );
        assertTrue(mockStream.isCloseCalled, "T2: Close() should have been called.");
        // The result data cannot be verified, as the exception precedes the successful return.
    }

    /**
     * T3: R-OK, S-LARGE, C-OK (Multiple reads, all success)
     * Expected: Returns data after multiple loop iterations.
     */
    @Test
    void testT3_ReadSuccess_LargeData_CloseSuccess() throws IOException {
        // F2: S-LARGE (Data size > 4096, e.g., 5000 bytes)
        int largeSize = 5000;
        byte[] expectedData = new byte[largeSize];
        Arrays.fill(expectedData, (byte) 0xFF); // Fill data for easy verification

        // F3: C-OK
        MockInputStream mockStream = new MockInputStream(expectedData, null);

        // F1: R-OK
        byte[] result = MethodInstrumentationAgent.readAllBytes(mockStream);

        assertArrayEquals(expectedData, result, "T3: Should return the correct large data.");
        assertTrue(mockStream.isCloseCalled, "T3: Close() should have been called.");
    }

    /**
     * T4: R-FAIL, C-OK (Read fails, Close succeeds)
     * Expected: Throws the original read exception. No suppressed exceptions.
     */
    @Test
    void testT4_ReadFails_CloseSuccess() {
        IOException readEx = new IOException("T4: Read failed.");
        // F1: R-FAIL
        // F3: C-OK
        MockInputStream mockStream = new MockInputStream(readEx, null);

        // Assert the original exception is thrown
        IOException thrown = assertThrows(IOException.class, () -> MethodInstrumentationAgent.readAllBytes(mockStream),
                "T4: Must throw the original read exception."
        );

        assertEquals(readEx.getMessage(), thrown.getMessage(), "T4: Thrown exception message should match the read failure message.");
        assertEquals(0, thrown.getSuppressed().length, "T4: No suppressed exceptions expected.");
        assertTrue(mockStream.isCloseCalled, "T4: Close() should have been called.");
    }

    /**
     * T5: R-FAIL, C-FAIL (Read fails, Close also fails)
     * Expected: Throws the original read exception, with the close exception suppressed.
     */
    @Test
    void testT5_ReadFails_CloseFails() {
        IOException readEx = new IOException("T5: Read failed.");
        IOException closeEx = new IOException("T5: Close failed.");

        // F1: R-FAIL
        // F3: C-FAIL
        MockInputStream mockStream = new MockInputStream(readEx, closeEx);

        // Assert the original exception is thrown
        IOException thrown = assertThrows(IOException.class, () -> MethodInstrumentationAgent.readAllBytes(mockStream),
                "T5: Must throw the original read exception."
        );

        // Verify read exception is the main exception
        assertEquals(readEx.getMessage(), thrown.getMessage(), "T5: Thrown exception message should match the read failure message.");

        // Verify close exception is suppressed
        List<Throwable> suppressed = Arrays.asList(thrown.getSuppressed());
        assertEquals(1, suppressed.size(), "T5: Exactly one suppressed exception expected.");
        assertEquals(closeEx.getMessage(), suppressed.get(0).getMessage(), "T5: The suppressed exception must be the close failure.");
        assertTrue(mockStream.isCloseCalled, "T5: Close() should have been called.");
    }

}
