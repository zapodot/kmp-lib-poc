package org.zapodot.kmp

object Library : PlatformDescription {
    override fun plattform(): Plattform = Plattform.JVM
}

actual val plattform: PlatformDescription = Library