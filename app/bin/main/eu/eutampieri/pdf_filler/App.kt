/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package eu.eutampieri.pdf_filler

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import eu.eutampieri.pdf_filler.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}