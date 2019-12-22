# DroidKaigi 2020 official Android app
DroidKaigi 2020 is a conference tailored for developers on 20th and 21th February 2020.  

You can install the prodution app via Get it on Google Play.  
// TODO: Add link to Google Play

And also, you can try the binary under development built on master branch through Try it on your device via DeployGate
// TODO: Add link to DeployGate

# Features


# Contributing

We always welcome any and all contributions! See CONTRIBUTING.md for more information
// TODO: Add link to CONTRIBUTING.md

## Requirements

Android Studio 3.6 and higher. You can download it from this page.

# Development Environment

## Multi module project
We separate the modules for each feature. We use the Dynamic feature modules for additional features.

![image](https://user-images.githubusercontent.com/1386930/71317852-528aa380-24cb-11ea-886f-8dabf225567a.png)

## Kotlin Multiplatform Project


# Architecture
This app uses an AndroidJetpack(AAC) based architecture using AAC(LiveData, ViewModel, Room), Kotlin, Kotlin Coroutines Flow, DataBinding, Dagger, Firebase.
![DroidKaigi 2020  All](https://user-images.githubusercontent.com/1386930/71318763-9a63f780-24d8-11ea-82c2-f3d9644af2aa.png)

![project dependency](https://github.com/DroidKaigi/conference-app-2020/blob/master/project.dot.png)


Fragment

```kotlin
    @Inject lateinit var sessionsFactory: SessionsViewModel.Factory
    private val sessionsViewModel by assistedViewModels {
        sessionsFactory.create(it)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionRecycler.adapter = groupAdapter

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        sessionDetailViewModel.uiModel.observe(viewLifecycleOwner) { uiModel: SessionDetailViewModel.UiModel ->
            progressTimeLatch.loading = uiModel.isLoading
            if (uiModel.session != null) {
                Toast.makeText(context, uiModel.session.toString(), Toast.LENGTH_LONG).show()
            }
            showError(uiModel.error)
        }
    }
```

ViewModel

```kotlin
class SessionsViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : ViewModel() {
    // UiModel definition
    data class UiModel(
        val sessionContents: SessionContents?,
        val isLoading: Boolean,
        val error: Error
    ) {
        sealed class Error {
            class FailLoadSessions(val e: Throwable) : Error()
            class FailFavorite(val e: Throwable) : Error()
            object None : Error()
            companion object {
                fun of(
                    sessionContentsLoadState: LoadState<SessionContents>,
                    favoriteLoadingState: LoadingState
                ): Error {
                    ...
                }
            }
        }

        companion object {
            val EMPTY = UiModel(null, false, Error.None)
        }
    }

    // LiveDatas
    private val sessionLoadState: LiveData<LoadState<SessionContents>> = liveData {
        emitSource(
            sessionRepository.sessionContents()
                .toLoadingState()
                .asLiveData()
        )
        sessionRepository.refresh()
    }
    private val favoriteLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.Initialized)

    // Compose UiModel
    val uiModel: LiveData<UiModel> = composeBy(
        initialValue = UiModel.EMPTY,
        liveData1 = sessionLoadState,
        liveData2 = favoriteLoadingState
    ) { current: UiModel,
        sessionsLoadState: LoadState<SessionContents>,
        favoriteLoadingState: LoadingState ->
        val isLoading = sessionsLoadState.isLoading || favoriteLoadingState.isLoading
        val sessionContents = when (sessionsLoadState) {
            is LoadState.Loaded -> {
                sessionsLoadState.value
            }
            else -> {
                current.sessionContents
            }
        }
        UiModel(
            sessionContents = sessionContents,
            isLoading = isLoading,
            error = UiModel.Error.of(
                sessionContentsLoadState = sessionsLoadState,
                favoriteLoadingState = favoriteLoadingState
            )
        )
    }

    // Functions
    fun favorite(session: Session): LiveData<Unit> {
        return liveData {
            try {
                favoriteLoadingState.value = LoadingState.Loading
                sessionRepository.toggleFavorite(session)
                favoriteLoadingState.value = LoadingState.Loaded
            } catch (e: Exception) {
                favoriteLoadingState.value = LoadingState.Error(e)
            }
        }
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
inline fun <T : Any, LIVE1 : Any, LIVE2 : Any> composeBy(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    crossinline block: (T, LIVE1, LIVE2) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        addSource(liveData1) { data ->
            callBlockWhenNonNullValue(liveData1, liveData2, block)
        }
        addSource(liveData2) { data ->
            callBlockWhenNonNullValue(liveData1, liveData2, block)
        }
    }
}

inline fun <LIVE1 : Any, LIVE2 : Any, T : Any> MediatorLiveData<T>.callBlockWhenNonNullValue(
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    crossinline block: (T, LIVE1, LIVE2) -> T
) {
    val currentValue = value
    val liveData1Value = liveData1.value
    val liveData2Value = liveData2.value
    if (currentValue != null && liveData1Value != null && liveData2Value != null) {
        value = block(currentValue, liveData1Value, liveData2Value)
    }
}

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
