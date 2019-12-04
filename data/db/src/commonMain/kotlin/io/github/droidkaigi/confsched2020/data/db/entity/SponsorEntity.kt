package io.github.droidkaigi.confsched2020.data.db.entity

interface SponsorEntity {
    val id: Int
    val plan: String
    val planDetail: String
    val companyUrl: String
    val companyName: CompanyNameEntity
    val companyLogoUrl: String
    val hasBooth: Boolean
    val sort: Int
}
