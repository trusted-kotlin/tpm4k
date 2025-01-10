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

package de.cacheoverflow.tpm4k

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

/**
 * @author Cedric Hammes
 * @since  10/01/2024
 */
@Suppress("FunctionName")
expect fun TPMDevice(): Result<TPMDevice>

/**
 * @author Cedric Hammes
 * @since  10/01/2024
 */
interface TPMDevice : AutoCloseable {
    /**
     * This function sends the specified message to the TPM device. After that we wait for the response of the TPM and
     * if it comes, it returns the response to the caller.
     *
     * @param message The raw message in bytes to be sent to the TPM device
     * @return        The response of the TPM device or an error
     *
     * @author Cedric Hammes
     * @since  10/01/2024
     */
    fun sendRaw(message: ByteArray): Result<ByteArray>
}

/**
 * @author Cedric Hammes
 * @since  10/01/2024
 */
fun <I, O> TPMDevice.send(
    request: I,
    requestStrategy: SerializationStrategy<I>,
    responseStrategy: DeserializationStrategy<O>
): Result<O> = Buffer().also { TPMMessageEncoder.encode(it, request, requestStrategy) }.use {
    sendRaw(it.readByteArray()).map { responseBytes -> TPMMessageDecoder.decode(responseBytes, responseStrategy) }
}

/**
 * @author Cedric Hmames
 * @since  10/01/2024
 */
inline fun <reified I, reified O> TPMDevice.send(request: I): Result<O> =
    send(request, serializer<I>(), serializer<O>())
