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
    private val context: Context
) : RecyclerView.ItemDecoration() {

    private val res: Resources = context.resources
    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = sessionTimeTextSizeInPx
            color = context.getThemeColor(AndroidRAttr.textColorHint)
        }
    }
    private val sessionTimeTextSizeInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_size).toFloat()
    }
    private val sessionTimeSpaceInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_space)
    }
    private val sessionTimeTextMarginTopInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_margin_top).toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        parent.forEachIndexed { index, view ->
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return

            val sessionItem = adapter.getItem(position) as SessionItem
            val startTimeText = calcTimeText(position, view)

            if (index > 0) {
                val lastSessionItem = adapter.getItem(position - 1) as SessionItem
                if (sessionItem.startSessionTime() == lastSessionItem.startSessionTime()) return@forEachIndexed
            }

            c.drawText(
                startTimeText.value,
                startTimeText.positionX,
                startTimeText.positionY,
                textPaint
            )
        }
    }

    private fun calcTimeText(position: Int, view: View): StartTimeText {
        val sessionItem = adapter.getItem(position) as SessionItem
        val nextSessionItem = if (position < adapter.itemCount - 1) {
            adapter.getItem(position + 1) as SessionItem
        } else null

        var positionY =
            view.top.coerceAtLeast(sessionTimeTextMarginTopInPx.toInt()) + sessionTimeTextMarginTopInPx + sessionTimeTextSizeInPx
        if (sessionItem.startSessionTime() != nextSessionItem?.startSessionTime()) {
            positionY = positionY.coerceAtMost(view.bottom.toFloat())
        }
        return StartTimeText(
            value = sessionItem.startSessionTime(),
            positionX = (sessionTimeSpaceInPx / 2).toFloat(),
            positionY = positionY
        )
    }

    private data class StartTimeText(
        val value: String,
        val positionX: Float,
        val positionY: Float
    )
}