package io.github.droidkaigi.confsched2020.ext

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Fragment.assistedViewModels(
    crossinline body: () -> T
): Lazy<T> {
    return viewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return body() as T
            }
        }
    }
}

inline fun <reified T : ViewModel> Fragment.assistedSavedStateViewModels(
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

inline fun <reified T : ViewModel> Fragment.assistedActivityViewModels(
    crossinline body: () -> T
): Lazy<T> {
    return activityViewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return body() as T
            }
        }
    }
}

inline fun <reified T : ViewModel> Fragment.assistedActivityViewModels(
    noinline keyProvider: () -> String,
    crossinline body: () -> T
): Lazy<T> {
    return this.createViewModelLazy(
        T::class,
        keyProvider,
        { requireActivity().viewModelStore },
        {
            object : ViewModelProvider.NewInstanceFactory() {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return body() as T
                }
            }
        }
    )
}

@MainThread
fun <VM : ViewModel> Fragment.createViewModelLazy(
    viewModelClass: KClass<VM>,
    keyProvider: () -> String,
    storeProducer: () -> ViewModelStore,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return KeyedViewModelLazy(viewModelClass, keyProvider, storeProducer, factoryPromise)
}

class KeyedViewModelLazy<VM : ViewModel>(
    private val viewModelClass: KClass<VM>,
    private val keyProvider: () -> String,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory
) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory).get(keyProvider(), viewModelClass.java).also {
                    cached = it
                }
            } else {
                viewModel
            }
        }

    override fun isInitialized() = cached != null
}
