package io.github.droidkaigi.confsched2020.data.api.response

interface SponsorResponse {
    val id: Int
    val plan: String
    val planDetail: String
    val companyUrl: String
    val companyName: CompanyNameResponse
    val companyLogoUrl: String
    val hasBooth: Boolean
    val sort: Int
}
