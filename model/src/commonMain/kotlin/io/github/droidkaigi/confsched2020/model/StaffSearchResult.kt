package io.github.droidkaigi.confsched2020.model

data class StaffSearchResult(
    val staffs: List<Staff>,
    val query: String?
) {
    companion object {
        val EMPTY = StaffSearchResult(listOf(), null)
    }
}
