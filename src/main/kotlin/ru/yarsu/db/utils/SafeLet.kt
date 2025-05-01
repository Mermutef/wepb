@file:Suppress("detekt:all")

package ru.yarsu.db.utils

inline fun <IN1 : Any, IN2 : Any, OUT : Any> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    block: (IN1, IN2) -> OUT?,
): OUT? =
    if (arg1 != null && arg2 != null) {
        block(arg1, arg2)
    } else {
        null
    }

inline fun <IN1 : Any, IN2 : Any, IN3 : Any, OUT : Any> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    block: (IN1, IN2, IN3) -> OUT?,
): OUT? =
    if (arg1 != null && arg2 != null && arg3 != null) {
        block(arg1, arg2, arg3)
    } else {
        null
    }

inline fun <IN1 : Any, IN2 : Any, IN3 : Any, IN4 : Any, OUT : Any> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    arg4: IN4?,
    block: (IN1, IN2, IN3, IN4) -> OUT?,
): OUT? =
    if (arg1 != null && arg2 != null && arg3 != null && arg4 != null) {
        block(arg1, arg2, arg3, arg4)
    } else {
        null
    }

@Suppress("detekt:LongParameterList")
inline fun <IN1 : Any, IN2 : Any, IN3 : Any, IN4 : Any, IN5 : Any, OUT : Any> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    arg4: IN4?,
    arg5: IN5?,
    block: (IN1, IN2, IN3, IN4, IN5) -> OUT?,
): OUT? =
    if (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null) {
        block(arg1, arg2, arg3, arg4, arg5)
    } else {
        null
    }

@Suppress("detekt:LongParameterList")
inline fun <
    IN1 : Any,
    IN2 : Any,
    IN3 : Any,
    IN4 : Any,
    IN5 : Any,
    IN6 : Any,
    OUT : Any,
> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    arg4: IN4?,
    arg5: IN5?,
    arg6: IN6?,
    block: (IN1, IN2, IN3, IN4, IN5, IN6) -> OUT?,
): OUT? =
    if (arg1 != null &&
        arg2 != null &&
        arg3 != null &&
        arg4 != null &&
        arg5 != null &&
        arg6 != null
    ) {
        block(arg1, arg2, arg3, arg4, arg5, arg6)
    } else {
        null
    }

@Suppress("detekt:LongParameterList")
inline fun <
    IN1 : Any,
    IN2 : Any,
    IN3 : Any,
    IN4 : Any,
    IN5 : Any,
    IN6 : Any,
    IN7 : Any,
    OUT : Any,
> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    arg4: IN4?,
    arg5: IN5?,
    arg6: IN6?,
    arg7: IN7?,
    block: (IN1, IN2, IN3, IN4, IN5, IN6, IN7) -> OUT?,
): OUT? =
    if (arg1 != null &&
        arg2 != null &&
        arg3 != null &&
        arg4 != null &&
        arg5 != null &&
        arg6 != null &&
        arg7 != null
    ) {
        block(arg1, arg2, arg3, arg4, arg5, arg6, arg7)
    } else {
        null
    }

@Suppress("detekt:LongParameterList")
inline fun <
    IN1 : Any,
    IN2 : Any,
    IN3 : Any,
    IN4 : Any,
    IN5 : Any,
    IN6 : Any,
    IN7 : Any,
    IN8 : Any,
    OUT : Any,
> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    arg4: IN4?,
    arg5: IN5?,
    arg6: IN6?,
    arg7: IN7?,
    arg8: IN8?,
    block: (IN1, IN2, IN3, IN4, IN5, IN6, IN7, IN8) -> OUT?,
): OUT? =
    if (arg1 != null &&
        arg2 != null &&
        arg3 != null &&
        arg4 != null &&
        arg5 != null &&
        arg6 != null &&
        arg7 != null &&
        arg8 != null
    ) {
        block(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
    } else {
        null
    }

@Suppress("detekt:LongParameterList")
inline fun <
        IN1 : Any,
        IN2 : Any,
        IN3 : Any,
        IN4 : Any,
        IN5 : Any,
        IN6 : Any,
        IN7 : Any,
        IN8 : Any,
        IN9 : Any,
        OUT : Any,
        > safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    arg4: IN4?,
    arg5: IN5?,
    arg6: IN6?,
    arg7: IN7?,
    arg8: IN8?,
    arg9: IN9?,
    block: (IN1, IN2, IN3, IN4, IN5, IN6, IN7, IN8, IN9) -> OUT?,
): OUT? =
    if (arg1 != null &&
        arg2 != null &&
        arg3 != null &&
        arg4 != null &&
        arg5 != null &&
        arg6 != null &&
        arg7 != null &&
        arg8 != null &&
        arg9 != null
    ) {
        block(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    } else {
        null
    }

@Suppress("detekt:LongParameterList")
inline fun <
    IN1 : Any,
    IN2 : Any,
    IN3 : Any,
    IN4 : Any,
    IN5 : Any,
    IN6 : Any,
    IN7 : Any,
    IN8 : Any,
    IN9 : Any,
    IN10 : Any,
    OUT : Any,
> safeLet(
    arg1: IN1?,
    arg2: IN2?,
    arg3: IN3?,
    arg4: IN4?,
    arg5: IN5?,
    arg6: IN6?,
    arg7: IN7?,
    arg8: IN8?,
    arg9: IN9?,
    arg10: IN10?,
    block: (IN1, IN2, IN3, IN4, IN5, IN6, IN7, IN8, IN9, IN10) -> OUT?,
): OUT? =
    if (arg1 != null &&
        arg2 != null &&
        arg3 != null &&
        arg4 != null &&
        arg5 != null &&
        arg6 != null &&
        arg7 != null &&
        arg8 != null &&
        arg9 != null &&
        arg10 != null

    ) {
        block(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
    } else {
        null
    }

fun <K, V> Map<out K?, V>.filterKeysNotNull(): Map<K, V> = mapNotNull { (k, v) -> k?.let { k to v } }.toMap()
