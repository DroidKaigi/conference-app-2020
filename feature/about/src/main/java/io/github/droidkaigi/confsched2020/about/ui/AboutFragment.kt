package io.github.droidkaigi.confsched2020.about.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.FragmentAboutBinding
import io.github.droidkaigi.confsched2020.about.ui.viewmodel.AboutViewModel
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : DaggerFragment() {

    @Inject
    lateinit var aboutModelFactory: AboutViewModel.Factory
    private val aboutViewModel: AboutViewModel by assistedViewModels {
        aboutModelFactory.create()
    }

    @Inject
    lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }

    private var binding: FragmentAboutBinding by autoCleared()

    private var progressTimeLatch: ProgressTimeLatch by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_about,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.aboutRecycler.run {
            adapter = groupAdapter
            doOnApplyWindowInsets { recyclerView, insets, initialState ->
                // Set a bottom padding due to the system UI is enabled.
                recyclerView.updatePadding(
                    bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
                )
            }
        }

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        binding.staffs.setOnClickListener(
            Navigation.createNavigateOnClickListener(AboutFragmentDirections.actionAboutToStaffs())
        )
        // TODO: Add AboutUI into RecyclerView
    }
}

@Module
abstract class AboutFragmentModule {

    @Module
    companion object {

        @PageScope
        @JvmStatic
        @Provides
        fun providesLifecycleOwnerLiveData(
            aboutFragment: AboutFragment
        ): LiveData<LifecycleOwner> {
            return aboutFragment.viewLifecycleOwnerLiveData
        }
    }
}
