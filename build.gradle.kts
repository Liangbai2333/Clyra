plugins {
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "site.liangbai"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-starter")
    api("dev.langchain4j:langchain4j-spring-boot-starter:0.36.2")
    api("dev.langchain4j:langchain4j-open-ai-spring-boot-starter:0.36.2")
    api("dev.langchain4j:langchain4j-dashscope-spring-boot-starter:0.36.2")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = project.group
            artifactId = project.name.lowercase()
            version = project.version.toString()
            from(components["java"])
            pom {
                name.set("Clyra")
                url.set("https://github.com/Liangbai2333/Clyra")
                description.set("Clyra 是一个轻量级的基于大模型的简单指令框架  ")
                developers {
                    developer {
                        id.set("Liangbai2333")
                        name.set("Liangbai2333")
                        email.set("liangbai2333@outlook.com")
                    }
                }
                licenses {
                    license {
                        name.set("GNU Affero General Public License v3.0")
                        url.set("https://github.com/Liangbai2333/Clyra/blob/main/LICENSE")
                    }
                }
                scm {
                    url.set("https://github.com/Liangbai2333/Clyra")
                    connection.set("scm:git:git://github.com/Liangbai2333/Clyra.git")
                    developerConnection.set("scm:git:ssh://github.com/Liangbai2333/Clyra.git")
                }
            }
        }
    }
    repositories {
        mavenLocal()

        maven {
            isAllowInsecureProtocol = true

            val snapshotsUrl = uri("http://47.96.226.41:8081/repository/maven-snapshots/")
            val releasesUrl = uri("http://47.96.226.41:8081/repository/maven-releases/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}
