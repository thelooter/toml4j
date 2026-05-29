plugins{
    id("java-library")
    id("maven-publish")
    id("jacoco")
    id("signing")
    alias(libs.plugins.vanniktech.maven.publish)
}
group = "de.thelooter"
description = "Java Library for parsing TOML"
version = createVersion()

repositories{
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
}

dependencies{
    implementation(libs.gson)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.hamcrest)
}

java{
    // Build/test run on a fixed LTS toolchain (JUnit 6, JaCoCo and Gradle 9 all need 17+),
    // independent of whichever JDK happens to be on the machine.
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

jacoco{
    toolVersion = libs.versions.jacoco.get()
}

tasks{
    // Keep the published library compatible with Java 11 while building on a newer JDK.
    // Only the main source set is constrained; tests build against the running JDK so they
    // can use modern test frameworks (JUnit 6 requires Java 17+).
    compileJava{
        options.release.set(11)
    }

    test{
        useJUnitPlatform()
    }

    jacocoTestReport{
        reports{
            xml.required.set(true)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

    javadoc {
        options {
            (options as? StandardJavadocDocletOptions)?.apply {
                encoding = "UTF-8"

                addBooleanOption("html5", true)

                links("https://docs.oracle.com/en/java/javase/11/docs/api/")
            }
        }
    }

    check{
        finalizedBy("jacocoTestReport")
    }


}

mavenPublishing {
    coordinates(project.group.toString(), rootProject.name, project.version.toString())

    pom {
        name = rootProject.name
        description = project.description
        url = "https://github.com/thelooter/toml4j"

        inceptionYear.set("2024")

        licenses{
            license{
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("moandji.ezana")
                name.set("Moandji Ezana")
                email.set("mwanji@gmail.com")
            }
            developer {
                id.set("thelooter")
                name.set("Eve Kolb")
                email.set("evekolb2204@gmail.com")
            }
        }

        issueManagement{
            system.set("GitHub")
            url.set("https://github.com/thelooter/toml4j/issues")
        }

        scm {
            connection.set("scm:git:git://github.com/thelooter/toml4j.git")
            developerConnection.set("scm:git:git@github.com:thelooter/toml4j.git")
            url.set("https://github.com/thelooter/toml4j")
            tag.set("HEAD")
        }

        ciManagement{
            system.set("Github Actions")
            url.set("https://github.com/thelooter/toml4j/actions")
        }
    }

    signAllPublications()
    publishToMavenCentral()
}

val backupVersion = "1.0.0"

fun createVersion(): String = System.getenv("TOML4J_VERSION")
    ?.let { it + (System.getenv("CI")?.let { "" } ?: "-dev") }
    ?: (backupVersion + (System.getenv("CI")?.let { "" } ?: "-dev"))