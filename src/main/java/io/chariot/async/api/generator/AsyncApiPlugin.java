package io.chariot.async.api.generator;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class AsyncApiPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Добавляем расширение для конфигурации
        AsyncApiExtension extension = project.getExtensions().create("asyncApi", AsyncApiExtension.class);

        // Регистрируем задачу
        project.getTasks().register("generateAsyncApi", JavaExec.class, task -> {
            task.getMainClass().set("io.chariot.async.api.generator.AsyncApiGenerator");
            task.setClasspath(project.getConfigurations().getByName("runtimeClasspath"));

            // Аргументы из расширения
            task.args(
                    extension.getInputYaml(),
                    extension.getOutputDir(),
                    extension.getBasePackage()
            );
        });

        // Автоматически добавляем сгенерированные исходники в sourceSet
        project.afterEvaluate(p -> {
            SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
            sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava().srcDir(extension.getOutputDir());
        });
    }
}
