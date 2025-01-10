/*
 * Copyright 2025 Cach30verfl0w
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cacheoverflow.tpm4k.message

import de.cacheoverflow.tpm4k.util.EnumResponseCode
import de.cacheoverflow.tpm4k.TPMCommand
import de.cacheoverflow.tpm4k.TPMResponse
import kotlinx.serialization.Serializable

/**
 * @author Cedric Hammes
 * @since  06/01/2024
 */
@Serializable
@TPMCommand(code = 0x00000143)
data class SelfTestRequest(val fullTest: Boolean = true)

/**
 * @author Cedric Hammes
 * @since  06/01/2024
 */
@TPMResponse
@Serializable
data class SelfTestResponse(val responseCodee: EnumResponseCode)
