plugins {
    java
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
}
description = ":async-api-generator"

group = "io.chariot"
version = "1.0.0"

// Установка кодировки UTF-8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}

dependencies {
    implementation("org.freemarker:freemarker:2.3.31")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("org.apache.commons:commons-collections4:4.5.0")

    compileOnly("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Jar> {
    manifest {
        attributes.putAll(mapOf (
            "Main-Class" to "io.chariot.async.api.generator.AsyncApiGenerator",
            "Implementation-Version" to version,
        ))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"
}

repositories {
    maven {
        url = uri("https://nxs.com/repository/maven/")
        isAllowInsecureProtocol = true
    }
    mavenLocal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("asyncApiGenerator") {
            id = "io.chariot.async.api.generator"
            displayName = "Chariot AsyncAPI Generator"
            description = "Генерация dto классов из AsyncAPI спецификации"
            implementationClass = "io.chariot.async.api.generator.AsyncApiPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            groupId = "io.chariot"
            artifactId = "async-api-generator"
            version = rootProject.version.toString()

            from(components["java"])

            pom {
                name.set("Chariot AsyncAPI Gradle Plugin")
                description.set("Gradle plugin for generating Java classes from AsyncAPI")
                
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                
                scm {
                    connection =
                        "scm:git:git://gitlab.io/chariot-networking/commons/backend/async-api-generator"
                    developerConnection =
                        "scm:git:ssh://gitlab.io/chariot-networking/commons/backend/async-api-generator"
                }
                developers {
                    developer {
                        id.set("sasakinme")
                        name.set("")
                        email.set("max_to_day@mail.ru")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri(
                System.getenv("MVN_RELEASES") ?: "https://nxs.com/repository/maven-releases"
            )
            val snapshotsRepoUrl = uri(
                System.getenv("MVN_SNAPSHOTS") ?: "https://nxs.com/repository/maven-snapshots"
            )
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = System.getenv("MVN_USERNAME") ?: ""
                password = System.getenv("MVN_PASSWORD") ?: ""
            }
        }
    }
}
