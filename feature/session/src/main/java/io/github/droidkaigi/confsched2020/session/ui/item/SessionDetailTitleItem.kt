package io.github.droidkaigi.confsched2020.session.ui.item

import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailTitleBinding

class SessionDetailTitleItem @AssistedInject constructor(
    @Assisted private val session: Session
) :
    BindableItem<ItemSessionDetailTitleBinding>() {
    override fun getLayout() = R.layout.item_session_detail_title

    override fun bind(binding: ItemSessionDetailTitleBinding, position: Int) {
        binding.session = session
        binding.lang = defaultLang()
        if (session is SpeechSession) {
            val langLabel = session.lang.text.getByLang(defaultLang())
            val categoryLabel = session.category.name.getByLang(defaultLang())
            val newTag = "$categoryLabel:$categoryLabel"
            val savedTag = binding.tags.tag
            if (savedTag != newTag) {
                binding.tags.removeAllViews()
                val context = binding.tags.context
                binding.tags.addView(
                    Chip(context).apply {
                        text = categoryLabel
                        isClickable = false
                    }
                )
                binding.tags.addView(
                    Chip(context).apply {
                        text = langLabel
                        isClickable = false
                    }
                )
                binding.tags.tag = newTag
            }

            binding.sessionMessage.text = session.message?.getByLang(defaultLang())
            binding.sessionMessage.isVisible = session.hasMessage
//            Test Code
//            binding.sessionMessage.text = "セッション部屋がRoom1からRoom3に変更になりました（サンプル）"
//            binding.sessionMessage.isVisible = true
        }
    }

    override fun isSameAs(other: Item<*>?) = other is SessionDetailTitleItem

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session
        ): SessionDetailTitleItem
    }
}
