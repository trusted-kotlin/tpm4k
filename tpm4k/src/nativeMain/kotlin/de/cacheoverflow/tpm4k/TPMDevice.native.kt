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
import kotlinx.io.IOException
import tpm.*

internal class NativeTPMDevice internal constructor(private val contextHandle: tpm_object_t) : TPMDevice {
    override fun sendRaw(message: ByteArray): Result<ByteArray> = memScoped {
        val response = alloc<tpm_object_t>()
        response.type = TPM_OBJECT_MESSAGE
        message.usePinned { pinned ->
            if (tpm_context_message(
                    context = contextHandle.ptr,
                    msg = pinned.addressOf(0).reinterpret(),
                    msg_length = message.size.convert(),
                    response = response.ptr
                ) != TPM_ERROR_SUCCESS
            ) {
                tpm_context_close(response.ptr)
                return@usePinned Result.failure(IOException("Unable to send TPM message"))
            }

            val responseBytes = requireNotNull(tpm_message_get_data(response.ptr))
                .readBytes(response.message.length.convert())
            tpm_context_close(response.ptr)
            Result.success(responseBytes)
        }
    }

    override fun close() {
        tpm_context_close(contextHandle.ptr)
        nativeHeap.free(contextHandle.rawPtr)
    }
}

/**
 * @author Cedric Hammes
 * @since  10/01/2024
 */
@Suppress("FunctionName")
actual fun TPMDevice(): Result<TPMDevice> {
    val contextHandle = nativeHeap.alloc<tpm_object_t>()
    contextHandle.type = TPM_OBJECT_CONTEXT
    if (tpm_context_init(contextHandle.ptr) != TPM_ERROR_SUCCESS) {
        return Result.failure(RuntimeException("Unable to initialize TPM device"))
    }
    return Result.success(NativeTPMDevice(contextHandle))
}
