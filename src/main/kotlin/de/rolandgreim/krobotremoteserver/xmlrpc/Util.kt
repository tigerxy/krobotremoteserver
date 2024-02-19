package de.rolandgreim.krobotremoteserver.xmlrpc

import org.w3c.dom.Document
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

private val declarationBytes = "<?xml version='1.0' encoding='UTF-8'?>".toByteArray()

internal fun createDocumentBuilder(): DocumentBuilder {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder()
}

internal fun createDefaultTransformer(prettyPrint: Boolean): Transformer {
    return TransformerFactory.newInstance().newTransformer().apply {
        if (prettyPrint) {
            setOutputProperty(OutputKeys.INDENT, "yes")
        }
        setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    }
}

fun Document.writeToByteArray(prettyPrint: Boolean = true): ByteArray {
    return ByteArrayOutputStream().use { out ->
        writeTo(out, prettyPrint)
        out.toByteArray()
    }
}

fun Document.writeTo(out: OutputStream, prettyPrint: Boolean = true) {
    out.write(declarationBytes)
    if (prettyPrint) out.write(System.lineSeparator().toByteArray())
    createDefaultTransformer(prettyPrint).transform(DOMSource(this), StreamResult(out))
}

fun Document.writeToString(prettyPrint: Boolean = true): String {
    return writeToByteArray(prettyPrint).toString(Charsets.UTF_8)
}
