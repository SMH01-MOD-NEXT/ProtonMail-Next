/*
 * Copyright (c) 2022 Proton Technologies AG
 * This file is part of Proton Technologies AG and Proton Mail.
 *
 * Proton Mail is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Mail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Mail. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.protonmail.android.mailupselling.data.remote.resource

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NPSFeedbackBody(

    @SerialName("rating_value")
    val ratingValue: Int, // [-1, 10]

    @SerialName("comment")
    val comment: String?,

    @SerialName("user_tier")
    val userTier: String,

    @SerialName("user_country")
    val userCountry: String,

    @SerialName("device_os")
    val deviceOs: String = "Android",

    @SerialName("days_from_signup")
    val daysFromSignup: Int,

    @SerialName("vpn_installed")
    val vpnInstalled: Boolean,

    @SerialName("pass_installed")
    val passInstalled: Boolean,

    @SerialName("wallet_installed")
    val walletInstalled: Boolean,

    @SerialName("calendar_installed")
    val calendarInstalled: Boolean,

    @SerialName("drive_installed")
    val driveInstalled: Boolean
) {
    companion object {
        const val NO_RATING = -1
    }
}
