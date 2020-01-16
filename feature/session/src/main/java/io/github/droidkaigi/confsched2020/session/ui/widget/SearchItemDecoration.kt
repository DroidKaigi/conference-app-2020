package io.github.droidkaigi.confsched2020.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import io.github.droidkaigi.confsched2020.session.R

class SearchItemDecoration(
    context: Context,
    private val getGroupId: (position: Int) -> Long,
    private val getInitial: (position: Int) -> String
) : RecyclerView.ItemDecoration() {

    private val textPaint: TextPaint
    private val labelPadding: Int
    private val fontMetrics: Paint.FontMetrics

    init {
        val resource = context.resources

        val attrs = context.obtainStyledAttributes(
            R.style.TextAppearance_DroidKaigi_Headline6,
            R.styleable.SearchHeader
        )
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = attrs.getColorOrThrow(R.styleable.SearchHeader_android_textColor)
            textSize = attrs.getDimensionOrThrow(R.styleable.SearchHeader_android_textSize)
            try {
                typeface = ResourcesCompat.getFont(
                    context,
                    attrs.getResourceIdOrThrow(R.styleable.SearchHeader_android_fontFamily)
                )
            } catch (_: Exception) {
                // ignore
            }
        }
        attrs.recycle()

        fontMetrics = textPaint.fontMetrics

        labelPadding = resource.getDimensionPixelSize(R.dimen.session_time_space) / 2
    }

    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDraw(c, parent, state)

        val totalItemCount = state.itemCount
        val lineHeight = textPaint.textSize + fontMetrics.descent
        var previousGroupId: Long
        var groupId: Long = EMPTY_ID

        parent.forEach { childView ->
            val position = parent.getChildAdapterPosition(childView)
            if (position < 0) return@forEach
            // Acquires the first character of the immediately preceding character and the Id of the character to be checked this time
            previousGroupId = groupId
            groupId = getGroupId(position)

            // If the current element is EMPTY or the same as the previous element,
            // there is nothing (if it differs from the previous element, proceed next)
            if (groupId == EMPTY_ID || previousGroupId == groupId) return@forEach

            // Get Initial and check if it is empty character
            val initial = getInitial(position)
            if (initial.isEmpty()) return@forEach

            // drawing
            val positionX = labelPadding - textPaint.measureText(initial) / 2
            val viewTop = childView.top + labelPadding
            val viewBottom = childView.bottom + childView.paddingBottom
            var textY = viewTop.coerceAtLeast(labelPadding) + lineHeight / 2
            if (position + 1 < totalItemCount) {
                val nextGroupId = getGroupId(position + 1)
                if (nextGroupId != groupId && viewBottom < textY + lineHeight) {
                    textY = viewBottom - lineHeight
                }
            }
            c.drawText(initial, positionX, textY, textPaint)
        }
    }

    companion object {
        const val EMPTY_ID: Long = -1
        const val DEFAULT_INITIAL = "*"
    }
}
