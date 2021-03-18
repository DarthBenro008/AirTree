package com.benrostudios.airtree.ar

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.benrostudios.airtree.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.node_card_view_layout.view.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch


class MainArFragment : Fragment(R.layout.fragment_ar) {

    private var deadTree: ModelRenderable? = null
    private var liveTree: ModelRenderable? = null
    private var helloArText: ViewRenderable? = null
    lateinit var simuFragmentMain: ArFragment
    private var locationManager: LocationManager? = null
    private var textLongitude: String = ""
    private var fusedLocationClient: FusedLocationProviderClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        simuFragmentMain = childFragmentManager.findFragmentById(R.id.arView) as ArFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
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

            val textAnchorNode = AnchorNode(
                hitResult.trackable.createAnchor(
                    hitResult.hitPose.compose(
                        Pose.makeTranslation(
                            0.0f,
                            1f,
                            0.0f
                        )
                    )
                )
            )
            textAnchorNode.setParent(simuFragmentMain.arSceneView.scene)
            anchorNode.setParent(simuFragmentMain.arSceneView.scene)
            val node: TransformableNode = TransformableNode(simuFragmentMain.transformationSystem)
            node.scaleController.maxScale = 0.5f;
            node.scaleController.minScale = 0.4f;
            node.renderable = liveTree
            node.setParent(anchorNode)

            val textNode = Node()
            textAnchorNode.renderable = helloArText
            textNode.setParent(textAnchorNode);

            helloArText?.view?.textView?.text = textLongitude;


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
            helloArText =
                ViewRenderable.builder().setView(requireContext(), R.layout.node_card_view_layout)
                    .build().await()
//            Toast.makeText(
//                requireContext(),
//                "Your Plants have been loaded!",
//                Toast.LENGTH_SHORT
//            ).show()
            initTapListener()
        }
    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationClient?.lastLocation!!.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                Toast.makeText(requireContext(), "${task.result.latitude}", Toast.LENGTH_LONG)
                    .show()
            } else {
                Log.w("myTag", "getLastLocation:exception", task.exception)
            }
        }
    }

}