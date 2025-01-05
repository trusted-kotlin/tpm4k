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

package de.cacheoverflow.tpm4k.codec

import kotlinx.io.InternalIoApi
import kotlinx.io.Sink
import kotlinx.io.readByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.internal.TaggedEncoder

/**
 * @author Cedric Hammes
 * @since  05/01/2024
 */
@OptIn(InternalSerializationApi::class)
internal class TPMMessageEncoder private constructor(private val sink: Sink) : TaggedEncoder<Unit>() {
    override fun SerialDescriptor.getTag(index: Int): Unit = Unit

    // Write primitive types
    override fun encodeTaggedByte(tag: Unit, value: Byte) = sink.writeByte(value)
    override fun encodeTaggedShort(tag: Unit, value: Short) = sink.writeShort(value)
    override fun encodeTaggedInt(tag: Unit, value: Int) = sink.writeInt(value)
    override fun encodeTaggedLong(tag: Unit, value: Long) = sink.writeLong(value)

    @OptIn(InternalIoApi::class, ExperimentalSerializationApi::class)
    override fun endEncode(descriptor: SerialDescriptor) {
        if (descriptor.annotations.isNotEmpty()) {
            when (val annotation = descriptor.annotations[0]) {
                is TPMCommand -> {
                    val data = sink.buffer.readByteArray()
                    sink.writeShort(0x8001.toShort())
                    sink.writeInt(data.size + 10) // 10 = tag (2) + code (4) + length (4)
                    sink.writeInt(annotation.code)
                    sink.write(data)
                }
            }
        }
    }

    companion object {
        fun <T> encode(sink: Sink, value: T, serializationStrategy: SerializationStrategy<T>) =
            TPMMessageEncoder(sink).encodeSerializableValue(serializationStrategy, value)
    }
}
