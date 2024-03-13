package de.rolandgreim.krobotremoteserver

import de.rolandgreim.krobotremoteserver.xmlrpc.ArrayValue
import de.rolandgreim.krobotremoteserver.xmlrpc.IntegerValue
import de.rolandgreim.krobotremoteserver.xmlrpc.StringValue
import de.rolandgreim.krobotremoteserver.xmlrpc.StructValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RobotLibraryHandlerTest {
    @Test
    fun `lib info`() {
        val robotLibrary = RobotLibraryHandler(object {
            @RobotKeyword(
                tags = ["x", "y"],
                documentation = "Keyword documentation.",
            )
            fun example(a: Int, b: Boolean) = if (b) a else a * -1
        })

        val lib = robotLibrary.getLibInfo()
        assertIs<StructValue>(lib)

        assertEquals(setOf("__intro__", "example"), lib.members.keys)

        val value = lib.members["example"]
        assertIs<StructValue>(value)

        val argsValue = value.members["args"]
        assertNotNull(argsValue)
        assertIs<ArrayValue>(argsValue)
        assertEquals(listOf(StringValue("a"), StringValue("b")), argsValue.data)

        val typesValue = value.members["types"]
        assertNotNull(typesValue)
        assertIs<ArrayValue>(typesValue)
        assertEquals(listOf(StringValue("int"), StringValue("boolean")), typesValue.data)

        val docValue = value.members["doc"]
        assertNotNull(docValue)
        assertIs<StringValue>(docValue)
        assertEquals(StringValue("Keyword documentation."), docValue)

        val tagsValue = value.members["tags"]
        assertNotNull(tagsValue)
        assertIs<ArrayValue>(tagsValue)
        assertEquals(listOf(StringValue("x"), StringValue("y")), tagsValue.data)
    }

    @Test
    fun `get keyword documentation`() {
        val robotLibrary = RobotLibraryHandler(
            @RobotLibrary(
                introduction = "library introduction",
                importing = "library importing",
            )
            object {
                @RobotKeyword(
                    documentation = "keyword documentation",
                )
                fun example(a: Int, b: Boolean) = if (b) a else a * -1
            }
        )

        val doc = robotLibrary.getKeywordDocumentation("example")
        assertIs<StringValue>(doc)
        assertEquals("keyword documentation", doc.value)

        val intro = robotLibrary.getKeywordDocumentation("__intro__")
        assertIs<StringValue>(intro)
        assertEquals("library introduction", intro.value)

        val init = robotLibrary.getKeywordDocumentation("__init__")
        assertIs<StringValue>(init)
        assertEquals("library importing", init.value)
    }

    @Test
    fun `run method`() {
        val robotLibrary = RobotLibraryHandler(object {
            fun add(a: Int, b: Int) = a + b
        })

        val value = robotLibrary.runMethod("add", listOf(1, 2))
        assertIs<StructValue>(value)

        val statusValue = value.members["status"]
        assertNotNull(statusValue)
        assertIs<StringValue>(statusValue)
        assertEquals("PASS", statusValue.value)

        val returnValue = value.members["return"]
        assertNotNull(returnValue)
        assertIs<IntegerValue>(returnValue)
        assertEquals(3, returnValue.value)
    }

    @Test
    fun `run failing method`() {
        val errorMessage = "This is an Error"

        val robotLibrary = RobotLibraryHandler(object {
            fun failingMethod() {
                throw Exception(errorMessage)
            }
        })

        val value = robotLibrary.runMethod("failingMethod", emptyList())
        assertIs<StructValue>(value)

        val statusValue = value.members["status"]
        assertNotNull(statusValue)
        assertIs<StringValue>(statusValue)
        assertEquals("FAIL", statusValue.value)

        val errorValue = value.members["error"]
        assertNotNull(errorValue)
        assertIs<StringValue>(errorValue)
        assertEquals(errorMessage, errorValue.value)

        val tracebackValue = value.members["traceback"]
        assertNotNull(tracebackValue)
        assertIs<StringValue>(tracebackValue)
        assertTrue { tracebackValue.value.isNotEmpty() }
    }
}