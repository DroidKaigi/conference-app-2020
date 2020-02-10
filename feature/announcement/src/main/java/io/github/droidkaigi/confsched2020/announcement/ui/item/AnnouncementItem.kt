package io.github.droidkaigi.confsched2020.announcement.ui.item

import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.core.view.doOnPreDraw
import androidx.transition.TransitionManager
import com.soywiz.klock.DateFormat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.announcement.R
import io.github.droidkaigi.confsched2020.announcement.databinding.ItemAnnouncementBinding
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset

class AnnouncementItem @AssistedInject constructor(
    @Assisted val announcement: Announcement,
    @Assisted var showEllipsis: Boolean,
    @Assisted val expandListener: () -> Unit
) : BindableItem<ItemAnnouncementBinding>(announcement.id) {

    companion object {
        private const val ELLIPSIS_LINE_COUNT = 4
        private val dateFormatter = DateFormat("MM.dd HH:mm")
    }

    override fun getLayout(): Int = R.layout.item_announcement

    override fun bind(viewBinding: ItemAnnouncementBinding, position: Int) {
        val context = viewBinding.root.context
        viewBinding.announcementIcon.setImageResource(
            when (announcement.type) {
                // TODO: apply new icon.
                Announcement.Type.NOTIFICATION -> R.drawable.ic_feed_notification_blue_20dp
                Announcement.Type.ALERT -> R.drawable.ic_feed_alert_amber_20dp
                Announcement.Type.FEEDBACK -> R.drawable.ic_feed_feedback_cyan_20dp
            }
        )
        viewBinding.announcementTitle.text = announcement.title
        viewBinding.announcementContent.run {
            text = announcement.content
            setEllipsis(ELLIPSIS_LINE_COUNT, context.getString(R.string.read_more_label))
        }
        viewBinding.announcementDateTime.text =
            DateUtils.formatDateTime(
                context,
                announcement.publishedAt.toOffset(defaultTimeZoneOffset()).utc.unixMillisLong,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME
            )
    }

    override fun hasSameContentAs(other: Item<*>): Boolean =
        announcement == (other as? AnnouncementItem)?.announcement

    private fun TextView.setEllipsis(line: Int, label: String) {
        doOnPreDraw {
            val fullText = text
            if (lineCount > line && showEllipsis) {
                val lastLineStartPosition = layout.getLineStart(line - 1)
                val ellipsisWidth = paint.measureText(label)
                // Avoid shifting position, delete line feed code after target line.
                val target = fullText.substring(lastLineStartPosition).replace("\n", "")
                val lastLineText = TextUtils.ellipsize(
                    target,
                    paint,
                    width - ellipsisWidth,
                    TextUtils.TruncateAt.END
                )
                val ellipsisColor = context.getThemeColor(R.attr.colorSecondary)
                val onClickListener = {
                    TransitionManager.beginDelayedTransition(rootView as ViewGroup)
                    text = fullText
                    showEllipsis = !showEllipsis
                    expandListener()
                }
                val detailText = fullText.substring(0, lastLineStartPosition) + lastLineText
                val text = buildSpannedString {
                    clickableSpan(
                        onClickListener,
                        {
                            append(detailText)
                            color(ellipsisColor) {
                                append(label)
                            }
                        }
                    )
                }
                setText(text, TextView.BufferType.SPANNABLE)
                movementMethod = LinkMovementMethod.getInstance()
            }
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
                    // NOP
                }
            },
            builderAction
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            announcement: Announcement,
            showEllipsis: Boolean,
            expandListener: () -> Unit
        ): AnnouncementItem
    }
}
