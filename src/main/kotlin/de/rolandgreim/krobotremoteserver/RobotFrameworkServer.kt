package de.rolandgreim.krobotremoteserver

import de.rolandgreim.krobotremoteserver.xmlrpc.MethodResponseFault
import de.rolandgreim.krobotremoteserver.xmlrpc.MethodResponseSuccess
import de.rolandgreim.krobotremoteserver.xmlrpc.myXmlRpc
import io.ktor.server.application.*
import io.ktor.server.routing.*

private const val ROBOT_LIBRARY_INFORMATION = "get_library_information"
private const val ROBOT_KEYWORD_NAMES = "get_keyword_names"
private const val ROBOT_KEYWORD_DOCUMENTATION = "get_keyword_documentation"
private const val ROBOT_RUN_KEYWORD = "run_keyword"

fun Application.robotFrameworkServer(robotInterface: Any, path: String = "/RPC2") {
    val lib = RobotLibrary(robotInterface)

    routing {
        myXmlRpc(path) { request ->
            when (request.methodName) {
                ROBOT_LIBRARY_INFORMATION -> MethodResponseSuccess(lib.getLibInfo())
                ROBOT_KEYWORD_NAMES -> MethodResponseSuccess(lib.getKeywords())
                ROBOT_KEYWORD_DOCUMENTATION -> MethodResponseSuccess()
                ROBOT_RUN_KEYWORD -> MethodResponseSuccess(
                    lib.runMethod(
                        request.params[0].flatten() as String,
                        request.params[1].flatten() as List<Any>,
                    )
                )

                else -> MethodResponseFault(42, "unknown method ${request.methodName}")
            }
        }
    }
}
