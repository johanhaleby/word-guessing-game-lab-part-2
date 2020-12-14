package se.haleby.wordguessinggame.domain

import se.haleby.wordguessinggame.domain.writemodel.Word

fun wordsOf(vararg words: String) = listOf(*words).map(::Word)