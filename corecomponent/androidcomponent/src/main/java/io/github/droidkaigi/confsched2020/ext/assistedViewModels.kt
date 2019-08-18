package io.github.droidkaigi.confsched2020.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

inline fun <reified T : ViewModel> Fragment.assistedViewModels(
        crossinline body: (state: SavedStateHandle) -> T
    ): Lazy<T> {
        return viewModels {
            object : AbstractSavedStateViewModelFactory(this, arguments) {
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    @Suppress("UNCHECKED_CAST")
                    return body(handle) as T
                }
            }
        }
    }