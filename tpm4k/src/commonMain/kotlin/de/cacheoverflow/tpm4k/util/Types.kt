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

/**
 * This type is representing a handle used in TPMs. Handles are 32-bit values used to reference shielded locations of
 * objects within the TPM.
 *
 * @author Cedric Hammes
 * @since  05/01/2024
 *
 * @see 8.1 "Handles/Introduction", ISO/IEC 11889-1:2015 "TPM Library: Architecture"
 */
typealias TpmHandle = UInt
