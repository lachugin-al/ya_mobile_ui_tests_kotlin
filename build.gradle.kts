plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation("org.jetbrains:annotations:24.0.1")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    // https://mvnrepository.com/artifact/io.appium/java-client
    implementation("io.appium:java-client:7.6.0")
    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation("commons-io:commons-io:2.14.0")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-text
    implementation("org.apache.commons:commons-text:1.10.0")
}

tasks.test {
    val platform = if (project.hasProperty("platform")) project.property("platform") as String else ""
    useJUnitPlatform() {
        if (project.hasProperty("tag") && project.property("tag") != "") {
            val tags = project.property("tag").toString().split(",")
            includeTags(*tags.toTypedArray())
        }
        if (platform == "ios") {
            excludeTags = mutableSetOf("androidOnly")
        } else if (platform == "android") {
            excludeTags = mutableSetOf("iosOnly")
        }
    }
}

kotlin {
    jvmToolchain(8)
}