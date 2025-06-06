package io.chariot.async.api.generator.languages;

import lombok.Getter;

@Getter
public enum TargetLanguage {
    JAVA("java", ".java", "java", "src/main/java"),
    KOTLIN("kotlin", ".kt", "kotlin", "src/main/kotlin"),
    PYTHON("python", ".py", "python", "src/main/python");

    private final String templateDir;
    private final String fileExtension;
    private final String languageName;
    private final String sourcePath;

    TargetLanguage(String templateDir, String fileExtension, String languageName, String sourcePath) {
        this.templateDir = templateDir;
        this.fileExtension = fileExtension;
        this.languageName = languageName;
        this.sourcePath = sourcePath;
    }

    public String getTemplatePath(String templateName) {
        return templateDir + "/" + templateName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getSourcePath() {
        return sourcePath;
    }
}
