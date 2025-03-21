package com.example.mycloset.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mycloset.activities.MainActivity
import com.example.mycloset.api.controllers.AuthController
import com.example.mycloset.consts.Listener
import com.example.mycloset.databinding.FragmentLoginBinding
import com.example.mycloset.dialogs.LoadingDialog

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view: View = binding.root

        loadingDialog = LoadingDialog(context ?: requireContext())

        binding.loginBtn.setOnClickListener {
            loadingDialog.show()
            val username = binding.loginUsername.text.toString()
            val password = binding.loginPassword.text.toString()

            AuthController.instance.logIn(
                username, password, object : Listener<Boolean> {
                    override fun onComplete(data: Boolean) {
                        loadingDialog.cancel()
                        if (data) {
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(
                                context, "Username or password are incorrect!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.cancel()
    }
}