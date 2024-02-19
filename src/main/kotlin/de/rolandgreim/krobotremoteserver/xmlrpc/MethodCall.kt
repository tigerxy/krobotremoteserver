package de.rolandgreim.krobotremoteserver.xmlrpc

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilder

data class MethodCall(
    val methodName: String,
    val params: List<Value>
) {
    constructor(methodName: String, vararg params: Value) : this(methodName, params.toList())

    fun toDocument(): Document {
        val document = createDocumentBuilder().newDocument()
        with(Serializer(document)) {
            appendToElement(document)
        }
        return document
    }

    companion object {
        fun parse(block: (DocumentBuilder) -> Document): MethodCall {
            return Deserializer().parseMethodCall(block(createDocumentBuilder()).documentElement)
        }
    }
}
