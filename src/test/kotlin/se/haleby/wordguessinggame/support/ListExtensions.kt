package se.haleby.wordguessinggame.domain.support

import se.haleby.wordguessinggame.domain.event.GameEvent


inline fun <reified T : GameEvent> List<GameEvent>.find(): T = first { it is T } as T
inline fun <reified T : GameEvent> Sequence<GameEvent>.find(): T = first { it is T } as T