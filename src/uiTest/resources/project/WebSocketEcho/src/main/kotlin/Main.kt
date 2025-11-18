package com.anjo

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofMinutes(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
        get("/healthcheck") {
            call.respondText("I'm alive!")
        }
        post("/create-chat") {
            delay(2000)
            call.respondText(
                text = "{ \"chatId\": \"91718190-04d9-497f-9491-b5387833df70\"}",
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.OK
            )
        }

        webSocket("/echo") {
            send("Please enter your name")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                if (receivedText.equals("bye", ignoreCase = true)) {
                    close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                } else {
                    send(Frame.Text("Hi, $receivedText!"))
                }
            }
        }
        webSocket("/chat/web-socket/{chatId}") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                this@module.log.atInfo().log("Received text: $receivedText")
                val delayValue = (2500..5500).random().toLong()
                delay(delayValue)
                send(Frame.Text("{ \"content\": \"Cool story, bro!\", \"senderId\": \"przemek\"}"))
            }
        }
    }
}