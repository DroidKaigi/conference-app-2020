package io.github.droidkaigi.confsched2020.data.api.response

interface ContributorItemResponse {
    val id: Int
    val username: String
    val iconUrl: String
    val profileUrl: String
    val sort: Int
}
