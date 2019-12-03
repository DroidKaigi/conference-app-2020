package io.github.droidkaigi.confsched2020.model

data class SponsorPlan(
    val plan: Plan,
    val sponsors: List<Sponsor>
) {

    enum class Plan(val id: String, val title: String) {
        PLATINUM("PLATINUM", "platinum\nsponsors"),
        GOLD("GOLD", "gold\nsponsors"),
        SUPPORTER("SUPPORTER", "sponsors"),
        COMMITTEE_SUPPORT("COMMITTEE_SUPPORT", "technical support\nfor network");

        companion object {
            fun from(category: String) = values().firstOrNull {
                it.id == category
            }
        }
    }
}
