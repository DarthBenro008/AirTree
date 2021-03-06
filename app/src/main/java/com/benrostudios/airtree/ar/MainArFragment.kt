package com.benrostudios.airtree.ar

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.benrostudios.airtree.R
import com.benrostudios.airtree.Secrets.API_KEY
import com.benrostudios.airtree.models.WeatherModel
import com.benrostudios.airtree.network.ServiceBuilder
import com.benrostudios.airtree.network.WeatherApi
import com.google.android.gms.location.*
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.node_card_view_layout.view.*
import kotlinx.android.synthetic.main.node_sideboard_layout.view.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainArFragment : Fragment(R.layout.fragment_ar) {

    private var deadTree: ModelRenderable? = null
    private var liveTree: ModelRenderable? = null
    private var moderateTree: ModelRenderable? = null
    private var aqiDashboard: ViewRenderable? = null
    private var carbonSideboard: ViewRenderable? = null
    private var emissionSideboard: ViewRenderable? = null
    lateinit var simuFragmentMain: ArFragment
    private var locationManager: LocationManager? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null


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


    private fun initTapListener(weatherModel: WeatherModel) {
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

            val emissionBoardAnchorNode = AnchorNode(
                hitResult.trackable.createAnchor(
                    hitResult.hitPose.compose(
                        Pose.makeTranslation(
                            0.8f,
                            1.0f,
                            0.0f
                        )
                    )
                )
            )
            val carbonBoardAnchorNode = AnchorNode(
                hitResult.trackable.createAnchor(
                    hitResult.hitPose.compose(
                        Pose.makeTranslation(
                            -0.8f,
                            1.0f,
                            0.0f
                        )
                    )
                )
            )
            textAnchorNode.setParent(simuFragmentMain.arSceneView.scene)
            carbonBoardAnchorNode.setParent(simuFragmentMain.arSceneView.scene)
            emissionBoardAnchorNode.setParent(simuFragmentMain.arSceneView.scene)
            anchorNode.setParent(simuFragmentMain.arSceneView.scene)
            val node: TransformableNode = TransformableNode(simuFragmentMain.transformationSystem)
            node.scaleController.maxScale = 0.5f;
            node.scaleController.minScale = 0.4f;
            val aqiIndex = weatherModel.list[0].main.aqi
            node.renderable = when {
                aqiIndex == 3.0 -> moderateTree
                aqiIndex > 3.0 -> deadTree
                else -> liveTree
            }
            node.setParent(anchorNode)
            val textNode = Node()
            val carbonNode = Node()
            val emissionNode = Node()
            textAnchorNode.renderable = aqiDashboard
            carbonBoardAnchorNode.renderable = carbonSideboard
            emissionBoardAnchorNode.renderable = emissionSideboard
            textNode.setParent(textAnchorNode)
            carbonNode.setParent(carbonBoardAnchorNode)
            emissionNode.setParent(emissionBoardAnchorNode)
            setCarbonSideboard(weatherModel)
            setEmissionSideboard(weatherModel)
            setDashboard(aqiIndex)
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
            moderateTree =
                ModelRenderable.builder()
                    .setSource(requireContext(), R.raw.fiddleleaffigpottedplant).build()
                    .await()
            aqiDashboard =
                ViewRenderable.builder().setView(requireContext(), R.layout.node_card_view_layout)
                    .build().await()
            carbonSideboard =
                ViewRenderable.builder().setView(requireContext(), R.layout.node_sideboard_layout)
                    .build().await()
            emissionSideboard =
                ViewRenderable.builder().setView(requireContext(), R.layout.node_sideboard_layout)
                    .build().await()

            Toast.makeText(
                requireContext(),
                "Your Plants have been loaded!",
                Toast.LENGTH_SHORT
            ).show()

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

//        fusedLocationClient?.lastLocation!!.addOnCompleteListener { task ->
//            if (task.isSuccessful && task.result != null) {
//                Toast.makeText(
//                    requireContext(),
//                    "${task.result.latitude} ${task.result.longitude}",
//                    Toast.LENGTH_LONG
//                )
//                    .show()
//                networkCall(task.result.latitude, task.result.longitude)
//            } else {
//                getLocationUpdates()
//                Log.w(
//                    "myTag",
//                    "getLastLocation:exception ${task.exception} ${task.result} ${task.result}",
//                    task.exception
//                )
//            }
//        }.addOnFailureListener {
//            Log.w("myTag", "lmaooooooo ${it.message} ${it.toString()}")
//        }
        fusedLocationClient?.lastLocation!!.addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                "${it.latitude} ${it.longitude}",
                Toast.LENGTH_LONG
            )
                .show()
            networkCall(it.latitude, it.longitude)
        }.addOnFailureListener {
            d("myTAG", "${it.message}")
        }
    }


    private fun networkCall(lat: Double, long: Double) {
        val request = ServiceBuilder.buildService(WeatherApi::class.java)
        val call = request.getWeatherData(lat, long, API_KEY)
        call.enqueue(object : Callback<WeatherModel> {
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                if (response.isSuccessful) {
                    d("myTAG", "Network call success")
                    val weatherModel = WeatherModel()

                    initTapListener(weatherModel)
                }
            }

            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                d("myTAG", "Network call failure due to ${t.message}")
                initTapListener(WeatherModel())
            }
        })
    }

    private fun setDashboard(aqiIndex: Double) {
        val dashboardView = aqiDashboard?.view
        dashboardView?.aqi_progress?.setProgress(5 - aqiIndex, 5.0);
        val aqiText = when (aqiIndex) {
            5.0 -> "Very Poor Quality Air"
            4.0 -> "Moderate Quality Air"
            3.0 -> "Fair Quality Air"
            2.0 -> "Good Air"
            1.0 -> "Healthy Air"
            else -> "Loading"
        }
        val plantHealth: String = when {
            aqiIndex >= 3 -> "Your plant isn't feeling well!"
            else -> "Your plant is feeling good!"
        }
        dashboardView?.plantHealth?.text = plantHealth
        dashboardView?.aqi_text?.text = aqiText
    }

    @SuppressLint("SetTextI18n")
    private fun setCarbonSideboard(weatherModel: WeatherModel) {
        val carbonSideboardView = carbonSideboard?.view
        carbonSideboardView?.header_sideboard?.text = "Carbon Emission"
        carbonSideboardView?.sideboard_title_one?.text = "CO2:"
        carbonSideboardView?.sideboard_title_two?.text = "O3:"
        carbonSideboardView?.sideboard_value_one?.text =
            "${weatherModel.list[0].components.co} ??g/m3"
        carbonSideboardView?.sideboard_value_two?.text =
            "${weatherModel.list[0].components.o3} ??g/m3"
    }

    @SuppressLint("SetTextI18n")
    private fun setEmissionSideboard(weatherModel: WeatherModel) {
        val emissionSideboardView = emissionSideboard?.view
        emissionSideboardView?.header_sideboard?.text = "Particulate Matter"
        emissionSideboardView?.sideboard_title_one?.text = "Fine Particulate"
        emissionSideboardView?.sideboard_title_two?.text = "Coarse Particulate"
        emissionSideboardView?.sideboard_value_one?.text =
            "${weatherModel.list[0].components.pm25} ??g/m3"
        emissionSideboardView?.sideboard_value_two?.text =
            "${weatherModel.list[0].components.pm10} ??g/m3"
    }


}