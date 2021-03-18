package com.benrostudios.airtree.ar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.benrostudios.airtree.R
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch


class MainArFragment : Fragment(R.layout.fragment_ar) {

    private var deadTree: ModelRenderable? = null
    private var liveTree: ModelRenderable? = null
    lateinit var simuFragmentMain: ArFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        simuFragmentMain = childFragmentManager.findFragmentById(R.id.arView) as ArFragment

        loadModel()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ar, container, false)
    }


    private fun initTapListener() {
        simuFragmentMain.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchorNode = AnchorNode(
                hitResult.createAnchor()
            )

            anchorNode.setParent(simuFragmentMain.arSceneView.scene)
            val yodaNode = Node()
            yodaNode.renderable = deadTree
            yodaNode.setParent(anchorNode)
        }
    }

    private fun loadModel() {
        lifecycleScope.launch {
            deadTree = ModelRenderable
                .builder()
                .setSource(
                    requireContext(),
                    R.raw.deadtree
                )
                .build()
                .await()
            liveTree =
                ModelRenderable.builder().setSource(requireContext(), R.raw.houseplant).build()
                    .await()
            Toast.makeText(
                requireContext(),
                "Your Plants have been loaded!",
                Toast.LENGTH_SHORT
            ).show()
            initTapListener()
        }
    }


}