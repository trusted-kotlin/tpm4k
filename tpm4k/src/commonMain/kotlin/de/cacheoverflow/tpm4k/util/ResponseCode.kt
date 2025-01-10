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

package de.cacheoverflow.tpm4k.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class ResponseCodeSerializer : KSerializer<EnumResponseCode> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(requireNotNull(EnumResponseCode::class.qualifiedName), PrimitiveKind.SHORT)

    override fun serialize(encoder: Encoder, value: EnumResponseCode) = encoder.encodeInt(value.value)
    override fun deserialize(decoder: Decoder): EnumResponseCode =
        requireNotNull(EnumResponseCode.fromInt(decoder.decodeInt()))
}

/**
 * @author Cedric Hammes
 * @since  06/01/2024
 */
@Serializable(with = ResponseCodeSerializer::class)
enum class EnumResponseCode(val value: Int, private val message: String) {
    // @formatter:off
    SUCCESS          (0x000, "Success"),
    INITIALIZE       (0x100, "TPM not initialized"),
    FAILURE          (0x101, "Commands not being accepted because of a TPM failure"),
    SEQUENCE         (0x103, "Improper use of a sequence handle"),
    EXCLUSIVE        (0x121, "Command failed because audit sequence required exclusivity"),
    AUTH_TYPE        (0x124, "Authorization handle is not correct for command"),
    AUTH_MISSING     (0x125, "Command requires an authorization session for handle"),
    POLICY           (0x126, "Policy failure in math operation or invalid authPolicy value"),
    PCR              (0x127, "PCR check fail"),
    PCR_CHANGE       (0x128, "PCR have changed since checked"),
    UPGRADE          (0x12D, "-/-"),
    TOO_MANY_CONTEXTS(0x12E, "Context ID counter is at maximum"),
    AUTH_UNAVAILABLE (0x12F, "authValue or authPolicy is not available for selected entity"),
    REBOOT           (0x130, "A initialization and startup is required before the TPM can resume operation"),
    UNBALANCED       (0x131, "The protection algorithms are not reasonably balanced. Digest size > key size"),
    COMMAND_SIZE     (0x142, "Command size is inconsistent ith contents of the command buffer"),
    COMMAND_CODE     (0x143, "Command code is not supported by TPM"),
    AUTH_SIZE        (0x144, "Authorization size is out of range or the authorization area is greater than required"),
    AUTH_CONTEXT     (0x145, "Use of an authorization session with a context command"),
    NV_RANGE         (0x146, "Non-volatile (NV) offset + size is out of range"),
    NV_SIZE          (0x147, "Requested allocation size is larger than allowed"),
    NV_LOCKED        (0x148, "Non-volatile (NV) memory access locked"),
    NV_AUTHORIZATION (0x149, "NV access authorization failed in command actions"),
    NV_UNINITIALIZED (0x14A, "A NV memory index is used before being initialized or the state could not be restored"),
    NV_SPACE         (0x14B, "Insufficient space for NV memory allocation"),
    NV_DEFINED       (0x14C, "NV memory index or persistent object is already defined"),
    BAD_CONTEXT      (0x150, "Context in TPM2_ContextLoad is not valid"),
    CPHASH           (0x151, "cpHash value already set or not correct for use"),
    PARENT           (0x152, "The handle for parent is not a valid parent"),
    NEEDS_TEST       (0x153, "Function needs testing"),
    NO_RESULT        (0x154, "Unspecified error in internal function"),
    SENSITIVE        (0x155, "Sensitive data was not correctly unmarshalled after decryption"),
    ASYMMETRIC       (0x081, "Asymmetric algorithm is not supported or not correct"),
    ATTRIBUTES       (0x082, "Inconsistent attributes"),
    HASH             (0x083, "Hash algorithm not supported or not appropriate"),
    VALUE            (0x084, "Value is out of range or is not correct for the context"),
    HIERARCHY        (0x085, "Hierarchy is not enabled or is not correct for the use"),
    KEY_SIZE         (0x087, "Key size is not supported"),
    MGF              (0x088, "Mask generation function not supported"),
    OP_MODE          (0x089, "Mode of operation is not supported"),
    TYPE             (0x08A, "The type of value is not appropriate for this use"),
    HANDLE           (0x08B, "The handle is not correct for this use"),
    KDF              (0x08C, "Unsupported key derivation function or function not appropriate for this use"),
    RANGE            (0x08D, "Value is out of allowed range"),
    AUTH_FAILED      (0x08E, "The authorization HMAC check failed and DA counter was incremented"),
    NONCE            (0x08F, "Invalid size of nonce"),
    PP               (0x090, "Authorization requires assertion of PP"),
    SCHEME           (0x092, "Unsupported or incompatible scheme"),
    WRONG_STRUCT_SIZE(0x095, "Structure is the wrong size"),
    SYMMETRIC        (0x096, "Unsupported symmetric algorithm or key size, or not appropriate for instance"),
    TAG              (0x097, "Incorrect structure tag"),
    SELECTOR         (0x098, "Union selector is incorrect"),
    INSUFFICIENT     (0x09A, "TPM was unable to unmarshal a value because not enough octets where in the input buffer"),
    SIGNATURE        (0x09B, "The signature is not valid"),
    KEY              (0x09C, "Key fields are not compatible with the selected use"),
    POLICY_FAIL      (0x09D, "Policy check failed"),
    INTEGRITY        (0x09F, "Integrity check failed"),
    TICKET           (0x0A0, "Invalid ticket"),
    RESERVED_BITS    (0x0A1, "Reserved bits not set to zero as required"),
    BAD_AUTH         (0x0A2, "Authorization failure without DA implications"),
    EXPIRED          (0x0A3, "The policy has expired"),
    POLICY_CC        (0x0A4, "The command code in the policy is not the command code of the command"),
    BINDING          (0x0A5, "Public and sensitive deportation of an object are not cryptographically bound"),
    CURVE_UNSUPPORTED(0x0A6, "EC curve is not supported"),
    ECC_POINT        (0x0A7, "EC point is not on the required curve"),
    CONTEXT_GAP      (0x901, "Gap for context ID is too large"),
    OBJECT_MEMORY    (0x902, "Out of memory for object contexts"),
    SESSION_MEMORY   (0x903, "Out of memory for session contexts"),
    MEMORY           (0x904, "Out of shared object/session memory or need space for internal operations"),
    SESSION_HANDLES  (0x905, "Out of session handles - a session must be flushed before a new session may be created"),
    OBJECT_HANDLES   (0x906, "Out of object handles - handle space for objects is depleted and a reboot is required"),
    LOCALITY         (0x907, "Bad locality"),
    YIELDED          (0x908, "TPM has suspended operation on the command"),
    CANCELLED        (0x909, "The command was cancelled"),
    TESTING          (0x90A, "TPM is performing self tests"),
    LOCKOUT          (0x921, "Authorizations for objects subject to DA protection are not allowed at this time"),
    RETRY            (0x922, "TPM was not able to start the command"),
    NV_UNAVAILABLE   (0x923, "Command may require writing of NV which is currently not available"),
    UNKNOWN          (0xAAA, "Unknown error");
    // @formatter:on

    override fun toString(): String = message

    companion object {
        fun fromInt(rawValue: Int): EnumResponseCode {
            val value = if (rawValue and 0x080 != 0) {
                rawValue and (0x3F + 0x080)
            } else if (rawValue and 0x900 != 0) {
                rawValue and (0x7F + 0x900)
            } else if (rawValue and 0x100 != 0) {
                rawValue and (0x7F + 0x100)
            } else {
                rawValue and 0x7F
            }
            return (entries.firstOrNull { it.value == value } ?: UNKNOWN)
        }
    }
}
