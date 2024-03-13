package de.rolandgreim.krobotremoteserver

import de.rolandgreim.krobotremoteserver.xmlrpc.MethodResponseFault
import de.rolandgreim.krobotremoteserver.xmlrpc.MethodResponseSuccess
import de.rolandgreim.krobotremoteserver.xmlrpc.myXmlRpc
import io.ktor.server.application.*
import io.ktor.server.routing.*

private const val ROBOT_LIBRARY_INFORMATION = "get_library_information"
private const val ROBOT_KEYWORD_NAMES = "get_keyword_names"
private const val ROBOT_KEYWORD_TAGS = "get_keyword_tags"
private const val ROBOT_KEYWORD_ARGUMENTS = "get_keyword_arguments"
private const val ROBOT_KEYWORD_TYPES = "get_keyword_types"
private const val ROBOT_KEYWORD_DOCUMENTATION = "get_keyword_documentation"
private const val ROBOT_RUN_KEYWORD = "run_keyword"

fun Application.robotFrameworkServer(robotInterface: Any, path: String = "/RPC2") {
    val lib = RobotLibraryHandler(robotInterface)

    routing {
        myXmlRpc(path) { request ->
            try {
                when (request.methodName) {
                    // ROBOT_LIBRARY_INFORMATION -> MethodResponseSuccess(lib.getLibInfo())
                    ROBOT_KEYWORD_NAMES -> MethodResponseSuccess(lib.getKeywords())
                    ROBOT_KEYWORD_TAGS -> MethodResponseSuccess(
                        lib.getKeywordTags(
                            request.params.first().flatten() as String
                        )
                    )

                    ROBOT_KEYWORD_DOCUMENTATION -> MethodResponseSuccess(
                        lib.getKeywordDocumentation(
                            request.params.first().flatten() as String
                        )
                    )

                    ROBOT_KEYWORD_ARGUMENTS -> MethodResponseSuccess(
                        lib.getKeywordArguments(
                            request.params.first().flatten() as String
                        )
                    )

                    ROBOT_KEYWORD_TYPES -> MethodResponseSuccess(
                        lib.getKeywordTypes(
                            request.params.first().flatten() as String
                        )
                    )

                    ROBOT_RUN_KEYWORD -> MethodResponseSuccess(
                        lib.runMethod(
                            request.params[0].flatten() as String,
                            request.params[1].flatten() as List<Any>,
                        )
                    )

                    else -> MethodResponseFault(1, "method ${request.methodName} not supported")
                }
            } catch (e: Exception) {
                MethodResponseFault(1, e.message ?: "internal error")
            }
        }
    }
}
