package de.rolandgreim.krobotremoteserver

internal class MethodNotSupportedException : Throwable() {
    override val message: String
        get() = "method is not supported"
}
