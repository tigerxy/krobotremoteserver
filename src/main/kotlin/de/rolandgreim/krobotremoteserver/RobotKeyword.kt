package de.rolandgreim.krobotremoteserver

@Target(AnnotationTarget.FUNCTION)
annotation class RobotKeyword(
    val documentation: String = "",
    val tags: Array<String> = []
)