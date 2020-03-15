package io.github.droidkaigi.confsched2020.widget.compose

import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.lifecycle.LifecycleOwner
import androidx.ui.core.Text

fun ViewGroup.setGreetingContentWithLifecycle(
    lifecycle: LifecycleOwner
): Unit {
    setContentWithLifecycle(lifecycle){
        Greeting("compose")
    }
}

@Composable
fun Greeting(name: String) {
    Text("Hello $name!")
}
