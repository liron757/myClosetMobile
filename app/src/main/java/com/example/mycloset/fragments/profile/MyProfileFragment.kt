package com.example.mycloset.fragments.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.createNavigateOnClickListener
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycloset.R
import com.example.mycloset.adapters.UserRecyclerAdapter
import com.example.mycloset.databinding.FragmentMyProfileBinding
import com.example.mycloset.activities.LoginActivity
import com.example.mycloset.api.controllers.AuthController
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.example.mycloset.viewmodels.MyProfileViewModel
import com.example.mycloset.viewmodels.UserViewModel
import com.squareup.picasso.Picasso

class MyProfileFragment : Fragment() {
    lateinit var binding: FragmentMyProfileBinding
    lateinit var adapter: UserRecyclerAdapter
    lateinit var myProfileViewModel: MyProfileViewModel
    lateinit var userViewModel: UserViewModel
    lateinit var currentView: View
    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        currentView = binding.root
        binding.rvSearchResult.setHasFixedSize(true)
        binding.rvSearchResult.layoutManager = LinearLayoutManager(context)

        userViewModel.user?.observe(
            viewLifecycleOwner
        ) { newUser: User? ->
            if (newUser != null) {
                user = newUser
                setProfile()
            }
        }

        return currentView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myProfileViewModel = ViewModelProvider(this)[MyProfileViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    fun setProfile() {
        binding.tvUsername.text = user.userName
        binding.tvEmail.text = user.email
        binding.tvCountry.text = user.country

        if (user.avatar != "") {
            Picasso.get().load(user.avatar).into(binding.ivAvatar)
        } else {
            binding.ivAvatar.setImageResource(R.drawable.profile_pic)
        }

        myProfileViewModel.getData(user.userName)?.observe(
            viewLifecycleOwner
        ) { posts: List<Post> ->
            adapter = UserRecyclerAdapter(
                posts,
                layoutInflater
            )
            binding.rvSearchResult.adapter = adapter
            adapter.setItemClickListener(object : UserRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapterPosition: Int) {
                    val action =
                        MyProfileFragmentDirections.actionMyProfileFragmentToPostFragment(
                            posts[adapterPosition].id
                        )
                    findNavController(currentView).navigate(action)
                }
            })
        }

        binding.editBtnMyProfile.setOnClickListener(
            createNavigateOnClickListener(
                R.id.action_myProfileFragment_to_editProfileFragment
            )
        )

        binding.logOutBtnMyProfile.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("yes") { _: DialogInterface?, _: Int ->
                    AuthController.instance.logOut()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }.setNegativeButton(
                    "No"
                ) { _: DialogInterface?, _: Int -> }
                .create().show()
        }
    }
}