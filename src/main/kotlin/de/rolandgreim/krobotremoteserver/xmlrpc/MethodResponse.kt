package de.rolandgreim.krobotremoteserver.xmlrpc

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilder

sealed interface MethodResponse {

    fun toDocument(): Document {
        val document = createDocumentBuilder().newDocument()
        with(Serializer(document)) {
            appendToElement(document)
        }
        return document
    }

    companion object {
        fun parse(block: (DocumentBuilder) -> Document): MethodResponse {
            return Deserializer().parseMethodResponse(block(createDocumentBuilder()).documentElement)
        }
    }
}

data class MethodResponseSuccess(
    val params: List<Value>
) : MethodResponse {

    constructor(vararg params: Value) : this(params.toList())
}

data class MethodResponseFault(
    val faultCode: Int,
    val faultString: String
) : MethodResponse
