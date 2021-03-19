package com.benrostudios.airtree

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.fragment.app.FragmentTransaction
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage


class StarterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)
        createOnBoarding()
    }


    private fun createOnBoarding(){
        val scr1 = PaperOnboardingPage(
            "Welcome to ARTree",
            "An AR Tree that teaches you about your environment",
            Color.parseColor("#FFFFFF"),
            R.drawable.arwow,
            R.drawable.ic_baseline_keyboard_arrow_right_24
        )
        val scr2 = PaperOnboardingPage(
            "Get to know your surrounding",
            "Get to know about your air quality around you and grow a plant! Visualise in a fun way",
            Color.parseColor("#65B0B4"),
            R.drawable.saly,
            R.drawable.ic_baseline_keyboard_arrow_right_24
        )
        val scr3 = PaperOnboardingPage(
            "Save the Planet!",
            "Make use of this app to grow real plants and save the planet!" +
                    "Swipe right to start saving the planet!",
            Color.parseColor("#4A8DF2"),
            R.drawable.planet,
            R.drawable.ic_baseline_keyboard_arrow_right_24
        )

        val elements: ArrayList<PaperOnboardingPage> = ArrayList()
        elements.add(scr1)
        elements.add(scr2)
        elements.add(scr3)
        val onBoardingFragment = PaperOnboardingFragment.newInstance(elements)


        onBoardingFragment.setOnRightOutListener {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment)
        fragmentTransaction.commit()
    }
}