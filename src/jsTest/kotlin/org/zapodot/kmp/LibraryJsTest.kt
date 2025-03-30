package org.zapodot.kmp

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class LibraryJsTest: StringSpec ({
    "Platform is JVM" {
        plattform.plattform() shouldBe Plattform.JS
    }

    "Description is Kotlin Multiplatform: JS" {
        plattform.description() shouldBe "Kotlin Multiplatform: JS"
    }
})