package de.rolandgreim.krobotremoteserver

@Target(AnnotationTarget.CLASS)
annotation class RobotLibrary(
    val introduction: String = "",
    val importing: String = "",
)