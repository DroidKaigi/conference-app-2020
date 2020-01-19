package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
import com.google.android.material.chip.Chip
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailTitleBinding

class SessionDetailTitleItem(
    private val context: Context,
    private val session: Session,
    private val onClickSurvey: () -> Unit
) :
    BindableItem<ItemSessionDetailTitleBinding>() {
    override fun getLayout() = R.layout.item_session_detail_title

    override fun bind(binding: ItemSessionDetailTitleBinding, position: Int) {
        binding.session = session
        binding.lang = defaultLang()
        binding.time.text = session.timeSummary(defaultLang(), defaultTimeZoneOffset())
        if (session is SpeechSession) {
            val langLabel = session.lang.text.getByLang(defaultLang())
            val categoryLabel = session.category.name.getByLang(defaultLang())
            val newTag = "$categoryLabel:$categoryLabel"
            val savedTag = binding.tags.tag
            if (savedTag != newTag) {
                binding.tags.removeAllViews()
                binding.tags.addView(Chip(context).apply {
                    text = categoryLabel
                    isClickable = false
                })
                binding.tags.addView(Chip(context).apply {
                    text = langLabel
                    isClickable = false
                })
                binding.tags.tag = newTag
            }
        }
        binding.survey.setOnClickListener {
            onClickSurvey()
        }
    }
}