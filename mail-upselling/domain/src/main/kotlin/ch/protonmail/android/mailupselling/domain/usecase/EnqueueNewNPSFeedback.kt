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

package ch.protonmail.android.mailupselling.domain.usecase

import java.util.Locale
import java.util.TimeZone
import ch.protonmail.android.mailcommon.domain.usecase.GetAppLocale
import ch.protonmail.android.mailupselling.domain.repository.GetInstalledProtonApps
import ch.protonmail.android.mailupselling.domain.repository.NPSFeedbackRepository
import kotlinx.coroutines.launch
import me.proton.core.auth.domain.usecase.GetPrimaryUser
import me.proton.core.util.kotlin.CoroutineScopeProvider
import javax.inject.Inject

class EnqueueNewNPSFeedback @Inject constructor(
    private val getAccountAgeInDays: GetAccountAgeInDays,
    private val getPrimaryUser: GetPrimaryUser,
    private val getAppLocale: GetAppLocale,
    private val getSubscriptionName: GetSubscriptionName,
    private val scopeProvider: CoroutineScopeProvider,
    private val repo: NPSFeedbackRepository,
    private val getInstalledProtonApps: GetInstalledProtonApps
) {
    fun skip() = onSupervisedScope { submitOrSkip(null, null, true) }

    fun submit(ratingValue: Int, comment: String?) = onSupervisedScope { submitOrSkip(ratingValue, comment, false) }

    private suspend fun submitOrSkip(
        ratingValue: Int?,
        comment: String?,
        skipped: Boolean
    ) {
        val user = getPrimaryUser() ?: return

        val subscriptionName = getSubscriptionName(user.userId).getOrNull()
        val localeDisplayName = getAppLocale().displayWithTimezone()
        val accountAgeInDays = getAccountAgeInDays(user)

        repo.enqueue(
            userId = user.userId,
            ratingValue = ratingValue,
            comment = comment,
            skipped = skipped,
            userTier = subscriptionName?.value.orEmpty(),
            userCountry = localeDisplayName,
            daysFromSignup = accountAgeInDays.days,
            installedProtonApps = getInstalledProtonApps()
        )
    }

    private fun onSupervisedScope(block: suspend () -> Unit) {
        scopeProvider.GlobalDefaultSupervisedScope.launch { block() }
    }

    private fun Locale.displayWithTimezone(): String {
        val timezone = TimeZone.getDefault()
        return "$displayName-${timezone.displayName}"
    }
}
