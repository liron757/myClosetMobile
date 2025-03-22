package com.example.mycloset.fragments.postinfo

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.createNavigateOnClickListener
import androidx.navigation.Navigation.findNavController
import com.example.mycloset.R
import com.example.mycloset.databinding.FragmentPostBinding
import com.example.mycloset.api.controllers.PostController
import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener
import com.example.mycloset.viewmodels.UserViewModel
import com.squareup.picasso.Picasso

class PostFragment : Fragment() {
    lateinit var postId: String
    lateinit var binding: FragmentPostBinding
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        val view: View = binding.root

        postId = PostFragmentArgs.fromBundle(requireArguments()).postId

        PostController.instance.getPostById(postId).observe(
            viewLifecycleOwner
        ) { post: Post ->
            binding.tvPostUsername.text = post.userName
            binding.tvPostOutfitName.text = post.outfitName
            if (post.outfitUrl.trim() != "") {
                Picasso.get().load(post.outfitUrl).into(binding.ivPostOutfit)
            }
            binding.tvPostDescription.text = post.outfitDescription
            binding.tvOutfitPieces.text = post.outfitPieces
            binding.tvOutfitCategory.text = post.outfitCategory
            userViewModel.user?.observe(
                viewLifecycleOwner
            ) { user: User? ->
                if (user != null) {
                    if (post.userName == user.userName) {
                        binding.btnPostLike.visibility = View.GONE
                        binding.btnEditPost.visibility = View.VISIBLE
                        binding.btnDeletePost.visibility = View.VISIBLE
                        binding.btnEditPost.setOnClickListener(
                            createNavigateOnClickListener(
                                PostFragmentDirections.actionPostFragmentToEditPostFragment(
                                    postId
                                )
                            )
                        )
                    } else {
                        binding.btnPostLike.visibility = View.VISIBLE
                        binding.btnEditPost.visibility = View.GONE
                        binding.btnDeletePost.visibility = View.GONE

                        val likeBtn = binding.btnPostLike
                        if (user.likedPosts.contains(post.id)) {
                            likeBtn.setImageResource(R.drawable.heart)
                        }

                        likeBtn.setOnClickListener {
                            val likedPostsList: MutableList<String> =
                                ArrayList(user.likedPosts)
                            if (!user.likedPosts.contains(post.id)) {
                                likedPostsList.add(post.id)
                                user.likedPosts = likedPostsList
                                UserController.instance
                                    .updateLikedPosts(user)
                                likeBtn.setImageResource(R.drawable.heart)
                            } else {
                                likedPostsList.remove(post.id)
                                user.likedPosts = likedPostsList
                                UserController.instance
                                    .updateLikedPosts(user)
                                likeBtn.setImageResource(R.drawable.heart_outline)
                            }
                        }
                    }
                    binding.btnDeletePost.setOnClickListener { view1: View ->
                        AlertDialog.Builder(
                            context
                        )
                            .setTitle("Are you sure you want to delete the post?")
                            .setMessage("Your post will be permanently deleted")
                            .setPositiveButton(
                                "Yes"
                            ) { _: DialogInterface?, _: Int ->
                                PostController.instance.deletePost(
                                    post, object : Listener<Void?> {
                                        override fun onComplete(data: Void?) {
                                            findNavController(
                                                view1
                                            ).popBackStack(R.id.myProfileFragment, false)
                                        }
                                    }
                                )
                            }.setNegativeButton(
                                "No"
                            ) { _: DialogInterface?, _: Int -> }
                            .create().show()
                    }
                }
            }
        }

        return view
    }
}