package com.example.mycloset.fragments.postlist

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
import com.example.mycloset.databinding.FragmentLikedPostsBinding
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener
import com.example.mycloset.viewmodels.LikedPostsViewModel
import com.example.mycloset.viewmodels.UserViewModel

class LikedPostsFragment : Fragment() {
    lateinit var binding: FragmentLikedPostsBinding
    lateinit var viewModel: LikedPostsViewModel
    lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedPostsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LikedPostsViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val view: View = binding.root

        binding.rvSearchResult.setHasFixedSize(true)
        binding.rvSearchResult.layoutManager = LinearLayoutManager(context)

        userViewModel.user?.observe(viewLifecycleOwner) { user: User ->
            val adapter = PostRecyclerAdapter(
                user,
                layoutInflater, viewModel.likedPosts, context
            )
            binding.rvSearchResult.adapter = adapter
            PostController.instance.getLikedPosts(
                user.likedPosts,
                object : Listener<List<Post>> {
                    override fun onComplete(data: List<Post>) {
                        adapter.onImageClickListener = (
                                object : PostRecyclerAdapter.OnItemClickListener {
                                    override fun onItemClick(pos: Int) {
                                        val post = data[pos]
                                        val action =
                                            LikedPostsFragmentDirections
                                                .actionLikedPostsFragmentToPostFragment(
                                                    post.id
                                                )
                                        findNavController(view).navigate(action)
                                    }
                                }
                                )
                        adapter.onUsernameClickListener =
                            (object : PostRecyclerAdapter.OnItemClickListener {
                                override fun onItemClick(pos: Int) {
                                    val post = data[pos]
                                    val action =
                                        LikedPostsFragmentDirections
                                            .actionLikedPostsFragmentToPostFragment(
                                                post.id
                                            )
                                    findNavController(view).navigate(action)
                                }
                            })
                        viewModel.likedPosts = data
                        adapter.updatePosts(data)
                    }
                }
            )
        }

        return view
    }
}