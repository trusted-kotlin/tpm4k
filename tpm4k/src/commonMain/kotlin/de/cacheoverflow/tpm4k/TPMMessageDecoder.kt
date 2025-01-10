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
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * @author Cedric Hammes
 * @since  05/01/2024
 */
@OptIn(ExperimentalSerializationApi::class)
internal class TPMMessageDecoder private constructor(private val source: Source) : AbstractDecoder() {
    override val serializersModule: SerializersModule = EmptySerializersModule()
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = CompositeDecoder.DECODE_DONE
    override fun decodeSequentially(): Boolean = true

    @OptIn(ExperimentalSerializationApi::class)
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (descriptor.kind) {
            StructureKind.CLASS -> {
                val annotation = descriptor.annotations.getOrNull(0)
                when (annotation) {
                    is TPMResponse -> {
                        source.readShort()
                        source.readInt() // TODO: Handle response code
                        return TPMMessageDecoder(source)
                    }

                    else -> {}
                }

                return TPMMessageDecoder(source)
            }

            StructureKind.LIST -> TPMMessageDecoder(source)

            else -> this
        }
    }

    // Read primitive types
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = source.readShort().toInt()
    override fun decodeBoolean(): Boolean = source.readByte() == 1.toByte()
    override fun decodeByte(): Byte = source.readByte()
    override fun decodeShort(): Short = source.readShort()
    override fun decodeInt(): Int = source.readInt()
    override fun decodeLong(): Long = source.readLong()

    companion object {
        private fun <T> decode(source: Source, deserializationStrategy: DeserializationStrategy<T>): T =
            TPMMessageDecoder(source).decodeSerializableValue(deserializationStrategy)

        fun <T> decode(bytes: ByteArray, deserializationStrategy: DeserializationStrategy<T>): T =
            Buffer().also { it.write(bytes) }.use { decode(it, deserializationStrategy) }
    }
}
