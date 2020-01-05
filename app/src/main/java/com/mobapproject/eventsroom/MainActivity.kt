package com.mobapproject.eventsroom

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity(), MultiplePermissionsListener {
    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

        if (!report!!.areAllPermissionsGranted()) {
            Toast.makeText(
                this,
                "STORAGE AND LOCATION MUST BE GRANTED TO ACCESS EVENTS AROUND YOUR LOCATION AND UPLOAD PICTURES!",
                Toast.LENGTH_LONG
            ).show()

        }
    }

    override fun onPermissionRationaleShouldBeShown(
        permissions: MutableList<PermissionRequest>?,
        token: PermissionToken?
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.action_bar_color)))

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(this)
            .check()
    }
}
