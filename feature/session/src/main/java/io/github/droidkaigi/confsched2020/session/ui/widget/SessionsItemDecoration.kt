package io.github.droidkaigi.confsched2020.session.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.view.forEachIndexed
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.util.AndroidRAttr

class SessionsItemDecoration(
    private val adapter: GroupAdapter<*>,
    private val context: Context,
    private val visibleSessionDate: Boolean
) : RecyclerView.ItemDecoration() {

    private val res: Resources = context.resources
    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.LEFT
            textSize = sessionTimeTextSizeInPx
            color = context.getThemeColor(AndroidRAttr.textColorHint)
        }
    }
    private val sessionTimeTextSizeInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_size).toFloat()
    }
    private val sessionTimeTextMarginTopInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_margin_top).toFloat()
    }
    private val sessionTimeTextMarginStartInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_margin_start).toFloat()
    }
    private val sessionTimeTextHeightInPx by lazy {
        sessionTimeTextMarginTopInPx + sessionTimeTextSizeInPx
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        parent.forEachIndexed { index, view ->
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return

            val sessionItem = adapter.getItem(position) as SessionItem
            // we need least first session's label, skip to check if time label is same as last item on first item.
            if (position > 0 && index > 0) {
                val lastSessionItem = adapter.getItem(position - 1) as SessionItem
                if (sessionItem.startSessionTime() == lastSessionItem.startSessionTime()) {
                    return@forEachIndexed
                }
            }

            val startDateTimeText =
                calcDateTimeText(position, view, shouldShowDateText(position))

            if (startDateTimeText.dateText != null) {
                c.drawText(
                    startDateTimeText.dateText.value,
                    startDateTimeText.dateText.positionX,
                    startDateTimeText.dateText.positionY,
                    textPaint
                )
            }

            c.drawText(
                startDateTimeText.startTimeText.value,
                startDateTimeText.startTimeText.positionX,
                startDateTimeText.startTimeText.positionY,
                textPaint
            )
        }
    }

    private fun shouldShowDateText(position: Int): Boolean {
        if (!visibleSessionDate) {
            return false
        }
        if (position <= 0) {
            return true
        }
        val currentSessionItem = adapter.getItem(position) as SessionItem
        val lastSessionItem = adapter.getItem(position - 1) as SessionItem
        val isSameDateWithLastItem =
            currentSessionItem.startSessionDate() == lastSessionItem.startSessionDate()
        return when {
            isSameDateWithLastItem -> false
            else -> true
        }
    }

    private fun calcDateTimeText(
        position: Int,
        view: View,
        shouldShowDateText: Boolean
    ): StartDateTimeText {
        val sessionItem = adapter.getItem(position) as SessionItem
        val nextSessionItem = if (position < adapter.itemCount - 1) {
            adapter.getItem(position + 1) as SessionItem
        } else null

        // session date text
        val dateText = if (shouldShowDateText) {

            var sessionDateTextPositionY = view.top.coerceAtLeast(
                sessionTimeTextMarginTopInPx.toInt()
            ) + sessionTimeTextHeightInPx

            if (sessionItem.startSessionTime() != nextSessionItem?.startSessionTime()) {
                sessionDateTextPositionY = sessionDateTextPositionY.coerceAtMost(
                    view.bottom.toFloat() - sessionTimeTextHeightInPx
                )
            }

            PositionalText(
                value = sessionItem.startSessionDate(),
                positionX = sessionTimeTextMarginStartInPx,
                positionY = sessionDateTextPositionY
            )
        } else null

        val sessionTimeTextMarginTop = if (shouldShowDateText) {
            sessionTimeTextMarginTopInPx + sessionTimeTextHeightInPx
        } else {
            sessionTimeTextMarginTopInPx
        }

        // session time text
        var sessionTimeTextPositionY =
            (view.top + sessionTimeTextMarginTop.toInt() - sessionTimeTextMarginTopInPx.toInt())
                .coerceAtLeast(sessionTimeTextMarginTop.toInt()) + sessionTimeTextHeightInPx

        if (sessionItem.startSessionTime() != nextSessionItem?.startSessionTime()) {
            sessionTimeTextPositionY = sessionTimeTextPositionY.coerceAtMost(view.bottom.toFloat())
        }

        val startTimeText = PositionalText(
            value = sessionItem.startSessionTime(),
            positionX = sessionTimeTextMarginStartInPx,
            positionY = sessionTimeTextPositionY
        )

        return StartDateTimeText(dateText, startTimeText)
    }

    private data class StartDateTimeText(
        val dateText: PositionalText?,
        val startTimeText: PositionalText
    )

    private data class PositionalText(
        val value: String,
        val positionX: Float,
        val positionY: Float
    )
}
