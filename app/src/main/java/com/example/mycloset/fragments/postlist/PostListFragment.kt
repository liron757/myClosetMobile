package com.example.mycloset.fragments.postlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycloset.adapters.PostRecyclerAdapter
import com.example.mycloset.api.controllers.PostController
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.LoadingState
import com.example.mycloset.databinding.FragmentPostListBinding
import com.example.mycloset.viewmodels.PostListFragmentViewModel
import com.example.mycloset.viewmodels.UserViewModel

class PostListFragment : Fragment() {
    var binding: FragmentPostListBinding? = null
    lateinit var adapter: PostRecyclerAdapter
    lateinit var viewModel: PostListFragmentViewModel
    lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[PostListFragmentViewModel::class.java]

        binding = FragmentPostListBinding.inflate(inflater, container, false)
        val view: View = binding!!.root

        PostController.instance.eventPostsListLoadingState.observe(
            viewLifecycleOwner
        ) { status: LoadingState ->
            binding!!.swipeRefresh.isRefreshing =
                status == LoadingState.LOADING
        }

        binding!!.swipeRefresh.setOnRefreshListener {
            reloadData()
        }

        binding!!.rvSearchResult.setHasFixedSize(true)
        binding!!.rvSearchResult.layoutManager = LinearLayoutManager(context)
        userViewModel.user?.observe(
            viewLifecycleOwner
        ) { user: User? ->
            if (user != null) {
                viewModel.data.observe(
                    viewLifecycleOwner
                ) { list: List<Post> ->
                    adapter = PostRecyclerAdapter(
                        user,
                        layoutInflater, list, context
                    )
                    binding!!.rvSearchResult.adapter = adapter

                    adapter.onImageClickListener =
                        (object : PostRecyclerAdapter.OnItemClickListener {
                            override fun onItemClick(pos: Int) {
                                val post = list[pos]
                                val action =
                                    PostListFragmentDirections.actionPostListFragmentToPostFragment(
                                        post.id
                                    )
                                findNavController(view).navigate(action)
                            }
                        })

                    adapter.onUsernameClickListener =
                        (object : PostRecyclerAdapter.OnItemClickListener {
                            override fun onItemClick(pos: Int) {
                                val post = list[pos]
                                val action =
                                    PostListFragmentDirections.actionPostListFragmentToUserProfileFragment(
                                        post.userName
                                    )
                                findNavController(view).navigate(action)
                            }
                        })
                }
            }
        }

        binding!!.progressBar.visibility = View.GONE

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    fun reloadData() {
        PostController.instance.refreshAllPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}