package io.github.droidkaigi.confsched2020.session.ui.item

import androidx.appcompat.content.res.AppCompatResources
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailTitleBinding
import java.util.regex.Pattern

class SessionDetailTitleItem @AssistedInject constructor(
    @Assisted private val session: Session,
    @Assisted private val searchQuery: String?
) : BindableItem<ItemSessionDetailTitleBinding>(session.id.hashCode().toLong()) {
    override fun getLayout() = R.layout.item_session_detail_title

    override fun bind(binding: ItemSessionDetailTitleBinding, position: Int) {
        binding.session = session
        binding.lang = defaultLang()
        binding.title.setSearchHighlight()
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
                        setTextColor(
                            AppCompatResources.getColorStateList(
                                context,
                                R.color.session_detail_label
                            )
                        )
                        setChipBackgroundColorResource(R.color.session_detail_chip_category)
                    }
                )
                binding.tags.addView(
                    Chip(context).apply {
                        text = langLabel
                        isClickable = false
                        setTextColor(
                            AppCompatResources.getColorStateList(
                                context,
                                R.color.session_detail_label
                            )
                        )
                        setChipBackgroundColorResource(R.color.session_detail_chip_level)
                    }
                )
                binding.tags.tag = newTag
            }

            binding.sessionMessage.text = session.message?.getByLang(defaultLang())
            binding.sessionMessage.setSearchHighlight()
            binding.sessionMessage.isVisible = session.hasMessage
//            Test Code
//            binding.sessionMessage.text = "セッション部屋がRoom1からRoom3に変更になりました（サンプル）"
//            binding.sessionMessage.isVisible = true
        }
    }

    private fun TextView.setSearchHighlight() {
        doOnPreDraw {
            if (searchQuery.isNullOrEmpty()) return@doOnPreDraw
            val highlightColor = context.getThemeColor(R.attr.colorSecondary)
            val pattern = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(text)
            val spannableStringBuilder = SpannableStringBuilder(text)
            while (matcher.find()) {
                spannableStringBuilder.setSpan(
                    BackgroundColorSpan(highlightColor),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            text = spannableStringBuilder
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session,
            searchQuery: String? = null
        ): SessionDetailTitleItem
    }
}
