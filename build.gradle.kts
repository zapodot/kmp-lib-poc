import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotestMultiplatform)
    alias(libs.plugins.npmPublish)
    alias(libs.plugins.dokka)
    id("maven-publish")
    signing
}

group = "org.zapodot"

repositories {
    mavenCentral()
}
tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}
kotlin {
    jvmToolchain(21)
    targets {
        jvm {
            compilerOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
        js(IR) {
            moduleName = "kmplib"
            binaries.library()
            compilations["main"].packageJson {
                description = "Kotlin Multiplatform Library POC"
                customField("repository", "github:zapodot/kmp-lib-poc")
                customField("license", "MIT")
                customField("homepage", "https://github.com/zapodot/kmp-lib-poc")
                customField("author", mapOf("name" to "Sondre Eikanger Kvalø", "email" to "zapodot at gmail.com", "url" to "https://zapodot.org"))
            }
            browser {
                testTask {
                    useKarma {
                        useChromeHeadless()
                        useFirefoxHeadless()
                    }
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {}
        val commonTest by getting {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions)
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
    tasks.named<Jar>("jvmJar") {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
        }.with(copySpec { from("${project.rootDir}/LICENSE") })
    }
}
// Tests
tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            showExceptions = true
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

// Docs
tasks {
    register<Jar>("dokkaJar") {
        from(dokkaHtml)
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
    }
    withType<Jar> {
        metaInf.with(copySpec {
            from("${project.rootDir}/LICENSE")
        })
    }
    val jvmJar by getting(Jar::class) {
        manifest {
            attributes("Automatic-Module-Name" to "org.zapodot.kmplibpoc")
        }
    }
}

npmPublish {
    registries {
        github {
            authToken = System.getenv("TOKEN")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/zapodot/kotlin-multiplatform-library")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }

    publications.withType<MavenPublication> {
            pom {
                version = project.version.toString()
                groupId = project.group.toString()
                artifactId = project.name

                name = "Kotlin Multiplatform Library POC"
                description = "Proof of concept for Kotlin Multiplatform Library"
                inceptionYear = "2025"
                url = "https://github.com/zapodot/kmp-lib-poc"
                scm {
                    connection = "scm:git@github.com:zapodot/kmp-lib-poc.git"
                    developerConnection = "scm:git@github.com:zapodot/kmp-lib-poc.git"
                    url = "https://github.com/zapodot/kmp-lib-poc"
                }
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "zapodot"
                        name = "Sondre Eikanger Kvalø"
                        email = "zapodot at gmail.com"
                    }
                }
            }
        artifact(tasks["dokkaJar"])

    }

}

// Signing
signing {
    useInMemoryPgpKeys(
        System.getenv("GPG_PRIVATE_KEY"),
        System.getenv("GPG_PRIVATE_KEY_PASSPHRASE")
    )
    sign(publishing.publications)
}

// Gradle wrapper
tasks {
    // see https://docs.gradle.org/current/userguide/gradle_wrapper.html#customizing_wrapper
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}