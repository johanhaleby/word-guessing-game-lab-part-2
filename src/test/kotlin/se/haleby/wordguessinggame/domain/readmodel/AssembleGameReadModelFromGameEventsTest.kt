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

package se.haleby.wordguessinggame.domain.readmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import se.haleby.wordguessinggame.domain.event.GameWasStarted
import se.haleby.wordguessinggame.domain.writemodel.GameId
import se.haleby.wordguessinggame.domain.writemodel.PlayerId
import se.haleby.wordguessinggame.domain.writemodel.Timestamp
import java.util.*

class AssembleGameReadModelFromGameEventsTest {

    @Test
    fun `doesn't obfuscate dashes when game is started`() {
        // Given
        val gameWasStarted = GameWasStarted(UUID.randomUUID(), Timestamp(), GameId.randomUUID(), PlayerId.randomUUID(), "Category", "One-Two", 2, 4)

        // When
        val readModel = AssembleGameReadModelFromDomainEvents().applyEvent(gameWasStarted).gameReadModel as OngoingGameReadModel

        // Then
        assertThat(readModel.hint).isEqualTo("___-___")
    }
}