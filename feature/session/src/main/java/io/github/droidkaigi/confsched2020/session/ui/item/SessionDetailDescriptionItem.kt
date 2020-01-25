package io.github.droidkaigi.confsched2020.session.ui.item

import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.doOnPreDraw
import androidx.transition.TransitionManager
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailDescriptionBinding

class SessionDetailDescriptionItem @AssistedInject constructor(
    @Assisted private val session: Session
) :
    BindableItem<ItemSessionDetailDescriptionBinding>() {

    companion object {
        private const val ELLIPSIS_LINE_COUNT = 6
    }

    private var showEllipsis = true

    override fun getLayout() = R.layout.item_session_detail_description

    override fun isSameAs(other: Item<*>?): Boolean = other is SessionDetailDescriptionItem

    override fun bind(binding: ItemSessionDetailDescriptionBinding, position: Int) {
        val fullDescription = session.desc
        val textView = binding.sessionDescription
        val moreTextView = binding.sessionDescriptionMore
        moreTextView.visibility = View.GONE
        textView.doOnPreDraw {
            textView.text = fullDescription
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
            @Suppress("DEPRECATION") val ellipsisColor = context.resources.getColor(R.color.transparent)
            val detailText = fullDescription.substring(0, lastLineStartPosition) + lastLineText
            textView.setText(detailText, TextView.BufferType.SPANNABLE)
            textView.movementMethod = LinkMovementMethod.getInstance()

            moreTextView.visibility = View.VISIBLE
            moreTextView.setOnClickListener {
                TransitionManager.beginDelayedTransition(binding.itemRoot)
                textView.text = fullDescription
                showEllipsis = !showEllipsis
            }
            // Calculate and set coordinates to more button
            val dp = context.resources.displayMetrics.density
            val lastLineOffset = textView.layout.getLineForOffset(lastLineStartPosition + lastLineText.length)
            val x =
                textView.layout.getPrimaryHorizontal(lastLineStartPosition + lastLineText.length)
            val firstLineBaseLine = textView.layout.getLineBaseline(0)
            val lastLineBaseLine = textView.layout.getLineBaseline(lastLineOffset)
            moreTextView.translationX = x - 16 * dp
            moreTextView.translationY = (lastLineBaseLine - firstLineBaseLine).toFloat()
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session
        ): SessionDetailDescriptionItem
    }
}
