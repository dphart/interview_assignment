package io.danielhartman.weedmaps.searchscreen

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.BasePermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import io.danielhartman.weedmaps.Dependencies
import io.danielhartman.weedmaps.R
import io.danielhartman.weedmaps.databinding.SearchFragmentBinding
import io.danielhartman.weedmaps.searchresults.SearchResultFragment
import io.danielhartman.weedmaps.searchscreen.recentadapter.RecentSearchAdapter

class SearchFragment : Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private val recentSearchAdapter by lazy {
        RecentSearchAdapter { recentItem ->
            viewModel.handleAction(Action.RecentClicked(recentItem))
        }
    }
    private val recentLayoutManager by lazy { LinearLayoutManager(context) }

    companion object {
        fun newInstance() =
            SearchFragment()
    }

    private lateinit var viewModel: SearchFragmentVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            searchButton.run {
                setOnClickListener {
                    val userInput = searchEntryField.editableText.toString().trim()
                    viewModel.handleAction(Action.SearchClicked(searchValue = userInput))
                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .add(R.id.container, SearchResultFragment.newInstance(userInput))
                        .addToBackStack(null)
                        .commit()
                }


            }
            recyclerView.run {
                adapter = recentSearchAdapter
                layoutManager = recentLayoutManager
            }
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = SearchFragmentVMFactory(Dependencies.recentSearches, Dependencies.locationData)
        viewModel = ViewModelProviders.of(this, factory).get(SearchFragmentVM::class.java)
        viewModel.run {
            searchHistory.observe(viewLifecycleOwner, Observer {
                recentSearchAdapter.data = it
            })
            textOverride.observe(viewLifecycleOwner, Observer {
                binding.searchEntryField.setText(it)
            })
        }
        Dexter.withActivity(activity).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: BasePermissionListener(){
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Dependencies.locationData.getLatestLocation()
                }
            }).check()
    }

    sealed class Action {
        class SearchClicked(val searchValue: String) : Action()
        class RecentClicked(val recentValue: String) : Action()
        class LocationGranted():Action()
    }

}
