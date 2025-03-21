package com.example.mycloset.fragments.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycloset.R
import com.example.mycloset.adapters.UserRecyclerAdapter
import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.example.mycloset.databinding.FragmentUserProfileBinding
import com.example.mycloset.viewmodels.UserProfileViewModel
import com.squareup.picasso.Picasso

class UserProfileFragment : Fragment() {
    lateinit var binding: FragmentUserProfileBinding
    lateinit var adapter: UserRecyclerAdapter
    lateinit var viewModel: UserProfileViewModel
    lateinit var user: User
    lateinit var currentView: View
    lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        currentView = binding.root
        binding.rvSearchResult.setHasFixedSize(true)
        binding.rvSearchResult.layoutManager = LinearLayoutManager(context)

        username = UserProfileFragmentArgs.fromBundle(requireArguments()).username

        UserController.instance.getOtherUser(username).observe(
            viewLifecycleOwner
        ) { otherUser: User? ->
            if (otherUser != null) {
                this.user = otherUser
                setProfile()
            }
        }

        return currentView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(
            UserProfileViewModel::class.java
        )
    }

    fun setProfile() {
        binding.userNameUserProfile.text = user.userName
        binding.emailUserProfile.text = user.email
        binding.countryUserProfile.text = user.country

        if (user.avatar == "") {
            binding.avatarUserProfile.setImageResource(R.drawable.profile_pic)
        } else {
            Picasso.get().load(user.avatar).into(binding.avatarUserProfile)
        }

        viewModel.getUserPosts(user.userName)?.observe(
            viewLifecycleOwner
        ) { posts: List<Post> ->
            adapter = UserRecyclerAdapter(posts, layoutInflater)
            binding.rvSearchResult.adapter = adapter
            adapter.setItemClickListener(
                object : UserRecyclerAdapter.OnItemClickListener {
                    override fun onItemClick(adapterPosition: Int) {
                        val action =
                            UserProfileFragmentDirections.actionUserProfileFragmentToPostFragment(
                                posts[adapterPosition].id
                            )
                        findNavController(currentView).navigate(action)
                    }
                }
            )
        }
    }
}
