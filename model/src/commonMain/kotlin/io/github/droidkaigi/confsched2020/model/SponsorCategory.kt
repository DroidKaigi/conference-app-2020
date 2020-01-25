package io.github.droidkaigi.confsched2020.model

data class SponsorCategory(
    val category: Category,
    val sponsors: List<Sponsor>
) {

    enum class Category(val id: String, val title: String) {
        PLATINUM("PLATINUM", "platinum sponsors"),
        GOLD("GOLD", "gold sponsors"),
        SUPPORTER("SUPPORTER", "supporter"),
        COMMITTEE_SUPPORT("COMMITTEE_SUPPORT", "technical support\nfor network");

        companion object {
            fun from(category: String) = values().firstOrNull {
                it.id == category
            }
        }
    }
}
