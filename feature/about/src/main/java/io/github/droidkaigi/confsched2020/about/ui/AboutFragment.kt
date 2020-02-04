package io.github.droidkaigi.confsched2020.about.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.FragmentAboutBinding
import io.github.droidkaigi.confsched2020.about.ui.AboutFragmentDirections.Companion.actionAboutToChrome
import io.github.droidkaigi.confsched2020.about.ui.AboutFragmentDirections.Companion.actionAboutToStaffs
import io.github.droidkaigi.confsched2020.about.ui.item.AboutHeaderItem
import io.github.droidkaigi.confsched2020.about.ui.item.AboutIconItem
import io.github.droidkaigi.confsched2020.about.ui.item.AboutItem
import io.github.droidkaigi.confsched2020.about.ui.item.AboutTextItem
import io.github.droidkaigi.confsched2020.about.ui.viewmodel.AboutViewModel
import io.github.droidkaigi.confsched2020.di.Injectable
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : Fragment(R.layout.fragment_about), Injectable {

    companion object {
        const val TWITTER_URL = "https://twitter.com/DroidKaigi"
        const val YOUTUBE_URL = "https://www.youtube.com/channel/UCgK6L-PKx2OZBuhrQ6mmQZw"
        const val MEDIUM_URL = "https://medium.com/droidkaigi"
        const val PRIVACY_URL = "http://www.association.droidkaigi.jp/privacy.html"
    }

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

    @Inject
    lateinit var aboutItemFactory: AboutItem.Factory
    @Inject
    lateinit var aboutHeaderItemFactory: AboutHeaderItem.Factory
    @Inject
    lateinit var aboutTextItemFactory: AboutTextItem.Factory
    @Inject
    lateinit var aboutIconItemFactory: AboutIconItem.Factory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAboutBinding.bind(view)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.aboutRecycler.run {
            adapter = groupAdapter
            doOnApplyWindowInsets { recyclerView, insets, initialState ->
                // Set a bottom padding due to the system UI is enabled.
                recyclerView.updatePadding(
                    bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
                )
            }
        }

        groupAdapter.update(
            listOf(
                aboutHeaderItemFactory.create(
                    onClickTwitter = {
                        findNavController().navigate(actionAboutToChrome(TWITTER_URL))
                    },
                    onClickYoutube = {
                        findNavController().navigate(actionAboutToChrome(YOUTUBE_URL))
                    },
                    onClickMedium = {
                        findNavController().navigate(actionAboutToChrome(MEDIUM_URL))
                    }
                ),
                aboutIconItemFactory.create(
                    getString(R.string.about_item_access)
                ) {
                    systemViewModel.navigateToAccessMap(requireActivity())
                },
                aboutItemFactory.create(
                    getString(R.string.about_item_staff)
                ) {
                    findNavController().navigate(actionAboutToStaffs())
                },
                aboutItemFactory.create(
                    getString(R.string.about_item_privacy_policy)
                ) {
                    findNavController().navigate(actionAboutToChrome(PRIVACY_URL))
                },
                aboutItemFactory.create(
                    getString(R.string.about_item_licence)
                ) {
                    // TODO go licence-page
                },
                aboutTextItemFactory.create(
                    getString(R.string.about_item_app_version),
                    "1.2.0" // TODO get app version code
                )
            )
        )

        binding.progressBar.hide()
    }
}

@Module
abstract class AboutFragmentModule {

    companion object {

        @PageScope
        @Provides
        fun providesLifecycleOwnerLiveData(
            aboutFragment: AboutFragment
        ): LiveData<LifecycleOwner> {
            return aboutFragment.viewLifecycleOwnerLiveData
        }
    }
}
