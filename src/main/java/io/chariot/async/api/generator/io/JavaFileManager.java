package io.chariot.async.api.generator.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaFileManager implements FileManager {

    @Override
    public File createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @Override
    public void writeToFile(File directory, String fileName, String content) throws IOException {
        Path filePath = directory.toPath().resolve(fileName);
        Files.writeString(filePath, content);
    }
}
