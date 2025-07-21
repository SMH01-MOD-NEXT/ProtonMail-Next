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

package ch.protonmail.android.mailupselling.data.repository

import ch.protonmail.android.mailcommon.data.worker.Enqueuer
import ch.protonmail.android.mailupselling.data.remote.NPSFeedbackWorker
import ch.protonmail.android.mailupselling.domain.repository.InstalledProtonApp
import ch.protonmail.android.mailupselling.domain.repository.NPSFeedbackRepository
import me.proton.core.domain.entity.UserId
import javax.inject.Inject

class NPSFeedbackRepositoryImpl @Inject constructor(
    private val enqueuer: Enqueuer
) : NPSFeedbackRepository {

    override fun enqueue(
        userId: UserId,
        ratingValue: Int?,
        comment: String?,
        userTier: String,
        userCountry: String,
        daysFromSignup: Int,
        skipped: Boolean,
        installedProtonApps: Set<InstalledProtonApp>
    ) {
        NPSFeedbackWorker.enqueue(
            enqueuer = enqueuer,
            userId = userId,
            ratingValue = ratingValue,
            comment = comment,
            userTier = userTier,
            userCountry = userCountry,
            daysFromSignup = daysFromSignup,
            skipped = skipped,
            installedProtonApps = installedProtonApps
        )
    }
}
