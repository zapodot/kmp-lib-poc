plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotestMultiplatform)
    id("maven-publish")
}

group = "org.zapodot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
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
        val commonTest by getting {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
    tasks.named<Test>("jvmTest") {
        useJUnitPlatform()
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

    publications {
        val kotlinMultiplatform by getting(MavenPublication::class) {
            version = project.version.toString()
            groupId = project.group.toString()
            artifactId = project.name
            pom {
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

        }
    }

}