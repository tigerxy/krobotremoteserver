package de.rolandgreim.krobotremoteserver

import de.rolandgreim.krobotremoteserver.xmlrpc.*
import io.ktor.util.reflect.*
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions

private const val INTRO_METHOD = "__intro__"
private const val INIT_METHOD = "__init__"

internal class RobotLibraryHandler(private val robotInterface: Any) {
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

    fun getLibInfo(): StructValue =
        kFunctions
            .associateWith { kFunction ->
                mapOf(
                    "args" to kFunction.getParameterNames(),
                    "types" to kFunction.getParameterTypes(),
                    "doc" to kFunction.getDocumentation(),
                    "tags" to kFunction.getTags()
                ).toStructValue()
            }
            .mapKeys { it.key.name }
            .plus(INTRO_METHOD to mapOf("doc" to libIntro).toStructValue())
            .plus(INIT_METHOD to mapOf("doc" to libInit).toStructValue())
            .toStructValue()

    fun getKeywords(): ArrayValue {
        val functions = kFunctions
            .map {
                StringValue(it.name)
            }
        return ArrayValue(functions)
    }

    fun getKeywordDocumentation(methodName: String) =
        when (methodName) {
            INTRO_METHOD -> libIntro
            INIT_METHOD -> libInit
            else -> getMethod(methodName).getDocumentation()
        }


    fun getKeywordTags(methodName: String) =
        getMethod(methodName).getTags()

    fun getKeywordArguments(methodName: String) =
        getMethod(methodName).getParameterNames()

    fun getKeywordTypes(methodName: String) =
        getMethod(methodName).getParameterTypes()

    private val libAnnotation =
        robotInterface::class.annotations
            .filterIsInstance<RobotLibrary>()
            .firstOrNull()

    private val libIntro =
        (libAnnotation
            ?.introduction
            ?.trimIndent()
            ?: "").toStringValue()

    private val libInit =
        (libAnnotation
            ?.importing
            ?.trimIndent()
            ?: "").toStringValue()

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
            .toList()
            .toArrayValue()

    private fun KFunction<*>.getDocumentation() =
        annotations
            .filterIsInstance<RobotKeyword>()
            .firstOrNull()
            ?.documentation
            ?.trimIndent()
            ?.toStringValue()
            ?: StringValue("")

    private fun KFunction<*>.getParameterNames() =
        parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .mapNotNull { parameter ->
                parameter.name
            }
            .toArrayValue()

    private fun KFunction<*>.getParameterTypes() =
        parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .mapNotNull { parameter ->
                parameter.type.platformType.typeName
            }
            .toArrayValue()
}

private fun String.toStringValue() = StringValue(this)
private fun Map<String, Value>.toStructValue() = StructValue(this)
private fun List<Value>.toArrayValueType() = ArrayValue(this)
private fun List<String>.toArrayValue() = map { it.toStringValue() }.toArrayValueType()