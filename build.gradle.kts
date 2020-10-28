plugins {
    `maven-publish`
    `java-library`
    id("com.jfrog.bintray") version "1.8.5"
    kotlin("jvm") version "1.4.10"
    id("jacoco")
}

group = "me.headsvk.defaults"
version = System.getenv("RELEASE_VERSION") ?: "0.0.0"
description = "Simple instantiation of data classes in tests"
val githubUsername = "headsvk"
val repoName = "defaults"

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

java {
    withSourcesJar()
    withJavadocJar()
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

/**
 * Publishing
 */
bintray {
    user = findProperty("bintray_user") as String? ?: System.getenv("BINTRAY_USER")
    key = findProperty("bintray_api_key") as String? ?: System.getenv("BINTRAY_API_KEY")
    override = true

    this.setPublications("release")

    with(pkg) {
        userOrg = "headsvk"
        repo = repoName
        name = project.name
        desc = project.description

        setLicenses("MIT")
        websiteUrl = "https://github.com/$githubUsername/${project.name}"
        issueTrackerUrl = "https://github.com/$githubUsername/${project.name}/issues"
        vcsUrl = "https://github.com/$githubUsername/${project.name}.git"
        setLabels("kotlin")
        with(version) {
            this.name = project.version.toString()
        }
    }
    dryRun = false
    publish = false
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$githubUsername/$repoName")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("release") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }

    publications.withType<MavenPublication>().forEach {
        it.apply {
            pom {
                name.set(repoName)
                description.set(project.description)
                url.set("http://www.github.com/$githubUsername/$repoName")

                scm {
                    connection.set("scm:git:http://www.github.com/$githubUsername/$repoName")
                    developerConnection.set("scm:git:http://github.com/$githubUsername/")
                    url.set("http://www.github.com/$githubUsername/$repoName/")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set(githubUsername)
                        name.set("Marek Hlava")
                        email.set("headsvk@gmail.com")
                    }
                }
            }
        }
    }
}
