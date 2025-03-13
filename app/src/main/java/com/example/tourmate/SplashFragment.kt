package com.example.tourmate

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.tourmate.managers.SharedPreferencesManager

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isFirstTime = SharedPreferencesManager.getValue("isFirstTime", true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstTime){
                findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }
        },2000)
    }
}