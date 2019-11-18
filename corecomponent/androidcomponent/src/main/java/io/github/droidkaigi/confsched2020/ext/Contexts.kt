package io.github.droidkaigi.confsched2020.ext

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use

@ColorInt
fun Context.getThemeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(intArrayOf(themeAttrId))
        .use {
            it.getColor(0, Color.MAGENTA)
        }
}