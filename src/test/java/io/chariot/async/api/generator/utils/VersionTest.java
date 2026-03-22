package io.chariot.async.api.generator.utils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Disabled
public class VersionTest {
    Runtime rt = Runtime.getRuntime();

    @Test
    public void checkIfVersionIsUpdated() throws Throwable {
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path filePath = Paths.get(root.toString(), "build.gradle.kts");

        var prefix = "version = \"";
        String currVersion = "";

        var lines = Files.lines(filePath);
        currVersion = lines.filter(l -> Pattern.matches("""
^\\s*version = \\"\\d+\\.\\d+\\.\\d+\\"$""", l)).findFirst().get();
        currVersion = currVersion.substring(currVersion.indexOf(prefix) + prefix.length(), currVersion.length());
        currVersion = currVersion.substring(0, currVersion.length() - 1);

        var r = run("git show HEAD^:build.gradle.kts");
        var oldVersion = new String(r.readAllBytes(), StandardCharsets.UTF_8);
        if (oldVersion.contains(prefix + currVersion + "\"")) {
            throw new IllegalStateException("TODO: you should update publication version in 'build.gradle.kts'\n");
        }
    }

    private InputStream run(String cmd) throws Throwable {
        Process pr = null;
            pr = rt.exec(cmd);
        while(pr.isAlive()) {
                Thread.sleep(500);
        }
        if(pr.exitValue() != 0) {
            throw new IllegalStateException("Command '%s' ended with error:\n%s"
                    .formatted(
                            cmd,
                            String.join("\n", pr.errorReader().lines().toList())));
        }
        return pr.getInputStream();
    }
}
