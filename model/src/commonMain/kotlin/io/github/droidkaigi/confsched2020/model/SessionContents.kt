package io.github.droidkaigi.confsched2020.model

data class SessionContents(
    val sessions: SessionList,
    val speakers: List<Speaker>,
    val rooms: List<Room>,
    val langs: List<Lang>,
    val langSupports: List<LangSupport>,
    val category: List<Category>,
    val audienceCategories: List<AudienceCategory>
) {
    companion object {
        val EMPTY = SessionContents(
            SessionList.EMPTY,
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
    }

    fun search(query: String): SearchResult {
        return SearchResult(
            sessions.filter {
                when (it) {
                    is SpeechSession ->
                        find(
                            query,
                            it.title.en,
                            it.title.ja,
                            it.desc,
                            *it.speakers.map { speaker -> speaker.name }.toTypedArray()
                        )
                    is ServiceSession ->
                        find(
                            query,
                            it.title.en,
                            it.title.ja
                        )
                }
            },
            speakers.filter {
                find(query, it.name, it.tagLine, it.bio)
            },
            query
        )
    }

    private fun find(query: String, vararg strings: String?): Boolean {
        return strings
            .find { it?.contains(query, true) ?: false } != null
    }
}
