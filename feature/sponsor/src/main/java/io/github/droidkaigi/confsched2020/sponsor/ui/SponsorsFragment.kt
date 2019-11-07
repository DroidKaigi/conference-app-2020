package io.github.droidkaigi.confsched2020.sponsor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.sponsor.databinding.FragmentSponsorsBinding
import io.github.droidkaigi.confsched2020.sponsor.ui.item.SponsorItem
import io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel.SponsorsViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import javax.inject.Inject

class SponsorsFragment : DaggerFragment() {

    private lateinit var binding: FragmentSponsorsBinding

    @Inject lateinit var sponsorsModelFactory: SponsorsViewModel.Factory
    private val sponsorsViewModel by assistedViewModels {
        sponsorsModelFactory.create()
    }

    @Inject
    lateinit var sponsorItemFactory: SponsorItem.Factory

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sponsors,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sponsorRecycler.adapter = groupAdapter

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        sponsorsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            progressTimeLatch.loading = uiModel.isLoading

        }
    }
}

@Module
abstract class SponsorsFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic @Provides fun providesLifeCycleLiveData(
            sponsorsFragment: SponsorsFragment
        ): LiveData<LifecycleOwner> {
            return sponsorsFragment.viewLifecycleOwnerLiveData
        }
    }
}