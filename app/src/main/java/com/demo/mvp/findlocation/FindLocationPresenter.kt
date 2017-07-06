package com.demo.mvp.findlocation

import android.annotation.SuppressLint
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import io.nlopez.smartlocation.SmartLocation

/**
 * Should be handled in one @{link Activity} of this app.
 */
const val REQUEST_CHECK_SETTINGS = 0x0000009

class FindLocationPresenter(private val view: FindLocationContract.Viewer, private val localService: SmartLocation = SmartLocation.with(view.getContext())) : FindLocationContract.Presenter {
    init {
        view.setPresenter(this)
    }

    @SuppressLint("MissingPermission")
    override fun findLocation() {
        requestLocation()
        if (localService.location().state().locationServicesEnabled()) {
            requestLocation()
        } else
            LocationServices.getSettingsClient(view.getActivity())
                    .checkLocationSettings(LocationSettingsRequest.Builder().setAlwaysShow(true).setNeedBle(true).addLocationRequest(LocationRequest.create()).build())
                    .addOnFailureListener({
                        val exp = it as ApiException
                        when (exp.statusCode) {
                            CommonStatusCodes.RESOLUTION_REQUIRED -> {
                                val resolvable = exp as ResolvableApiException
                                resolvable.startResolutionForResult(view.getActivity(),
                                        REQUEST_CHECK_SETTINGS)
                            }
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                view.canNotShowSettingDialog()
                            }
                        }
                    })
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        localService.location().start({ view.showCurrentLocation(it) })
    }

    override fun release() {
        localService.location().stop()
    }
}
