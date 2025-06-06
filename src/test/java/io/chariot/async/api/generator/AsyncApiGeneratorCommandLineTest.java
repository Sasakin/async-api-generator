package io.chariot.async.api.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsyncApiGeneratorCommandLineTest {

    @Test
    void shouldHandleInvalidArguments() {
        // Check insufficient number of arguments
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            String[] args = new String[]{"input.yaml"};
            AsyncApiGenerator.main(args);
        });
        assertTrue(exception.getMessage().contains("Usage:"),
                "Exception message should contain usage information");
    }

    @Test
    void shouldFallbackToDefaultAppVersionWhenNull() {
        // Save current appVersion value
        String originalAppVersion = AsyncApiGenerator.appVersion;
        var originalGenerationTs = AsyncApiGenerator.generationTs;

        try {
            // Set appVersion to null for fallback testing
            AsyncApiGenerator.appVersion = null;
            AsyncApiGenerator.generationTs = null;

            // Run generator with minimal required arguments
            String[] args = new String[] {
                    "specs/basic-dto-generation-spec.yaml",
                    System.getProperty("java.io.tmpdir"),
                    "io.test.package"
            };
            AsyncApiGenerator.main(args);

            // Check that appVersion was set to fallback value
            assertEquals("x.x.x", AsyncApiGenerator.appVersion,
                    "App version should fallback to 'x.x.x' when null");
        } finally {
            // Restore original values
            AsyncApiGenerator.appVersion = originalAppVersion;
            AsyncApiGenerator.generationTs = originalGenerationTs;
        }
    }
}