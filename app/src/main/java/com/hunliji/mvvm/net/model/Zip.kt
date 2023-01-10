package com.hunliji.mvvm.net.model

/**
 * Zip
 *
 * @author wm
 * @date 20-2-28
 */
data class Zip2<X, Y>(val one: X? = null, val two: Y? = null)

data class Zip3<X, Y, Z>(val one: X? = null, val two: Y? = null, val three: Z? = null)

data class Zip4<X, Y, Z, W>(
    val one: X? = null,
    val two: Y? = null,
    val three: Z? = null,
    val four: W? = null
)

data class Zip2NotNull<X, Y>(val one: X, val two: Y)

data class Zip3NotNull<X, Y, Z>(val one: X, val two: Y, val three: Z)

data class Zip4NotNull<X, Y, Z, W>(
    val one: X,
    val two: Y,
    val three: Z,
    val four: W
)