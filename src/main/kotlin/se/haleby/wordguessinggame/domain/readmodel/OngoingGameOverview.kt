package se.haleby.wordguessinggame.domain.readmodel

import java.util.*


data class OngoingGameOverview(val gameId: UUID, val category: String, val startedBy: UUID, val startedAt: Date)