/*
 * Copyright 2020 Johan Haleby
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.haleby.wordguessinggame.domain.writemodel

import se.haleby.wordguessinggame.domain.event.*
import se.haleby.wordguessinggame.domain.support.add
import java.util.*

/**
 * Start game
 */
fun startGame(previousEvents: Sequence<GameEvent>, gameId: GameId, timestamp: Timestamp, playerId: PlayerId, wordList: WordList,
              maxNumberOfGuessesPerPlayer: MaxNumberOfGuessesPerPlayer, maxNumberOfGuessesTotal: MaxNumberOfGuessesTotal): Sequence<GameEvent> {
    val state = previousEvents.rehydrate()

    if (state !is NotStarted) {
        throw IllegalStateException("Cannot start game $gameId since it has already been started")
    }

    val wordToGuess = wordList.words.random()

    val gameStarted = GameWasStarted(eventId = UUID.randomUUID(), timestamp = timestamp, gameId = gameId, startedBy = playerId, category = wordList.category.value,
            wordToGuess = wordToGuess.value, maxNumberOfGuessesPerPlayer = maxNumberOfGuessesPerPlayer.value, maxNumberOfGuessesTotal = maxNumberOfGuessesTotal.value)

    return sequenceOf(gameStarted)
}

fun guessWord(previousEvents: Sequence<GameEvent>, timestamp: Timestamp, playerId: PlayerId, word: Word): Sequence<GameEvent> = when (val game = previousEvents.rehydrate()) {
    NotStarted -> throw IllegalStateException("Cannot guess word for a game that is not started")
    is Ended -> throw IllegalStateException("Cannot guess word for a game that is already ended")
    is Ongoing -> {
        if (game.isMaxNumberOfGuessesExceededForPlayer(playerId)) {
            throw IllegalArgumentException("Number of guessing attempts exhausted for player $playerId.")
        }

        val events = mutableListOf<GameEvent>()

        if (game.isRightGuess(word)) {
            events.add(PlayerGuessedTheRightWord(UUID.randomUUID(), timestamp, game.gameId, playerId, word.value))
            events.add(GameWasWon(UUID.randomUUID(), timestamp, game.gameId, playerId))
        } else {
            events.add(PlayerGuessedTheWrongWord(UUID.randomUUID(), timestamp, game.gameId, playerId, word.value))

            if (game.isLastGuessForPlayer(playerId)) {
                events.add(NumberOfGuessesWasExhaustedForPlayer(UUID.randomUUID(), timestamp, game.gameId, playerId))
            }

            if (game.isLastGuessForGame()) {
                events.add(GameWasLost(UUID.randomUUID(), timestamp, game.gameId))
            }
        }

        events.asSequence()
    }
}

private data class Guess(val playerId: PlayerId, val timestamp: Timestamp, val word: String)

// States
private sealed class GameState
private object NotStarted : GameState()
private data class Ongoing(val gameId: GameId, val wordToGuess: String, val maxNumberOfGuessesPerPlayer: Int, val maxNumberOfGuessesTotal: Int, val startedBy: PlayerId, val guesses: List<Guess> = emptyList()) : GameState() {
    fun numberOfGuessesForPlayer(playerId: PlayerId) = guesses.count { it.playerId == playerId }
    fun isMaxNumberOfGuessesExceededForPlayer(playerId: PlayerId): Boolean = numberOfGuessesForPlayer(playerId) == maxNumberOfGuessesPerPlayer
    fun isLastGuessForPlayer(playerId: PlayerId): Boolean = numberOfGuessesForPlayer(playerId) + 1 == maxNumberOfGuessesPerPlayer
    fun isLastGuessForGame(): Boolean = guesses.size == maxNumberOfGuessesTotal - 1
    fun isRightGuess(guessedWord: Word) = guessedWord.value.equals(wordToGuess, ignoreCase = true)
}

private object Ended : GameState()

private fun Sequence<GameEvent>.rehydrate(): GameState = fold<GameEvent, GameState>(NotStarted) { state, event ->
    when {
        state is NotStarted && event is GameWasStarted -> Ongoing(event.gameId, event.wordToGuess, event.maxNumberOfGuessesPerPlayer, event.maxNumberOfGuessesTotal, event.startedBy)
        state is Ongoing && event is PlayerGuessedTheWrongWord -> state.copy(guesses = state.guesses.add(Guess(event.playerId, event.timestamp, event.guessedWord)))
        state is Ongoing && event is PlayerGuessedTheRightWord -> state.copy(guesses = state.guesses.add(Guess(event.playerId, event.timestamp, event.guessedWord)))
        state is Ongoing && event is NumberOfGuessesWasExhaustedForPlayer -> state
        state is Ongoing && event is GameWasWon -> Ended
        state is Ongoing && event is GameWasLost -> Ended
        else -> throw IllegalStateException("Event ${event.type} is not applicable in state ${state::class.simpleName!!}")
    }
}