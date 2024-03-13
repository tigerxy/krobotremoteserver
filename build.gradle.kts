plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    `maven-publish`
    signing
}

group = "de.rolandgreim.krobotremoteserver"
version = "0.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.netty)
}

tasks.test {
    useJUnitPlatform()
}
java {
    withJavadocJar()
    withSourcesJar()
}
kotlin {
    jvmToolchain(11)
}
publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("release"))
        }
    }
    publications {
        create<MavenPublication>("Maven") {
            from(components["java"])
            pom {
                name = "Krobotremoteserver"
                description = "Robot Framework remote server implemented with Kotlin"
                url = "https://github.com/tigerxy/krobotremoteserver"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        name = "Roland Greim"
                        email = "github@rolandgreim.de"
                        organization = "rolandgreim"
                        organizationUrl = "https://www.rolandgreim.de"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/tigerxy/krobotremoteserver.git"
                    developerConnection = "scm:git:ssh://git@github.com:tigerxy/krobotremoteserver.git"
                    url = "https://github.com/tigerxy/krobotremoteserver.git"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["Maven"])
}
