import io.gitlab.arturbosch.detekt.detekt
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("io.gitlab.arturbosch.detekt") version "1.11.1"

    id("org.jetbrains.dokka") version "0.9.18"
    `maven-publish`
    signing
}

group = "com.github.krupt"
version = "1.1.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-parent:2.3.3.RELEASE") {
            bomProperty("kotlin.version", "1.3.72")
        }
    }
}

repositories {
    mavenCentral()
    jcenter {
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    }
    maven(url = "https://dl.bintray.com/kotlin/dokka")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.springframework.data:spring-data-commons")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    api("io.springfox:springfox-swagger2:2.9.2")
    api("io.springfox:springfox-swagger-ui:2.9.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("com.ninja-squad:springmockk:1.1.2")
    testImplementation("org.springframework.data:spring-data-commons")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.11.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
        javaParameters = true
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.findByName("build")?.dependsOn("detekt")

detekt {
    toolVersion = "1.11.1"
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("detekt.yml")
    buildUponDefaultConfig = true
}

tasks.withType<DokkaTask> {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
    linkMapping {
        dir = "src/main/kotlin"
        url = "https://github.com/krupt/spring-boot-starter-jsonrpc/blob/master/src/main/kotlin"
        // Suffix which is used to append the line number to the URL. Use #L for GitHub
        suffix = "#L"
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val kotlinDocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}

val sonatypeRepoUsername: String? by project
val sonatypeRepoPassword: String? by project

publishing {
    repositories {
        maven {
            url = uri(
                    if ((project.version as String).endsWith("-SNAPSHOT"))
                        "https://oss.sonatype.org/content/repositories/snapshots"
                    else
                        "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            )
            credentials {
                username = sonatypeRepoUsername
                password = sonatypeRepoPassword
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(kotlinDocJar)

            pom {
                url.set("https://github.com/krupt/spring-boot-starter-jsonrpc")

                name.set("Spring Boot Starter JSON-RPC")
                description.set("Spring module for JSON-RPC")

                developers {
                    developer {
                        id.set("krupt")
                        name.set("Andrey Kovalev")
                        email.set("krupt25@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/krupt/spring-boot-starter-jsonrpc.git")
                    developerConnection.set("scm:git:git@github.com:krupt/spring-boot-starter-jsonrpc.git")
                    url.set("https://github.com/krupt/spring-boot-starter-jsonrpc/tree/${project.version}")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
            }
        }
    }
}

signing {
    setRequired({
        !(project.version as String).endsWith("-SNAPSHOT")
                && gradle.taskGraph.hasTask(":publishMavenJavaPublicationToMavenRepository")
    })

    sign(publishing.publications["mavenJava"])
}
