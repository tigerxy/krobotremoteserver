package de.rolandgreim.krobotremoteserver.xmlrpc

import java.util.Base64

sealed interface Value {
    fun flatten(): Any
}

data class IntegerValue(val value: Int) : Value {
    override fun flatten() = value
}

data class BooleanValue(val value: Boolean) : Value {
    override fun flatten() = value
}

data class StringValue(val value: String) : Value {
    override fun flatten() = value
}

data class DoubleValue(val value: Double) : Value {
    override fun flatten() = value
}

data class DateTimeIso8601Value(val value: String) : Value {
    override fun flatten() = value
}

data class Base64Value(val value: String) : Value {
    fun toByteArray(): ByteArray = Base64.getDecoder().decode(value)
    override fun flatten() = value
}

data class StructValue(val members: Map<String, Value>) : Value {
    constructor(vararg members: Pair<String, Value>) : this(members.toMap())

    override fun flatten() = members.mapValues { it.value.flatten() }
}

data class ArrayValue(val data: List<Value>) : Value {
    constructor(vararg data: Value) : this(data.toList())

    override fun flatten() = data.map { it.flatten() }
}

fun Any.toValueType(): Value? =
    when (this) {
        is Int -> IntegerValue(this)
        is Boolean -> BooleanValue(this)
        is String -> StringValue(this)
        is Double -> DoubleValue(this)
        is List<*> -> ArrayValue(this.mapNotNull { it?.toValueType() })
        is Unit -> null
        else -> null
    }
