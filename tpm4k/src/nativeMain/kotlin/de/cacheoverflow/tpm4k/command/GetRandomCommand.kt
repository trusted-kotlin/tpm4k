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

package de.cacheoverflow.tpm4k.command

import de.cacheoverflow.tpm4k.TPMContext
import de.cacheoverflow.tpm4k.TPMCommand
import de.cacheoverflow.tpm4k.TPMResponse
import kotlinx.serialization.Serializable

/**
 * @author Cedric Hammes
 * @since  05/01/2024
 */
@Serializable
@TPMCommand(code = 0x0000017B)
data class GetBytesRequest(val bytesCount: Short)

/**
 * @author Cedric Hammes
 * @since  05/01/2024
 */
@TPMResponse
@Serializable
data class GetBytesResponse(val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean = other is GetBytesResponse && bytes.contentEquals(other.bytes)
    override fun hashCode(): Int = bytes.contentHashCode()
}

/**
 * This function calls the TPM to as much generate random bytes as specified by the caller of the function. After the
 * TPM sent its response this function extracts the random bytes out of the message.
 *
 * @param count The count of the bytes to get generated
 * @return      A result with the byte array filled with the data
 *
 * @author Cedric Hammes
 * @since  05/01/2024
 */
inline fun TPMContext.randomBytes(count: Short): Result<ByteArray> =
    emitMessage(GetBytesRequest(count), GetBytesRequest.serializer(), GetBytesResponse.serializer()).map { it.bytes }
