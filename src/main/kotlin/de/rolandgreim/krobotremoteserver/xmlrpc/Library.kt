package de.rolandgreim.krobotremoteserver.xmlrpc

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.myXmlRpc(
    path: String,
    handler: suspend ApplicationCall.(MethodCall) -> MethodResponse
) {
    post(path) {
        if (call.request.contentType() !in listOf(
                ContentType.Application.Xml,
                ContentType.Text.Xml
            )
        ) {
            return@post call.respond(HttpStatusCode.NotAcceptable, "")
        }
        val methodCall = call.receiveStream().use { input ->
            MethodCall.parse { it.parse(input) }
        }
        val methodResponse = call.handler(methodCall)
        val document = methodResponse.toDocument()
        call.respondBytes(ContentType.Application.Xml, HttpStatusCode.OK) {
            document.writeToByteArray()
        }
    }
}
