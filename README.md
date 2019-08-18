# DroidKaigi 2020 official Android app [WIP]

 * Modern Android AAC based app
    * ViewModel using LiveData Kotlin Coroutine builder
    * Kotlin Coroutine Flow for observing data
 * Dynamic feature modules
 * Kotlin Multiplatform Project
 * Groupie

![project dependency](https://github.com/DroidKaigi/conference-app-2020/blob/master/project.dot.png)


Fragment

```kotlin
    @Inject lateinit var sessionsFactory: SessionsViewModel.Factory
    private val sessionsViewModel by assistedViewModels {
        sessionsFactory.create(it)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ...
        
        sessionsViewModel.sessionContentsLoadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingState.Loaded -> {
                    groupAdapter.update(state.value.sessions.map {
                        sessionItemFactory.create(it, sessionsViewModel)
                    })
                }
                LoadingState.Loading -> Unit
                is LoadingState.Error -> {
                    state.e.printStackTrace()
                }
            }
        }
    }
```

ViewModel

```kotlin
class SessionsViewModel @AssistedInject constructor(
    @Assisted private val state: SavedStateHandle,
    val sessionRepository: SessionRepository
) : ViewModel() {

    val sessionContentsLoadingState: LiveData<LoadingState<SessionContents>> = liveData {
        emitSource(
            sessionRepository.sessionContents()
                .toLoadingState()
                .asLiveData()
        )
        sessionRepository.refresh()
    }
    
    ...
    
    @AssistedInject.Factory
    interface Factory {
        fun create(state: SavedStateHandle): SessionsViewModel
    }
}
```

Repository

```kotlin
    override suspend fun sessionContents(): Flow<SessionContents> = coroutineScope {
        val sessionsFlow = sessions()
            .map {
                it.sortedBy { it.startTime }
            }
            ...
```

Extensions

```kotlin
fun <reified T : ViewModel> Fragment.assistedViewModels(
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

fun <T> Flow<T>.toLoadingState(): Flow<LoadingState<T>> {
    return map<T,LoadingState<T>> { LoadingState.Loaded(it) }
        .onStart {
            @Suppress("UNCHECKED_CAST")
            emit(LoadingState.Loading as LoadingState<T>)
        }
        .catch { e ->
            emit(LoadingState.Error<T>(e))
        }
}


// waiting https://android-review.googlesource.com/c/platform/frameworks/support/+/1096457
fun <T> Flow<T>.asLiveData(): LiveData<T> = liveData {
    collect {
        emit(it)
    }
}
```