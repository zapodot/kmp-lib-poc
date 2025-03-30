package org.zapodot.kmp

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class LibraryJvmTest: StringSpec({
    "Platform is JVM" {
        plattform.plattform() shouldBe Plattform.JVM
    }
})