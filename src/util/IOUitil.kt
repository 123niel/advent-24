package util

fun <T : Any?> T.println() = this.also { println(it) }
