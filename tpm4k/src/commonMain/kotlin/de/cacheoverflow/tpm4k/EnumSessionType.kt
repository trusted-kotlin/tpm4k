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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class SessionTypeSerializer : KSerializer<EnumSessionType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(requireNotNull(EnumSessionType::class.qualifiedName), PrimitiveKind.BYTE)

    override fun serialize(encoder: Encoder, value: EnumSessionType) = encoder.encodeByte(value.value)
    override fun deserialize(decoder: Decoder): EnumSessionType =
        requireNotNull(EnumSessionType.fromByte(decoder.decodeByte()))
}

/**
 * @author Cedric Hammes
 * @since  05/01/2024
 *
 * @see 7.11 "TPM_SE (Session Type)", ISO/IEC 11889-2:2015 "TPM Library: Structures"
 */
@Serializable(with = SessionTypeSerializer::class)
enum class EnumSessionType(private val literal: String, val value: Byte) {
    HMAC("HMAC", 0x00),
    POLICY("Policy", 0x01),
    TRIAL("Trial", 0x02),
    INVALID("Invalid", 0x1F);

    override fun toString(): String = literal

    companion object {
        fun fromByte(value: Byte): EnumSessionType = entries.firstOrNull { it.value == value } ?: INVALID
    }
}
