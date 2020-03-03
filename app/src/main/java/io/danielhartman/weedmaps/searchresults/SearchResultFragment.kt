package io.danielhartman.weedmaps.searchresults

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.danielhartman.weedmaps.Dependencies
import io.danielhartman.weedmaps.databinding.SearchResultFragmentBinding
import io.danielhartman.weedmaps.searchresults.resultadapter.SearchResultAdapter

class SearchResultFragment : Fragment() {
    val searchResultKey = "searchResultKey"

    sealed class Action {
        object OnEnterScreen : Action()
        object UserScrolledToBottom : Action()
    }

    private var _binding: SearchResultFragmentBinding? = null
    private val binding get() = _binding!!
    private val resultAdapter by lazy {
        SearchResultAdapter {
            viewModel.handleAction(Action.UserScrolledToBottom)
        }
    }
    private val resultLayoutManager by lazy { LinearLayoutManager(context) }


    companion object {
        fun newInstance(searchTerm: String) = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putString(searchResultKey, searchTerm)
            }
        }
    }

    private lateinit var viewModel: SearchResultVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchResultFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            resultsRecycler.run {
                adapter = resultAdapter
                layoutManager = resultLayoutManager
            }
            this.backArrow.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.handleAction(Action.OnEnterScreen)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val term = arguments?.getString(searchResultKey) ?: ""
        val factory = SearchResultVMFactory(term, Dependencies.searchResultData(term))
        viewModel = ViewModelProviders.of(this, factory).get(SearchResultVM::class.java)
        viewModel.run {
            this.searchItems.observe(viewLifecycleOwner, Observer {
                resultAdapter.submitList(it)
            })
        }
    }

}