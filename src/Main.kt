package com.example

import io.ktor.application.*
import io.ktor.html.respondHtml
import io.ktor.http.*
import io.ktor.http.content.files
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.css.Color
import kotlinx.css.body
import kotlinx.css.em
import kotlinx.css.p
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.ul
import java.io.File

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 80) {
        routing {
            trace { application.log.trace(it.buildText()) }
            static("static"){
                staticRootFolder = File("/resources/startbootstrap-landing-page-gh-pages/")
                files(File("/resources/startbootstrap-landing-page-gh-pages/img/"))
                files("img")
            }
//            get("img") { call.respond("img/") }
            static("img"){
                staticRootFolder = File("/resources/startbootstrap-landing-page-gh-pages/img/")
                files("img")
            }

            static("custom") {
//                staticRootFolder = File("/resources/startbootstrap-landing-page-gh-pages/")
//                files("img")

                static("css") {
//                    files("css")
                }
//                files("img")
                static("vendor") {
//                    files("*")
                    static("jquery") {
                        files("js")
                    }
                }
            }
            get("/") {
                call.respondText("Hello World!-1", ContentType.Text.Plain)
            }
            get("/demo") {
                call.respondText("HELLO WORLD!")
            }
            get("/html-dsl") {
                call.respondHtml {
                    body {
                        h1 { +"HTML" }
                        ul {
                            for (n in 1..12) {
                                li { +"$n" }
                            }
                        }
                    }
                }
            }
            get("/lab-home") {
                call.respondFile(File("resources/index.html"))
            }

            get("/lab-home-good") {
                call.respondFile(File("resources/startbootstrap-landing-page-gh-pages/index.html"))
            }

            get("/vendor/bootstrap/css/bootstrap.min.css") {
                call.respondFile(File("resources/startbootstrap-landing-page-gh-pages/vendor/bootstrap/css/bootstrap.min.css"))
            }
            get("/vendor/fontawesome-free/css/all.min.css") {
                call.respondFile(File("resources/startbootstrap-landing-page-gh-pages/vendor/fontawesome-free/css/all.min.css"))
            }
            get("/css/landing-page.min.css") {
                call.respondFile(File("resources/startbootstrap-landing-page-gh-pages/css/landing-page.min.css"))
            }
            get("/vendor/simple-line-icons/css/simple-line-icons.css") {
                call.respondFile(File("resources/startbootstrap-landing-page-gh-pages/vendor/simple-line-icons/css/simple-line-icons.css"))
            }

            get("/index.css") {
                call.respondFile(File("resources/index.css"))
            }
//            get("img") {
//                call.respondRedirect (  "resources/startbootstrap-landing-page-gh-pages/img/")
//            }

            get("/vendor/jquery/jquery.min.js") {
                call.respondFile(File("resources/startbootstrap-landing-page-gh-pages/vendor/jquery/jquery.min.js"))
            }
            get("/vendor/bootstrap/js/bootstrap.bundle.min.js") {
                call.respondFile(File("resources/startbootstrap-landing-page-gh-pages/vendor/bootstrap/js/bootstrap.bundle.min.js"))
            }
            get("/jquery.min.js") {
                call.respondFile(File("resources/jquery.min.js"))
            }

            get("//socket.io/socket.io.js") {
                call.respondFile(File("resources/socket.io.js"))
            }

            get("/styles.css") {
                call.respondCss {
                    body {
                        backgroundColor = Color.red
                    }
                    p {
                        fontSize = 2.em
                    }
                    rule("p.myclass") {
                        color = Color.blue
                    }
                }
            }

        }
    }
    server.start(wait = true)
}