package utils;

import lombok.SneakyThrows;
import org.apache.tools.ant.util.ResourceUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.UUID;

public class TestUtils {

    @SneakyThrows
    public static String resourceFileToString(String path) {
        try (var is = ResourceUtils.class.getClassLoader().getResourceAsStream(path)) {
            return new String(is.readAllBytes());
        }
    }

    @SneakyThrows
    public static Path writeStringToTempFile(StringBuffer sb) {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = File.createTempFile("asyncapigen-temp-", ".yaml", tempDir);
        ;
        try(FileWriter fileWriter = new FileWriter(tempFile, true);
            BufferedWriter bw = new BufferedWriter(fileWriter)
        ) {
            bw.write(sb.toString());
        }

        return tempFile.toPath();
    }

    public static String randomPackageName() {
        return "a" + UUID.randomUUID().toString() // просто начинаем с буквы
                .replace("{", "")
                .replace("-", "");
    }
}
