package se.haleby.wordguessinggame.domain.support

internal fun <T> List<T>.add(value: T): List<T> = run {
    val arrayList = ArrayList(this)
    arrayList.add(value)
    return arrayList
}