package com.example.mycloset.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

            // TODO(LOGIN LOGIC)
            println(username)
            println(password)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.cancel()
    }
}