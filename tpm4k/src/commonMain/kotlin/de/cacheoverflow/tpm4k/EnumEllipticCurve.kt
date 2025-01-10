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

internal class EllipticCurveSerializer : KSerializer<EnumEllipticCurve> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(requireNotNull(EnumEllipticCurve::class.qualifiedName), PrimitiveKind.SHORT)

    override fun serialize(encoder: Encoder, value: EnumEllipticCurve) = encoder.encodeShort(value.value)
    override fun deserialize(decoder: Decoder): EnumEllipticCurve =
        requireNotNull(EnumEllipticCurve.find(decoder.decodeShort()))
}

/**
 * @author Cedric Hammes
 * @since  05/01/2024
 *
 * @see 9. Table "TPM_ECC_CURVE Constants", ISO/IEC 11889-2:2015 "TPM Library: Structures"
 */
@Serializable(with = EllipticCurveSerializer::class)
enum class EnumEllipticCurve(private val literal: String, internal val value: Short) {
    NONE("None", 0x0000),
    NIST_P192("NIST P192", 0x0001),
    NIST_P224("NIST P224", 0x0002),
    NIST_P256("NIST P256", 0x0003),
    NIST_P384("NIST P384", 0x0004),
    NIST_P521("NIST P521", 0x0005),
    BN_P256("BN P256", 0x0010),
    BN_P638("BN P638", 0x0011),
    SM2_P256("SM2 P256", 0x0012);

    override fun toString(): String = literal

    companion object {
        fun find(value: Short): EnumEllipticCurve? = EnumEllipticCurve.entries.firstOrNull { it.value == value }
    }
}

