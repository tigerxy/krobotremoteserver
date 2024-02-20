package de.rolandgreim.krobotremoteserver

internal class MethodNotFoundException : Throwable() {
    override val message: String
        get() = "method not found"
}
