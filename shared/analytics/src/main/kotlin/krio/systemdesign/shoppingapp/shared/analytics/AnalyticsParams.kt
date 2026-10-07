package krio.systemdesign.shoppingapp.shared.analytics

fun analyticsParams(build: AnalyticsParams.() -> Unit): Map<String, AnalyticsValue> =
    AnalyticsParams().apply(build).values

// Only value types every analytics system accepts.
class AnalyticsParams internal constructor() {
    internal val values = mutableMapOf<String, AnalyticsValue>()

    fun param(
        key: String,
        value: String,
    ) {
        values[key] = AnalyticsValue.Text(value)
    }

    fun param(
        key: String,
        value: Long,
    ) {
        values[key] = AnalyticsValue.Number(value)
    }

    fun param(
        key: String,
        value: Int,
    ) = param(key, value.toLong())

    fun param(
        key: String,
        value: Boolean,
    ) {
        values[key] = AnalyticsValue.Flag(value)
    }
}

// Bare values in toString, so params print as {code=SAVE10}, not {code=Text(value=SAVE10)}.
sealed interface AnalyticsValue {
    data class Text(val value: String) : AnalyticsValue {
        override fun toString() = value
    }

    data class Number(val value: Long) : AnalyticsValue {
        override fun toString() = value.toString()
    }

    data class Flag(val value: Boolean) : AnalyticsValue {
        override fun toString() = value.toString()
    }
}
