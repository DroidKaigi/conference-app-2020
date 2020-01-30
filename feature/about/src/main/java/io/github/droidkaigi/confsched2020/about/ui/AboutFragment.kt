package io.github.droidkaigi.confsched2020.about.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.FragmentAboutBinding
import io.github.droidkaigi.confsched2020.about.ui.AboutFragmentDirections.Companion.actionAboutToChrome
import io.github.droidkaigi.confsched2020.about.ui.AboutFragmentDirections.Companion.actionAboutToStaffs
import io.github.droidkaigi.confsched2020.about.ui.viewmodel.AboutViewModel
import io.github.droidkaigi.confsched2020.di.Injectable
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : Fragment(R.layout.fragment_about), Injectable {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAboutBinding.bind(view)

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

        binding.progressBar.show()
        binding.staffs.setOnClickListener(
            Navigation.createNavigateOnClickListener(actionAboutToStaffs())
        )
        binding.twitter.setOnClickListener {
            openTwitter()
        }
        // TODO: Add AboutUI into RecyclerView
    }

    private fun openTwitter() {
        findNavController().navigate(actionAboutToChrome("https://twitter.com/DroidKaigi"))
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
