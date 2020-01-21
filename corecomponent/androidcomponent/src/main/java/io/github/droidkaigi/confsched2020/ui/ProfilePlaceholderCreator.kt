package io.github.droidkaigi.confsched2020.ui

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import io.github.droidkaigi.confsched2020.widget.component.R

object ProfilePlaceholderCreator {

    fun create(context: Context): VectorDrawableCompat? =
        VectorDrawableCompat.create(
            context.resources,
            R.drawable.ic_person_outline_black_32dp,
            null
        )?.apply {
            setTint(
                AppCompatResources.getColorStateList(context, R.color.profile_icon).defaultColor
            )
        }
}
