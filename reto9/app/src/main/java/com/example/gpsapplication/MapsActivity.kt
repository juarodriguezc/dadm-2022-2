package com.example.gpsapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText

import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.example.gpsapplication.PermissionUtils.isPermissionGranted


import com.example.gpsapplication.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory


import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.GoogleMap.OnPoiClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest


class MapsActivity : AppCompatActivity(),
    OnMyLocationButtonClickListener,
    OnMyLocationClickListener,
    OnMapReadyCallback,
    OnPoiClickListener,
    OnRequestPermissionsResultCallback {


    private var permissionDenied = false

    private lateinit var mMap: GoogleMap

    private lateinit var mLocationClient: FusedLocationProviderClient

    private lateinit var zoomButton: Button

    private var zoomDef = 18;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        mLocationClient = FusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        zoomButton = findViewById<View>(R.id.zoom_button) as Button

        zoomButton.setOnClickListener {
            showdialog()
        }


    }

    private fun showPOI(poi: PointOfInterest) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(poi.name + "\nid: "+poi.placeId)
        builder.setMessage("\n\nLAT: "+poi.latLng.latitude + "\nLON: "+poi.latLng.longitude)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->

        }


        builder.show()
    }

    private fun showdialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Title")
        builder.setMessage("Insert zoom value ( 1 - 30 ), 0 - reset zoom")

        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.hint = "1 - 30"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            var mText = input.text.toString()
            if (mText == "")
                mText = "0"
            zoomDef = mText.toInt()
            if (zoomDef < 1 || zoomDef > 25) {
                zoomDef = 18
            }
            getCurrLocation()


        })
        builder.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //googleMap.setOnMyLocationButtonClickListener(this)
        //googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()

        getCurrLocation()

        mMap.setOnPoiClickListener(this)


    }

    override fun onPoiClick(p0: PointOfInterest) {
        if (p0 != null) {
            showPOI(p0)

        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrLocation() {

        if (!permissionDenied) {
            mLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    goToLocation(it.getResult().latitude, it.getResult().longitude)

                }
            }
        }
    }

    private fun goToLocation(latitude: Double, longitude: Double) {
        var latLng = LatLng(latitude, longitude)
        var cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomDef.toFloat());
        mMap.moveCamera(cameraUpdate)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            getCurrLocation()
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}