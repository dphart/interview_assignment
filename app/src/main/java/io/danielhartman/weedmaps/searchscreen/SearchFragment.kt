package io.danielhartman.weedmaps.searchscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
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
        viewModel = ViewModelProviders.of(this).get(SearchFragmentVM::class.java)
        viewModel.run {
            searchHistory.observe(viewLifecycleOwner, Observer {
                recentSearchAdapter.data = it
            })
            textOverride.observe(viewLifecycleOwner, Observer {
                binding.searchEntryField.setText(it)
            })
        }
    }

    sealed class Action {
        class SearchClicked(val searchValue: String) : Action()
        class RecentClicked(val recentValue: String) : Action()
    }

}
