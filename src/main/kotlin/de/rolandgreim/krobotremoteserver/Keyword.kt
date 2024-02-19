package de.rolandgreim.krobotremoteserver

@Target(AnnotationTarget.FUNCTION)
annotation class Keyword(val tags: Array<String> = [])