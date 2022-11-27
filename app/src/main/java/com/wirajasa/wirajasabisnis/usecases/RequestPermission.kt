package com.wirajasa.wirajasabisnis.usecases

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.wirajasa.wirajasabisnis.R
import com.wirajasa.wirajasabisnis.utility.Constant
import pub.devrel.easypermissions.EasyPermissions

class RequestPermission(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun accessMedia() {
        EasyPermissions.requestPermissions(
            context as Activity,
            context.getString(R.string.gallery_permission_title),
            Constant.READ_EXTERNAL,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    }

    fun accessExternal() {
        EasyPermissions.requestPermissions(
            context as Activity,
            context.getString(R.string.gallery_permission_title),
            Constant.READ_EXTERNAL,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}