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
import kotlinx.io.Sink
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class TPMMessageEncoder(private val sink: Sink) : AbstractEncoder(), AutoCloseable {
    override val serializersModule: SerializersModule = EmptySerializersModule()
    private val buffer = Buffer()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = TPMMessageEncoder(buffer)
    override fun endStructure(descriptor: SerialDescriptor) {
        when (descriptor.kind) {
            StructureKind.CLASS -> {
                if (descriptor.annotations.isNotEmpty()) {
                    when (val annotation = descriptor.annotations[0]) {
                        is TPMCommand -> {
                            sink.writeShort(0x8001.toShort())
                            sink.writeInt(buffer.size.toInt() + 10)
                            sink.writeInt(annotation.code)
                            sink.write(buffer, buffer.size)
                        }
                    }
                }
            }
            StructureKind.LIST -> {
                sink.writeShort(buffer.size.toShort())
                sink.write(buffer, buffer.size)
            }
            else -> {}
        }
        buffer.close()
    }

    // Encode primitive types and null
    override fun encodeNull() = Unit
    override fun encodeBoolean(value: Boolean) = buffer.writeByte(if(value) 1 else 0)
    override fun encodeByte(value: Byte) = buffer.writeByte(value)
    override fun encodeShort(value: Short) = buffer.writeShort(value)
    override fun encodeInt(value: Int) = buffer.writeInt(value)
    override fun encodeLong(value: Long) = buffer.writeLong(value)

    // Close
    override fun close() = sink.write(buffer, buffer.size)

    companion object {
        fun <T> encode(sink: Sink, value: T, serializationStrategy: SerializationStrategy<T>) =
            TPMMessageEncoder(sink).use { it.encodeSerializableValue(serializationStrategy, value) }
    }
}
