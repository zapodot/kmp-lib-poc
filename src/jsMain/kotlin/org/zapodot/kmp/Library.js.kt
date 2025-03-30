package org.zapodot.kmp

object Library : PlatformDescription {
    override fun plattform(): Plattform = Plattform.JS
}

actual val plattform: PlatformDescription = Library