package com.example.mycloset.fragments.postinfo

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.mycloset.R
import com.example.mycloset.adapters.SpinnerAdapter
import com.example.mycloset.api.controllers.PostController
import com.example.mycloset.api.firebase.FirebaseModel
import com.example.mycloset.databinding.FragmentEditPostBinding
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.consts.Listener
import com.example.mycloset.consts.outfitCategories
import com.example.mycloset.dialogs.LoadingDialog
import com.example.mycloset.viewmodels.ImageViewModel
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {
    lateinit var binding: FragmentEditPostBinding
    lateinit var viewModel: ImageViewModel
    lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    lateinit var galleryLauncher: ActivityResultLauncher<String>
    lateinit var outfitCategory: String
    lateinit var postId: String

    lateinit var loadingDialog: LoadingDialog

    var isImageSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(context ?: requireContext())

        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                viewModel.url = uri
                binding.outfitImgEditPost.setImageURI(viewModel.url)
                isImageSelected = true
            }
        }
        cameraLauncher = registerForActivityResult<Void?, Bitmap>(
            ActivityResultContracts.TakePicturePreview()
        ) { uri ->
            if (uri != null) {
                viewModel.bitmap = uri
                binding.outfitImgEditPost.setImageBitmap(viewModel.bitmap)
                isImageSelected = true
            }

        }
    }

    fun updatePost (post: Post, view: View) {
        PostController.instance.updatePost(
            post, object : Listener<Void?> {
                override fun onComplete(data: Void?) {
                    loadingDialog.cancel()
                    findNavController(
                        view
                    ).popBackStack(R.id.postFragment, false)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        val view: View = binding.root

        viewModel = ViewModelProvider(this)[ImageViewModel::class.java]
        if (viewModel.bitmap != null) binding.outfitImgEditPost.setImageBitmap(viewModel.bitmap)
        if (viewModel.url != null) binding.outfitImgEditPost.setImageURI(viewModel.url)

        val outfitCategorySpinner = binding.categorySpinnerEditPost
        outfitCategorySpinner.adapter = context?.let {
            SpinnerAdapter.setOutfitCategoriesSpinner(
                it
            )
        }
        outfitCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    outfitCategory = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.cameraBtnEditPost.setOnClickListener { cameraLauncher.launch(null) }

        binding.editImgBtnEditPost.setOnClickListener { galleryLauncher.launch("image/*") }

        postId = EditPostFragmentArgs.fromBundle(requireArguments()).postId
        PostController.instance.getPostById(postId).observe(
            viewLifecycleOwner
        ) { post: Post ->
            Picasso.get().load(Uri.parse(post.outfitUrl)).into(binding.outfitImgEditPost)

            binding.outfitNameEditPost.setText(post.outfitName)
            binding.outfitDescriptionEditPost.setText(post.outfitDescription)
            binding.outfitPiecesEditPost.setText(post.outfitPieces)
            binding.categorySpinnerEditPost.setSelection(outfitCategories.indexOf(post.outfitCategory))

            binding.confirmBtnEditPost.setOnClickListener { view1: View ->
                loadingDialog.show()

                post.outfitName = binding.outfitNameEditPost.text.toString()
                post.outfitDescription = binding.outfitDescriptionEditPost.text.toString()
                post.outfitPieces = binding.outfitPiecesEditPost.text.toString()
                post.outfitCategory = outfitCategory

                if (isImageSelected) {
                    val bitmap = (binding.outfitImgEditPost.drawable as BitmapDrawable).bitmap
                    FirebaseModel.instance.uploadImage(
                        postId, bitmap,
                        object : Listener<String?> {
                            override fun onComplete(data: String?) {
                                data?.let {
                                    post.outfitUrl = data
                                    updatePost(post, view1)
                                }
                            }
                        }
                    )
                } else {
                    updatePost(post, view1)
                }
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.cancel()
    }
}