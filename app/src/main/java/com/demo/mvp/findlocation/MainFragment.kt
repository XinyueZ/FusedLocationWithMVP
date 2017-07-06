package com.demo.mvp.findlocation

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.demo.mvp.databinding.ContentMainBinding
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

private const val PRQ_FINE_LOCATION = 0x0000002

class MainFragment : Fragment(), FindLocationContract.Viewer,
        EasyPermissions.PermissionCallbacks {
    private var binding: ContentMainBinding? = null
    private var presenter: FindLocationContract.Presenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ContentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.release()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun setPresenter(presenter: FindLocationContract.Presenter) {
        this.presenter = presenter
    }

    override fun showCurrentLocation(location: Location?) {
        if (location != null) {
            binding?.locationTv?.text = "Current location -> $location.latitude, $location.longitude"
        } else {
            binding?.locationTv?.text = "No location was found ."
        }

        val view = view
        if (view != null && location != null) {
            Snackbar.make(view, "Refresh location after second(s).", Snackbar.LENGTH_SHORT)
                    .show()
        }

        presenter?.release()
    }

    @AfterPermissionGranted(PRQ_FINE_LOCATION)
    override fun getCurrentLocation() {
        if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            presenter?.findLocation()
        } else {
            EasyPermissions.requestPermissions(this, "This demo needs location permission.", PRQ_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    override fun canNotShowSettingDialog() {
        //For demo, I ignore here.
        Toast.makeText(context, "Can not show setting dialog.", Toast.LENGTH_LONG)
                .show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        when (requestCode) {
            PRQ_FINE_LOCATION -> {
                presenter?.findLocation()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        when (requestCode) {
            PRQ_FINE_LOCATION -> {
                //permission is not allowed.
            }
        }
    }

}