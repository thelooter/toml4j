import com.vanniktech.maven.publish.SonatypeHost

plugins{
    id("java-library")
    id("maven-publish")
    id("jacoco")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.29.0"
}
group = "de.thelooter"
description = "Java Library for parsing TOML"
version = createVersion()

repositories{
    mavenCentral()
}

dependencies{
    implementation("com.google.code.gson:gson:2.8.9")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}

tasks{
    java{
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        withSourcesJar()
    }

    test{
        useJUnitPlatform()
    }

    jacoco{
        toolVersion = "0.8.10"
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

                links("https://docs.oracle.com/javase/8/docs/api/")
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

    configure<JavaPluginExtension> {
        withSourcesJar()
    }

    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}

val backupVersion = "1.0.0"

fun createVersion(): String = System.getenv("TOML4J_VERSION")
    ?.let { it + (System.getenv("CI")?.let { "" } ?: "-dev") }
    ?: (backupVersion + (System.getenv("CI")?.let { "" } ?: "-dev"))