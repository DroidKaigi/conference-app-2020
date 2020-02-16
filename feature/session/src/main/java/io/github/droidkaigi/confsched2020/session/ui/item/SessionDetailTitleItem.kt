package io.github.droidkaigi.confsched2020.session.ui.item

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.chip.Chip
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.ThumbsUpCount
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailTitleBinding
import io.github.droidkaigi.confsched2020.session.ui.animation.dropOut
import io.github.droidkaigi.confsched2020.session.ui.animation.pop
import io.github.droidkaigi.confsched2020.session.ui.animation.popUp
import java.util.regex.Pattern

class SessionDetailTitleItem @AssistedInject constructor(
    @Assisted private val session: Session,
    @Assisted private val searchQuery: String?,
    @Assisted private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    @Assisted private val thumbsUpCount: ThumbsUpCount,
    @Assisted private val thumbsUpListener: () -> Unit
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

            binding.thumbsUp.setOnClickListener {
                thumbsUpListener.invoke()
            }

            binding.thumbsUpCount = thumbsUpCount
            if (!thumbsUpCount.incrementedUpdated) {
                return
            }

            when (thumbsUpCount.incremented) {
                0 -> binding.incrementedThumbsUpCount.dropOut(lifecycleCoroutineScope)
                1 -> {
                    binding.incrementedThumbsUpCount.setIncrementedText(
                        count = thumbsUpCount.incremented
                    )
                    binding.incrementedThumbsUpCount.popUp(lifecycleCoroutineScope)
                }
                else -> {
                    binding.incrementedThumbsUpCount.setIncrementedText(
                        count = thumbsUpCount.incremented
                    )
                    binding.incrementedThumbsUpCount.pop(lifecycleCoroutineScope)
                }
            }
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

    private fun TextView.setIncrementedText(count: Int) {
        text = context.getString(
            R.string.thumbs_up_increment_label,
            count
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session,
            searchQuery: String? = null,
            lifecycleCoroutineScope: LifecycleCoroutineScope,
            thumbsUpCount: ThumbsUpCount,
            thumbsUpListener: () -> Unit
        ): SessionDetailTitleItem
    }
}
