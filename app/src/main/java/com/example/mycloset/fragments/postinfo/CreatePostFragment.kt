package com.example.mycloset.fragments.postinfo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.example.mycloset.dialogs.LoadingDialog
import com.example.mycloset.adapters.SpinnerAdapter
import com.example.mycloset.api.controllers.PostController
import com.example.mycloset.api.firebase.FirebaseModel
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener
import com.example.mycloset.databinding.FragmentCreatePostBinding
import com.example.mycloset.viewmodels.UserViewModel
import java.util.UUID

class CreatePostFragment : Fragment() {
    private lateinit var loadingDialog: LoadingDialog

    lateinit var binding: FragmentCreatePostBinding
    lateinit var user: User
    lateinit var outfitCategory: String

    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var userViewModel: UserViewModel
    private lateinit var outfitName: String
    private lateinit var outfitDescription: String
    private lateinit var outfitPieces: String

    private var isOutfitImageSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                binding.outfitImgCreatePost.setImageURI(uri)
                isOutfitImageSelected = true
            }
        }
        
        cameraLauncher = registerForActivityResult<Void?, Bitmap>(
            ActivityResultContracts.TakePicturePreview()
        ) { result ->
            if (result != null) {
                binding.outfitImgCreatePost.setImageBitmap(result)
                isOutfitImageSelected = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        val view: View = binding.root

        userViewModel.user?.observe(viewLifecycleOwner) { newUser -> user = newUser }

        val outfitCategorySpinner = binding.categorySpinnerCreatePost
        outfitCategorySpinner.adapter = context?.let {
            SpinnerAdapter.setOutfitCategoriesSpinner(
                it
            )
        }

        outfitCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    outfitCategory = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.confirmBtnCreatePost.setOnClickListener { view1: View ->
            loadingDialog.show()

            outfitName = binding.outfitNameCreatePost.text.toString()
            outfitDescription = binding.outfitDescriptionCreatePost.text.toString()
            outfitPieces = binding.outfitPiecesCreatePost.text.toString()

            if (validateInput()) {
                val bitmap = (binding.outfitImgCreatePost.drawable as BitmapDrawable).bitmap
                val id = UUID.randomUUID().toString()
                val post = Post(
                    id,
                    user.userName,
                    outfitCategory,
                    outfitName,
                    outfitDescription,
                    outfitPieces,
                    ""
                )

                FirebaseModel.instance.uploadImage(
                    id, bitmap, object : Listener<String?> {
                        override fun onComplete(data: String?) {
                            if (data != null) {
                                post.outfitUrl = data
                            }
                            PostController.instance.addPost(
                                post, object : Listener<Void?> {
                                    override fun onComplete(data: Void?) {
                                        findNavController(view1).popBackStack()
                                        findNavController(view).navigate(R.id.postListFragment)
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }

        binding.cameraBtnCreatePost.setOnClickListener {
            cameraLauncher.launch(null)
        }

        binding.editImgBtnCreatePost.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        loadingDialog.cancel()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    private fun validateInput(): Boolean {
        if (!isOutfitImageSelected) {
            makeAToast("Please choose a picture for your outfit")
            return false
        } else if (outfitName.trim() == "") {
            makeAToast("Please write a Name for your outfit")
            return false
        } else if (outfitDescription.trim() == "") {
            makeAToast("Please write a description for your outfit")
            return false
        } else if (outfitPieces.trim() == "") {
            makeAToast("Please write a pieces for your outfit")
            return false
        } else if (outfitCategory.trim() == "Category") {
            makeAToast("Please choose outfit category")
            return false
        }
        return true
    }

    private fun makeAToast(text: String?) {
        AlertDialog.Builder(context)
            .setTitle("Invalid Input")
            .setMessage(text)
            .setPositiveButton(
                "Ok"
            ) { _: DialogInterface?, _: Int -> }
            .create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.cancel()
    }
}