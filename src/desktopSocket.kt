package com.example

import javax.swing.JFrame
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.websocket.*
import io.ktor.client.features.websocket.ws
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.http.cio.websocket.readText

suspend fun main(){
    var myForm = JFrame("111")
    myForm.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    myForm.setLocationRelativeTo(null)
    myForm.isVisible = true
    val client = HttpClient(Apache){
        install(WebSockets)
//        install(io.ktor.client.features.websocket.WebSockets)
    }

    client.ws(
        method = HttpMethod.Get,
        host = "localhost",
        port = 8080, path = "/"
    ) { // this: DefaultClientWebSocketSession

        // Send text frame.
        send(Frame.Text("Hello World"))

        // Send binary frame.
//        send(Frame.Binary(...))

        // Receive frame.
        val frame = incoming.receive()
        when (frame) {
            is Frame.Text -> println(frame.readText())
            is Frame.Binary -> println(frame.readBytes())
        }
    }
}