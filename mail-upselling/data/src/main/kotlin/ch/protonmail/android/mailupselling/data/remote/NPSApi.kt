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

import ch.protonmail.android.mailupselling.data.remote.resource.NPSFeedbackBody
import me.proton.core.network.data.protonApi.BaseRetrofitApi
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface NPSApi : BaseRetrofitApi {

    @POST("api/vpn/v1/nps/dismiss")
    suspend fun skip(@Body body: NPSFeedbackBody): ResponseBody

    @POST("api/vpn/v1/nps/submit")
    suspend fun submit(@Body body: NPSFeedbackBody): ResponseBody
}
