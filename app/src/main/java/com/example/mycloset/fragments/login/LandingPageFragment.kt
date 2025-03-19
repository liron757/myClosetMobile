package com.example.mycloset.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.createNavigateOnClickListener
import com.example.mycloset.R
import com.example.mycloset.databinding.FragmentLandingPageBinding

class LandingPageFragment : Fragment() {
    lateinit var binding: FragmentLandingPageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        val view: View = binding.root
        binding.btnLogin.setOnClickListener(
            createNavigateOnClickListener(R.id.action_landingPageFragment_to_loginFragment)
        )
        binding.btnRegister.setOnClickListener(
            createNavigateOnClickListener(R.id.action_landingPageFragment_to_signUpFragment)
        )
        return view
    }
}