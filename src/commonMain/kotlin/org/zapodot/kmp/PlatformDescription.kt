package org.zapodot.kmp

interface PlatformDescription {
    fun plattform(): Plattform
    fun description(): String = "Kotlin Multiplatform: ${plattform()}"
}

expect val plattform: PlatformDescription

enum class Plattform {
    JVM,
    JS
}