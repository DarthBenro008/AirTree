package com.benrostudios.airtree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import com.benrostudios.airtree.ar.MainArFragment
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, MainArFragment(), "AR")
                .addToBackStack("AR")
                .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}