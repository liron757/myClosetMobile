package com.example.mycloset.fragments.postlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycloset.adapters.PostRecyclerAdapter
import com.example.mycloset.api.controllers.PostController
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.consts.Listener
import com.example.mycloset.databinding.FragmentSearchBinding
import com.example.mycloset.viewmodels.SearchViewModel
import com.example.mycloset.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    var queryTextChangedJob: Job? = null

    lateinit var binding: FragmentSearchBinding
    lateinit var searchViewModel: SearchViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var userViewModel: UserViewModel
    lateinit var adapter: PostRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    fun doSearch (newText: String, view: View) {
        PostController.instance.getPostsByOutfitName(
            newText, object : Listener<List<Post>> {
                override fun onComplete(data: List<Post>) {
                    adapter.onImageClickListener = (
                            object : PostRecyclerAdapter.OnItemClickListener {
                                override fun onItemClick(pos: Int) {

                                    val post = data[pos]
                                    val action =
                                        SearchFragmentDirections
                                            .actionSearchFragmentToPostFragment(post.id)
                                    findNavController(view).navigate(action)
                                }
                            })
                    adapter.onUsernameClickListener =
                        (object : PostRecyclerAdapter.OnItemClickListener {
                            override fun onItemClick(pos: Int) {
                                val post = data[pos]
                                val action =
                                    PostListFragmentDirections.actionPostListFragmentToUserProfileFragment(
                                        post.userName
                                    )
                                findNavController(view).navigate(action)
                            }
                        })

                    searchViewModel.data = data
                    adapter.updatePosts(data)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view: View = binding.root

        recyclerView = binding.rvSearchResult
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        binding.swOutfitSearch.clearFocus()

        adapter = PostRecyclerAdapter(
            userViewModel.user?.value, layoutInflater,
            searchViewModel.data, context
        )
        recyclerView.adapter = adapter

        binding.swOutfitSearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val text = newText?.trim()
                if (text == "" || text == null) {
                    return false
                }

                queryTextChangedJob?.cancel()
                queryTextChangedJob = lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)
                    doSearch(text, view)
                }

                return true
            }
        })

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        queryTextChangedJob?.cancel()
    }
}

