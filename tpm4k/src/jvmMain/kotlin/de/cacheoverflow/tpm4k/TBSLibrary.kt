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

import com.sun.jna.Native
import com.sun.jna.Structure
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.win32.StdCallLibrary

@Suppress("Unused", "FunctionName")
internal interface TBSLibrary : StdCallLibrary {

    fun Tbsi_Context_Create(params: TBS_CONTEXT_PARAMS2, returnedHandle: IntByReference): Int
    fun Tbsip_Context_Close(handle: Int): Int
    fun Tbsip_Submit_Command(
        handle: Int,
        locality: Int,
        priority: Int,
        inBuf: ByteArray,
        inBufLen: Int,
        outBuf: PointerByReference,
        outBufLen: IntByReference
    ): Int

    @Suppress("ClassName")
    class TBS_CONTEXT_PARAMS2(@JvmField var version: Int, @JvmField var params: Int) : Structure() {
        override fun getFieldOrder(): MutableList<String> = mutableListOf("version", "params")
    }

    companion object {
        internal val INSTANCE: TBSLibrary = Native.load("TBS", TBSLibrary::class.java)
    }
}
