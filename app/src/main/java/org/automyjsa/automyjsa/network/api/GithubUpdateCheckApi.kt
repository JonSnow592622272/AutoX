package org.automyjsa.automyjsa.network.api

import org.automyjsa.automyjsa.network.entity.GithubReleaseInfo
import org.automyjsa.automyjsa.network.entity.GithubReleaseInfoList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GithubUpdateCheckApi {
    @GET("/repos/kkevsekk1/Automyjsx/releases/latest")
    @Headers("Cache-Control: no-cache")
    suspend fun getGithubLastReleaseInfo(): GithubReleaseInfo

    @GET("/repos/kkevsekk1/Automyjsx/releases/tags/{tag}")
    @Headers("Cache-Control: no-cache")
    suspend fun getGithubLastReleaseInfo(@Path("tag") tag: String): Response<GithubReleaseInfo>

    @GET("/repos/kkevsekk1/Automyjsx/releases")
    @Headers("Cache-Control: no-cache")
    suspend fun getGithubReleaseInfoList(): GithubReleaseInfoList
}
