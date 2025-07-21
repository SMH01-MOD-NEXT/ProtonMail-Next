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

package ch.protonmail.android.mailupselling.data.remote

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ch.protonmail.android.mailcommon.data.worker.Enqueuer
import ch.protonmail.android.mailcommon.domain.util.requireNotBlank
import ch.protonmail.android.mailupselling.data.datasource.NPSFeedbackRemoteDataSource
import ch.protonmail.android.mailupselling.data.remote.resource.NPSFeedbackBody
import ch.protonmail.android.mailupselling.domain.repository.InstalledProtonApp
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.proton.core.domain.entity.UserId
import me.proton.core.util.kotlin.takeIfNotBlank
import timber.log.Timber

@HiltWorker
class NPSFeedbackWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val dataSource: NPSFeedbackRemoteDataSource
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val userId = UserId(requireNotBlank(inputData.getString(Keys.UserId), fieldName = "User id"))
        val comment = inputData.getString(Keys.Comment)?.takeIfNotBlank()
        val userTier = inputData.getString(Keys.UserTier)?.takeIfNotBlank()
        val userCountry = inputData.getString(Keys.UserCountry)?.takeIfNotBlank()
        val daysFromSignup = inputData.getInt(Keys.DaysFromSignup, 0)
        val ratingValue = inputData.getInt(Keys.RatingValue, -1).takeIf { it >= 0 }
        val skipped = inputData.getBoolean(Keys.Skipped, true)

        val vpnInstalled = inputData.getBoolean(Keys.VpnInstalled, false)
        val driveInstalled = inputData.getBoolean(Keys.DriveInstalled, false)
        val calendarInstalled = inputData.getBoolean(Keys.CalendarInstalled, false)
        val walletInstalled = inputData.getBoolean(Keys.WalletInstalled, false)
        val passInstalled = inputData.getBoolean(Keys.PassInstalled, false)

        val body = NPSFeedbackBody(
            ratingValue = ratingValue ?: NPSFeedbackBody.NO_RATING,
            comment = comment,
            userTier = userTier.orEmpty(),
            userCountry = userCountry.orEmpty(),
            daysFromSignup = daysFromSignup,
            vpnInstalled = vpnInstalled,
            driveInstalled = driveInstalled,
            calendarInstalled = calendarInstalled,
            walletInstalled = walletInstalled,
            passInstalled = passInstalled
        )
        return if (skipped) {
            dataSource.skip(userId, body)
        } else {
            dataSource.submit(userId, body)
        }.fold(
            ifLeft = {
                Timber
                    .tag("NPSFeedbackWorker")
                    .e("API error submitting feedback - error: %s", it)

                Result.failure()
            },
            ifRight = {
                Result.success()
            }
        )
    }

    private object Keys {
        const val UserId = "UserId"
        const val RatingValue = "RatingValue"
        const val Comment = "Comment"
        const val UserTier = "UserTier"
        const val UserCountry = "UserCountry"
        const val DaysFromSignup = "DaysFromSignup"
        const val Skipped = "Skipped"
        const val VpnInstalled = "VpnInstalled"
        const val DriveInstalled = "DriveInstalled"
        const val CalendarInstalled = "CalendarInstalled"
        const val WalletInstalled = "WalletInstalled"
        const val PassInstalled = "PassInstalled"
    }

    companion object {

        @Suppress("LongParameterList")
        fun enqueue(
            enqueuer: Enqueuer,
            userId: UserId,
            ratingValue: Int?,
            comment: String?,
            userTier: String,
            userCountry: String,
            daysFromSignup: Int,
            skipped: Boolean,
            installedProtonApps: Set<InstalledProtonApp>
        ) {
            enqueuer.enqueue<NPSFeedbackWorker>(
                userId,
                mapOf(
                    Keys.UserId to userId.id,
                    Keys.RatingValue to (ratingValue ?: -1),
                    Keys.Comment to comment.orEmpty(),
                    Keys.UserTier to userTier,
                    Keys.UserCountry to userCountry,
                    Keys.DaysFromSignup to daysFromSignup,
                    Keys.Skipped to skipped,
                    Keys.VpnInstalled to installedProtonApps.contains(InstalledProtonApp.VPN),
                    Keys.DriveInstalled to installedProtonApps.contains(InstalledProtonApp.Drive),
                    Keys.CalendarInstalled to installedProtonApps.contains(InstalledProtonApp.Calendar),
                    Keys.WalletInstalled to installedProtonApps.contains(InstalledProtonApp.Wallet),
                    Keys.PassInstalled to installedProtonApps.contains(InstalledProtonApp.Pass)
                )
            )
        }
    }
}
