package eka.dr.intl.common

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import kotlin.system.exitProcess

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun isContactPermissionGranted(): Boolean {
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        return rc == PackageManager.PERMISSION_GRANTED
    }

    fun requestContactPermission() {
        val permissions = arrayOf(Manifest.permission.READ_CONTACTS)

        ActivityCompat.requestPermissions(
            this,
            permissions,
            BaseActivity.RC_HANDLE_READ_CONTACT_PERM
        )

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {

        if (requestCode == RC_HANDLE_READ_CONTACT_PERM) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactAccessGranted()
            } else {
                val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)
                if (!showRationale) {
                    showDialog()
                } else {
                    contactAccessDenied()
                }
            }

        } else if (requestCode == REQUEST_CODE_PERMISSIONS) {
            cameraAccessDenied();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    open fun contactAccessGranted() {}

    open fun contactAccessDenied() {}
    open fun cameraAccessDenied() {}

    private fun showDialog() {
        val title = getString(R.string.open_app_setting)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.contact_permission_title))
            .setCancelable(false)
            .setMessage(getString(R.string.contact_permission_desc))
            .setPositiveButton(title) { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(
                    "package",
                    packageName, null
                )
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Not now") { dialogInterface, _ ->
                dialogInterface.dismiss()
                contactAccessDenied()
            }
            .show()
    }

    fun showExitDialog() {
        val title = getString(R.string.rooted_device)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setCancelable(false)
            .setMessage(title)
            .setPositiveButton(getString(R.string.ok_got_it)) { _, _ ->
                exitProcess(0)
            }
            .show()
    }

    companion object {
        const val RC_HANDLE_READ_CONTACT_PERM = 1
        const val REQUEST_CODE_PERMISSIONS = 10
    }
}
