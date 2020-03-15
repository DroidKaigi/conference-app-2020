package io.github.droidkaigi.confsched2020.widget.compose

import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.ui.core.setContent

/**
 * A version of [ViewGroup.setContent], which accepts a [LifecycleOwner] to automatically
 * dispose of the [Composition] once destroyed.
 */
fun ViewGroup.setContentWithLifecycle(
    lifecycle: LifecycleOwner,
    content: @Composable() () -> Unit
): Composition {
    val composition = setContent(content = content)

    val observer = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            composition.dispose()
        }
    }
    lifecycle.lifecycle.addObserver(observer)

    return composition
}
