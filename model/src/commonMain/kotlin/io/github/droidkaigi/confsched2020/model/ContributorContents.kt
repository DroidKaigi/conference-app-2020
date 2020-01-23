package io.github.droidkaigi.confsched2020.model

data class ContributorContents(
    val contributors: List<Contributor>
) {
    companion object {
        val EMPTY = ContributorContents(emptyList())
    }
}
