package io.github.droidkaigi.confsched2020.session.ui.item

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.core.view.doOnPreDraw
import androidx.transition.TransitionManager
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailDescriptionBinding
import java.util.regex.Pattern

class SessionDetailDescriptionItem @AssistedInject constructor(
    @Assisted private val session: Session,
    @Assisted private var showEllipsis: Boolean,
    @Assisted private val searchQuery: String?,
    @Assisted private val expandClickListener: () -> Unit
) : BindableItem<ItemSessionDetailDescriptionBinding>(session.id.hashCode().toLong()) {

    companion object {
        private const val ELLIPSIS_LINE_COUNT = 6
    }

    override fun getLayout() = R.layout.item_session_detail_description

    override fun bind(binding: ItemSessionDetailDescriptionBinding, position: Int) {
        // FIXME: When session description is empty and back from the floor map, the layout is broken without this.
        val fullDescription = if (session.desc.isEmpty()) "\n" else session.desc
        val textView = binding.sessionDescription
        textView.doOnPreDraw {
            textView.text = fullDescription
            textView.setSearchHighlight()
            // Return here if not more than the specified number of rows
            if (!(textView.lineCount > ELLIPSIS_LINE_COUNT && showEllipsis)) return@doOnPreDraw
            val lastLineStartPosition = textView.layout.getLineStart(ELLIPSIS_LINE_COUNT - 1)
            val context = textView.context
            val ellipsis = context.getString(R.string.ellipsis_label)
            val lastLineText = TextUtils.ellipsize(
                fullDescription.substring(lastLineStartPosition),
                textView.paint,
                textView.width - textView.paint.measureText(ellipsis),
                TextUtils.TruncateAt.END
            )
            val ellipsisColor = context.getThemeColor(R.attr.colorSecondary)
            val onClickListener = {
                TransitionManager.beginDelayedTransition(binding.itemRoot)
                textView.text = fullDescription
                showEllipsis = !showEllipsis
                expandClickListener()
            }
            val detailText = fullDescription.substring(0, lastLineStartPosition) + lastLineText
            val text = buildSpannedString {
                clickableSpan(
                    onClickListener,
                    {
                        append(detailText)
                        color(ellipsisColor) {
                            append(ellipsis)
                        }
                    }
                )
            }
            textView.setText(text, TextView.BufferType.SPANNABLE)
            textView.setSearchHighlight()
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun SpannableStringBuilder.clickableSpan(
        clickListener: () -> Unit,
        builderAction: SpannableStringBuilder.() -> Unit
    ) {
        inSpans(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    clickListener()
                }

                override fun updateDrawState(ds: TextPaint) {
                    // nothing
                }
            },
            builderAction
        )
    }

    private fun TextView.setSearchHighlight() {
        if (searchQuery.isNullOrEmpty()) return
        val highlightColor = context.getThemeColor(R.attr.colorSecondary)
        val pattern = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        val spannableStringBuilder = SpannableStringBuilder(text)
        while (matcher.find()) {
            spannableStringBuilder.setSpan(
                BackgroundColorSpan(highlightColor),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        text = spannableStringBuilder
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session,
            showEllipsis: Boolean,
            searchQuery: String? = null,
            expandClickListener: () -> Unit
        ): SessionDetailDescriptionItem
    }
}
