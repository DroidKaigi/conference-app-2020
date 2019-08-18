package io.github.droidkaigi.confsched2020.model

data class ContributorContents(
    val contributors: List<Contributor>
) {
    companion object {
        val EMTPY = ContributorContents(emptyList())
    }
}
