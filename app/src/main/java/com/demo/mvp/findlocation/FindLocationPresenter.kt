package com.demo.mvp.findlocation

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

/**
 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
 */
private const val UPDATE_INTERVAL: Long = 30000 // Every 30 seconds.

/**
 * The fastest rate for active location updates. Updates will never be more frequent
 * than this value, but they may be less frequent.
 */
private const val FASTEST_UPDATE_INTERVAL: Long = 30000 / 2 // Every 15 seconds

/**
 * The max time before batched results are delivered by location services. Results may be
 * delivered sooner than this interval.
 */
private const val MAX_WAIT_TIME = UPDATE_INTERVAL * 3 // Every 3 minutes.

/**
 * Should be handled in one @{link Activity} of this app.
 */
const val REQUEST_CHECK_SETTINGS = 0x0000009

class FindLocationPresenter(
    private val view: FindLocationContract.Viewer,
    @Suppress("CanBeParameter") private val mainPresenter: MainPresenter,
    private val localClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        view.getViewContext()
    ),
    private val localReq: LocationRequest = LocationRequest.create(),
    private val localCallback: LocationCallback = FindCallback(view)
) : FindLocationContract.Presenter {
    init {
        view.setPresenter(this)
        localReq.interval = UPDATE_INTERVAL
        localReq.fastestInterval = FASTEST_UPDATE_INTERVAL
        localReq.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        localReq.maxWaitTime = MAX_WAIT_TIME
    }

    @SuppressLint("MissingPermission")
    override fun findLocation() {
        localClient.lastLocation.addOnSuccessListener { view.showCurrentLocation(it) }
        requestLocation()
        LocationServices.getSettingsClient(view.getViewActivity())
            .checkLocationSettings(
                LocationSettingsRequest.Builder().setAlwaysShow(true).setNeedBle(
                    true
                ).addLocationRequest(localReq).build()
            )
            .addOnFailureListener {
                val exp = it as ApiException
                when (exp.statusCode) {
                    CommonStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvable = exp as ResolvableApiException
                        resolvable.startResolutionForResult(
                            view.getViewActivity(),
                            REQUEST_CHECK_SETTINGS
                        )
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        view.canNotShowSettingDialog()
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        localClient.requestLocationUpdates(localReq, localCallback, Looper.myLooper())
    }

    override fun release() {
        localClient.flushLocations()
        localClient.removeLocationUpdates(localCallback)
    }
}

private class FindCallback(private val view: FindLocationContract.Viewer) : LocationCallback() {
    override fun onLocationResult(p0: LocationResult?) {
        super.onLocationResult(p0)
        view.showCurrentLocation(p0?.let { p0.lastLocation })
        Log.d("FindLocationPresenter", "onLocationResult $p0")
    }

    override fun onLocationAvailability(p0: LocationAvailability?) {
        super.onLocationAvailability(p0)
        Log.d("FindLocationPresenter", "onLocationAvailability $p0")
    }
}