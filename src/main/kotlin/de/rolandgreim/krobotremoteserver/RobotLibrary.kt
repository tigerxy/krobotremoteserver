package de.rolandgreim.krobotremoteserver

import de.rolandgreim.krobotremoteserver.xmlrpc.*
import io.ktor.util.reflect.*
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions

internal class RobotLibrary(private val robotInterface: Any) {
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

    fun getLibInfo(): StructValue {
        val functions = kFunctions
            .associate { function ->
                function.name to StructValue(
                    mapOf(
                        "args" to ArrayValue(function.getParameterNames()),
                        "types" to ArrayValue(function.getParameterTypes()),
                        "tags" to ArrayValue(function.getTags())
                    ).filter { it.value.data.isNotEmpty() }
                )
            }
        return StructValue(functions)
    }

    fun getKeywords(): ArrayValue {
        val functions = kFunctions
            .map {
                StringValue(it.name)
            }
        return ArrayValue(functions)
    }

    fun getKeywordDocumentation(methodName: String) =
        getMethod(methodName)
            .getDocumentation()
            ?: throw MethodNotSupportedException()

    fun getKeywordTags(methodName: String) =
        getMethod(methodName)
            .getTags()

    fun getKeywordArguments(methodName: String) =
        getMethod(methodName)
            .getParameterNames()
            .let { ArrayValue(it) }

    fun getKeywordTypes(methodName: String) =
        getMethod(methodName)
            .getParameterTypes()
            .let { ArrayValue(it) }


    private val kFunctions = robotInterface::class.memberFunctions
        .filter { function ->
            function.annotations.any { it.instanceOf(RobotKeyword::class) }
        }

    private fun getMethod(methodName: String) =
        kFunctions
            .firstOrNull { it.name == methodName }
            ?: throw MethodNotFoundException()

    private fun KFunction<*>.getTags() =
        annotations
            .filterIsInstance<RobotKeyword>()
            .firstOrNull()
            ?.tags
            .orEmpty()
            .map { StringValue(it) }

    private fun KFunction<*>.getDocumentation() =
        annotations
            .filterIsInstance<RobotKeyword>()
            .firstOrNull()
            ?.documentation
            ?.let { StringValue(it) }

    private fun KFunction<*>.getParameterNames() =
        parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .mapNotNull { parameter ->
                parameter.name
            }
            .map { StringValue(it) }

    private fun KFunction<*>.getParameterTypes() =
        parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .mapNotNull { parameter ->
                parameter.type.platformType.typeName
            }
            .map { StringValue(it) }
}