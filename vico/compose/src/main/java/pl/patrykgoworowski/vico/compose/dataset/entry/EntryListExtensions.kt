/*
 * Copyright (c) 2021. Patryk Goworowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.patrykgoworowski.vico.compose.dataset.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import pl.patrykgoworowski.vico.core.dataset.entry.collection.EntryCollection
import pl.patrykgoworowski.vico.core.dataset.entry.collection.EntryModel

@Composable
public fun <Model : EntryModel> EntryCollection<Model>.collectAsState(): State<Model> =
    produceState(initialValue = model) {
        val listener: (Model) -> Unit = { entriesModel ->
            value = entriesModel
        }

        addOnEntriesChangedListener(listener)

        awaitDispose {
            removeOnEntriesChangedListener(listener)
        }
    }