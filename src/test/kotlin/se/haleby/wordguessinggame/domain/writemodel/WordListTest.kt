package se.haleby.wordguessinggame.domain.writemodel

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import se.haleby.wordguessinggame.domain.wordsOf

class WordListTest {

    @Test
    fun `throws iae when list contains duplicate words`() {
        // Given
        val words = wordsOf("Hello", "shark", "HELLO", "hellO", "apple")

        // When
        val throwable = catchThrowable { WordList(WordCategory("category"), words) }

        // Then
        assertThat(throwable).isExactlyInstanceOf(IllegalArgumentException::class.java).hasMessage("Duplicate words in the same category is not allowed: Hello, HELLO, hellO")
    }
}