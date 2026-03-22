package io.chariot.async.api.generator.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static void writeToFile(File dir, String filename, String content) {
        try {
            File file = new File(dir, filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + filename, e);
        }
    }
}
