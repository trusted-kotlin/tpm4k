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

import com.sun.jna.Memory
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import de.cacheoverflow.tpm4k.util.EnumResponseCode
import kotlinx.io.IOException
import java.io.RandomAccessFile

/**
 * @author Cedric Hammes
 * @since  10/01/2024
 */
internal class LinuxTPMDevice internal constructor(private val deviceFile: RandomAccessFile) : TPMDevice {
    override fun sendRaw(message: ByteArray): Result<ByteArray> {
        try {
            deviceFile.write(message)

            val header = ByteArray(6)
            if (deviceFile.read(header, 0, 6) != 6) {
                return Result.failure(IOException("Unable to read header of response"))
            }

            val responseSize = header.sliceArray(2..5).fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }
            val response = header.copyOf(responseSize)
            if (deviceFile.read(response, 6, response.size - 6) != responseSize - 6) {
                return Result.failure(IOException("Unable to read remaining response"))
            }

            return Result.success(response)
        } catch (ex: IOException) {
            return Result.failure(ex)
        }
    }

    override fun close() = deviceFile.close()
}

internal class WindowsTPMDevice internal constructor(private val handle: Int) : TPMDevice {
    override fun sendRaw(message: ByteArray): Result<ByteArray> {
        val buffer = Memory(4096)
        val response = PointerByReference().also { it.pointer = buffer }
        val responseLen = IntByReference().also { it.value = 4096 }
        if (TBSLibrary.INSTANCE.Tbsip_Submit_Command(handle, 0, 0, message, message.size, response, responseLen) != 0) {
            return Result.failure(IOException("Unable to send command to TPM device"))
        }

        val responseBytes = ByteArray(responseLen.value)
        buffer.read(0, responseBytes, 0, responseBytes.size)
        return Result.success(responseBytes)
    }

    override fun close() {
        TBSLibrary.INSTANCE.Tbsip_Context_Close(handle)
    }
}

/**
 * @author Cedric Hammes
 * @since  10/01/2024
 */
@OptIn(ExperimentalStdlibApi::class)
@Suppress("FunctionName")
actual fun TPMDevice(): Result<TPMDevice> {
    val os = System.getProperty("os.name")
    return when {
        os.contains("nix", true) || os.contains("nux", true) || os.contains("aix", true) -> Result.runCatching {
            LinuxTPMDevice(RandomAccessFile("/dev/tpmrm0", "rwd"))
        }
        os.contains("win", true) -> {
            val contextParams = TBSLibrary.TBS_CONTEXT_PARAMS2(2, 1 shl 2)
            val handleRef = IntByReference()
            val result = TBSLibrary.INSTANCE.Tbsi_Context_Create(contextParams, handleRef)
            if (result != 0) {
                println("Error (0x${result.toHexString()})")
                return Result.failure(IOException("Unable to open TPM device: ${EnumResponseCode.fromInt(result)}"))
            }

            return Result.success(WindowsTPMDevice(handleRef.value))
        }
        else -> throw IllegalStateException("Unsupported operating system '$os'")
    }
}
