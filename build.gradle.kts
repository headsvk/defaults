plugins {
    kotlin("jvm") version "1.4.10"
    id("jacoco")
}

group = "me.headsvk.defaults"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test-junit"))
}

kotlin {
    explicitApi()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}
