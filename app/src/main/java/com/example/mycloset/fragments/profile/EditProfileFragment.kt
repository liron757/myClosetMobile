package com.example.mycloset.fragments.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.mycloset.R
import com.example.mycloset.adapters.SpinnerAdapter
import com.example.mycloset.databinding.FragmentEditProfileBinding
import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.firebase.FirebaseModel
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener
import com.example.mycloset.dialogs.LoadingDialog
import com.example.mycloset.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import java.util.UUID

class EditProfileFragment : Fragment() {
    lateinit var binding: FragmentEditProfileBinding
    lateinit var galleryLauncher: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    lateinit var userViewModel: UserViewModel
    lateinit var country: String

    lateinit var loadingDialog: LoadingDialog

    var isAvatarSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        loadingDialog = LoadingDialog(context ?: requireContext())

        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                binding.editProfileAvatar.setImageURI(uri)
                isAvatarSelected = true
            }
        }

        cameraLauncher = registerForActivityResult<Void?, Bitmap>(
            ActivityResultContracts.TakePicturePreview()
        ) { result ->
            if (result != null) {
                binding.editProfileAvatar.setImageBitmap(result)
                isAvatarSelected = true
            }
        }
    }

    fun updateProfile(newUser: User, view: View) {
        UserController.instance.updateUser(
            newUser, object : Listener<Void?> {
                override fun onComplete(data: Void?) {
                    findNavController(view).popBackStack()
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.editProfileGalleryBtn.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        binding.editProfileCameraBtn.setOnClickListener {
            cameraLauncher.launch(null)
        }

        userViewModel.user?.observe(viewLifecycleOwner) { user: User ->
            if (user.avatar.isNotEmpty()) Picasso.get().load(user.avatar)
                .into(binding.editProfileAvatar)
            else {
                binding.editProfileAvatar.setImageResource(R.drawable.profile_pic)
            }

            binding.editProfileSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        pos: Int,
                        id: Long
                    ) {
                        user.country = parent.getItemAtPosition(pos).toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            context?.let {
                loadingDialog.show()
                SpinnerAdapter.setCountrySpinner(
                    it, object : Listener<ArrayAdapter<String>> {
                        override fun onComplete(data: ArrayAdapter<String>) {
                            binding.editProfileSpinner.adapter = data
                            val pos = data.getPosition(user.country)
                            binding.editProfileSpinner.setSelection(pos)
                            loadingDialog.cancel()
                        }
                    }
                )
            }

            binding.editProfileConfirmBtn.setOnClickListener { view1: View ->
                if (user.country != "Country") {
                    val newUser = User(
                        user.userName,
                        user.avatar,
                        user.email,
                        user.country,
                        user.likedPosts
                    )

                    if (isAvatarSelected) {
                        val bitmap = (binding.editProfileAvatar.drawable as BitmapDrawable).bitmap
                        val id = UUID.randomUUID().toString()

                        FirebaseModel.instance.uploadImage(
                            id, bitmap,
                            object : Listener<String?> {
                                override fun onComplete(data: String?) {
                                    data?.let {
                                        newUser.avatar = it
                                        updateProfile(newUser, view1)
                                    }
                                }
                            }
                        )
                    } else {
                        updateProfile(newUser, view1)
                    }
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Bad Input")
                        .setMessage("Please select a valid country")
                        .setPositiveButton(
                            "Ok"
                        ) { _: DialogInterface?, _: Int -> }
                        .create().show()
                }
            }
        }

        return view
    }
}