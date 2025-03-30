plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotestMultiplatform)

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
