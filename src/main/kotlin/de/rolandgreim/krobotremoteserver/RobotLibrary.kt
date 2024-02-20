package de.rolandgreim.krobotremoteserver

import de.rolandgreim.krobotremoteserver.xmlrpc.*
import io.ktor.util.reflect.*
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions

class RobotLibrary(private val robotInterface: Any) {
    fun runMethod(methodName: String, params: List<Any>): Value {
        val method = robotInterface::class.memberFunctions.firstOrNull { it.name == methodName }
        return try {
            val result = method?.call(robotInterface, *params.toTypedArray())?.toValueType()

            val returnValue = result?.let {
                mapOf("return" to result)
            } ?: emptyMap()

            StructValue(
                mapOf(
                    "status" to StringValue("PASS")
                ) + returnValue
            )
        } catch (e: InvocationTargetException) {
            val errorValue = e.targetException.message?.let {
                mapOf("error" to StringValue(it))
            } ?: emptyMap()

            StructValue(
                mapOf(
                    "status" to StringValue("FAIL"),
                    "traceback" to StringValue(e.targetException.stackTraceToString())
                ) + errorValue
            )
        }
    }

    fun getKeywords(): ArrayValue {
        val functions = robotInterface::class.memberFunctions
            .filter { function ->
                function.annotations.any { it.instanceOf(Keyword::class) }
            }
            .map {
                StringValue(it.name)
            }
        return ArrayValue(functions)
    }

    fun getLibInfo(): StructValue {
        val functions = robotInterface::class.memberFunctions
            .filter { function ->
                function.annotations.any { it.instanceOf(Keyword::class) }
            }
            .associate { function ->
                val parameters = function.parameters
                    .filter { it.kind == KParameter.Kind.VALUE }
                    .mapNotNull { parameter ->
                        parameter.name
                    }
                    .map { StringValue(it) }

                val types = function.parameters
                    .filter { it.kind == KParameter.Kind.VALUE }
                    .mapNotNull { parameter ->
                        parameter.type.platformType.typeName
                    }
                    .map { StringValue(it) }

                val tags = function.annotations
                    .filterIsInstance<Keyword>()
                    .firstOrNull()
                    ?.tags
                    .orEmpty()
                    .map { StringValue(it) }

                function.name to StructValue(
                    mapOf(
                        "args" to ArrayValue(parameters),
                        "types" to ArrayValue(types),
                        "tags" to ArrayValue(tags)
                    ).filter { it.value.data.isNotEmpty() }
                )
            }
        return StructValue(functions)
    }
}