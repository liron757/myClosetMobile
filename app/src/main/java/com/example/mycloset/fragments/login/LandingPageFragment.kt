package com.example.mycloset.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mycloset.databinding.FragmentLandingPageBinding

class LandingPageFragment : Fragment() {
    lateinit var binding: FragmentLandingPageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        val view: View = binding.root

        return view
    }
}