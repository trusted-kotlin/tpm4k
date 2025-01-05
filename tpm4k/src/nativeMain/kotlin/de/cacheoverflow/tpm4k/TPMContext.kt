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

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import tpm.*

/**
 * This class provides a platform-independent API for interacting with the TPM and send commands to the TPM and receive
 * responses from it.
 *
 * @author Cedric Hammes
 * @since  04/01/2024
 */
@OptIn(ExperimentalForeignApi::class)
value class TPMContext private constructor(private val contextHandle: tpm_object_t) : AutoCloseable {

    private fun emitMessage(message: ByteArray): Result<ByteArray> = memScoped {
        val response = nativeHeap.alloc<tpm_object_t>()
        response.type = TPM_OBJECT_MESSAGE
        message.usePinned { pinned ->
            if (tpm_context_message(
                    context = contextHandle.ptr,
                    msg = pinned.addressOf(0).reinterpret(),
                    msg_length = message.size.convert(),
                    response = response.ptr
            ) != TPM_ERROR_SUCCESS) {
                tpm_context_close(response.ptr)
                return@usePinned Result.failure(RuntimeException("Unable to send TPM message"))
            }

            val responseBytes = requireNotNull(tpm_message_get_data(response.ptr))
                .readBytes(response.message.length.convert())
            tpm_context_close(response.ptr)
            Result.success(responseBytes)
        }
    }

    private fun TPMContext.emitMessage(message: Source): Result<ByteArray> = emitMessage(message.readByteArray())

    fun <T, R> TPMContext.emitMessage(
        command: T,
        serializationStrategy: SerializationStrategy<T>,
        deserializationStrategy: DeserializationStrategy<R>
    ): Result<R> {
        val buffer = Buffer().also { TPMMessageEncoder.encode(it, command, serializationStrategy) }
        val response = buffer.use { emitMessage(it) }
        return Result.success(
            TPMMessageDecoder.decode(
                response.getOrNull() ?: return Result.failure(requireNotNull(response.exceptionOrNull())),
                deserializationStrategy
            )
        )
    }

    override fun close() {
        tpm_context_close(contextHandle.ptr)
        nativeHeap.free(contextHandle.rawPtr)
    }

    companion object {
        fun new(): Result<TPMContext> {
            val contextHandle = nativeHeap.alloc<tpm_object_t>()
            contextHandle.type = TPM_OBJECT_CONTEXT
            if (tpm_context_init(contextHandle.ptr) != TPM_ERROR_SUCCESS) {
                return Result.failure(RuntimeException("Unable to initialize TPM context"))
            }
            return Result.success(TPMContext(contextHandle))
        }
    }
}
