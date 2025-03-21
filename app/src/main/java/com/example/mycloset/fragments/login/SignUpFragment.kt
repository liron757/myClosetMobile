package com.example.mycloset.fragments.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import com.example.mycloset.activities.MainActivity
import com.example.mycloset.adapters.SpinnerAdapter
import com.example.mycloset.api.controllers.AuthController
import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.firebase.FirebaseModel
import com.example.mycloset.api.models.user.User
import com.example.mycloset.consts.Listener
import com.example.mycloset.databinding.FragmentSignUpBinding
import com.example.mycloset.dialogs.LoadingDialog
import java.util.UUID
import java.util.regex.Pattern

class SignUpFragment : Fragment() {
    private lateinit var loadingDialog: LoadingDialog
    lateinit var binding: FragmentSignUpBinding
    lateinit var galleryLauncher: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    lateinit var username: String
    lateinit var email: String
    lateinit var password: String
    lateinit var country: String

    var isAvatarSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog(context ?: requireContext())

        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                binding.signupAvatar.setImageURI(uri)
                isAvatarSelected = true
            }
        }

        cameraLauncher = registerForActivityResult<Void?, Bitmap>(
            ActivityResultContracts.TakePicturePreview()
        ) { result ->
            if (result != null) {
                binding.signupAvatar.setImageBitmap(result)
                isAvatarSelected = true
            }
        }
    }

    fun makeAToast(text: String?) {
        AlertDialog.Builder(context)
            .setTitle("Invalid Input")
            .setMessage(text)
            .setPositiveButton(
                "Ok"
            ) { _: DialogInterface?, _: Int -> }
            .create().show()
        loadingDialog.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.signupCountrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    country = parent.getItemAtPosition(pos).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        context?.let {
            SpinnerAdapter.setCountrySpinner(
                it, object : Listener<ArrayAdapter<String>> {
                    override fun onComplete(data: ArrayAdapter<String>) {
                        binding.signupCountrySpinner.adapter = data
                    }
                }
            )
        }

        fun isValidEmail(email: String): Boolean {
            val regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"

            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(email)

            return matcher.matches()
        }

        fun validateInput(): Boolean {
            if (username.trim() == "") {
                makeAToast("Please enter an username")
                return false
            } else if (!isValidEmail(email)) {
                makeAToast("Please enter a valid email")
                return false
            } else if (password.length < 6) {
                makeAToast("Password must be at least 6 characters long")
                return false
            } else if (country == "Country" || country == null) {
                makeAToast("Please choose a Country")
                return false
            }

            return true
        }

        binding.signupSubmitBtn.setOnClickListener {

            username = binding.signupUsername.text.toString()
            email = binding.signupEmail.text.toString()
            password = binding.signupPassword.text.toString()

            if (validateInput()) {
                loadingDialog.show()

                UserController.instance.isUsernameTaken(
                    username, object : Listener<Boolean> {
                        override fun onComplete(data: Boolean) {
                            if (data) {
                                makeAToast("Username is already taken")
                            } else {
                                UserController.instance.isEmailTaken(
                                    email, object : Listener<Boolean> {
                                        override fun onComplete(data: Boolean) {
                                            if (data) {
                                                makeAToast("Email is already taken")
                                            } else {
                                                val newUser = User(
                                                    username,
                                                    "",
                                                    email,
                                                    country,
                                                    ArrayList()
                                                )

                                                if (isAvatarSelected) {
                                                    val bitmap =
                                                        (binding.signupAvatar.drawable as BitmapDrawable).bitmap
                                                    val id = UUID.randomUUID().toString()

                                                    FirebaseModel.instance.uploadImage(
                                                        id,
                                                        bitmap,
                                                        object : Listener<String?> {
                                                            override fun onComplete(data: String?) {
                                                                newUser.avatar = data ?: ""
                                                                signUp(newUser)
                                                            }
                                                        }
                                                    )
                                                } else {
                                                    signUp(newUser)
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }

        binding.signupEditAvatar.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.signupCameraBtn.setOnClickListener {
            cameraLauncher.launch(null)
        }

        return view
    }

    fun signUp (newUser: User) {
        AuthController.instance.signUp(
            newUser,
            password,
            object : Listener<Void?> {
                override fun onComplete(data: Void?) {
                    val intent = Intent(
                        activity,
                        MainActivity::class.java
                    )
                    startActivity(intent)
                    activity?.finish()
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.cancel()
    }
}