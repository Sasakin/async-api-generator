# AsyncAPI Generator

This repository contains an AsyncAPI Generator that generates DTOs and message models from AsyncAPI specifications.

## Features

- Generate Java or Kotlin DTOs from AsyncAPI specifications
- Support for complex message structures and schemas
- Integration with Gradle build system
- Automatic code generation during build process

## Requirements

- Java 8 or higher
- Gradle (for integration)

## Installation and Gradle Integration

### 1. Import the library:
`io.chariot:async-api-generator:version`

### 2. Add to build.gradle in the module containing async-api.yml:

```groovy
dependencies {
    api(libs.async.api.generator)
}

val classPathForAsyncApiGen = configurations.runtimeClasspath.get()
tasks.register<JavaExec>("generateAsyncApi") {
    mainClass.set("io.chariot.async.api.generator.AsyncApiGenerator")
    args = listOf(
            "$projectDir/src/main/resources/asyncapi/async-api.yml",
            "${project.extra["generatedSourcesPath"]}/src/main/java",
            "io.project.api",
            "--language=java"       // supports java and kotlin
    )
    classpath = classPathForAsyncApiGen
}

sourceSets {
    main {
        java {
            srcDirs("${project.extra["generatedSourcesPath"]}/src/main/java")
        }
    }
}
```

### 3. For generating Kotlin DTOs
To generate Kotlin DTOs, specify the `--language=kotlin` parameter in the generateAsyncApi task.

## Generated Code Structure

```
build/generated/src/main/java/
└── io/project/api/
├── dto/ # DTO from components.schemas
├── messagetraits/ # MessageTraits
└── messages/ # Message models
```

## Usage

1. Create your AsyncAPI specification file (e.g., `asyncapi/async-api.yml`)
2. Configure the Gradle task as shown above
3. Run the `generateAsyncApi` task or build your project to automatically generate the code

## AsyncAPI Specification
Uses the standard [AsyncAPI Specification](https://www.asyncapi.com/).

## Contributing

We welcome contributions to this project! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please ensure your code follows the project's coding standards and includes appropriate tests.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

If you have questions or need help, please open an issue in the repository.