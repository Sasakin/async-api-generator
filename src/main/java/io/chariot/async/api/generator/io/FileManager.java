package io.chariot.async.api.generator.io;

import java.io.File;
import java.io.IOException;

public interface FileManager {
    File createDirectory(String path);
    void writeToFile(File directory, String fileName, String content) throws IOException;
}
