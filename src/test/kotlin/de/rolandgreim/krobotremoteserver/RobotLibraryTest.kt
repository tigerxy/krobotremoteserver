package de.rolandgreim.krobotremoteserver

import de.rolandgreim.krobotremoteserver.xmlrpc.IntegerValue
import de.rolandgreim.krobotremoteserver.xmlrpc.StringValue
import de.rolandgreim.krobotremoteserver.xmlrpc.StructValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RobotLibraryTest {
    @Test
    fun `run method`() {
        val robotLibrary = RobotLibrary(object {
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

        val robotLibrary = RobotLibrary(object {
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