package us.handstand.kartwheel.util

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import com.crashlytics.android.Crashlytics
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.model.Storage
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

object Photos {
    val REQUEST_IMAGE_CAPTURE = 1;
    val requestPermissions = FLAG_GRANT_WRITE_URI_PERMISSION or FLAG_GRANT_READ_URI_PERMISSION
    val executor = Executors.newSingleThreadExecutor()

    private var currentPhotoPath: String? = null

    /**
     * Start Camera app for selfie. When the user takes a photo, use the returned File to retrieve it.
     */
    fun takeSelfie(fragment: Fragment): File? {
        val activity = fragment.activity
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        val resolvedComponent = takePictureIntent.resolveActivity(activity.packageManager)
        if (resolvedComponent != null) {
            // Create a File for the camera app to store the selfie
            try {
                val tempImageFile = createTempImageFile(activity)
                val photoURI = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", tempImageFile)
                val resolvedActivities = activity.packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
                // Find one resolved activity and grant it permissions to access our file
                for (resolveInfo in resolvedActivities) {
                    activity.grantUriPermission(resolveInfo.activityInfo.packageName, photoURI, requestPermissions)
                    Storage.selfieUri = photoURI.toString()
                    break
                }
                takePictureIntent.putExtra(EXTRA_OUTPUT, photoURI)
                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                currentPhotoPath = tempImageFile.absolutePath
                return tempImageFile
            } catch (e: Exception) {
                Crashlytics.logException(e)
                return null
            }
        } else {
            return null
        }
    }

    @Throws(IOException::class)
    private fun createTempImageFile(context: Context): File {
        val image = File.createTempFile(
                "${System.currentTimeMillis()}-${Storage.userId}-user-profile-photo",
                ".jpeg",
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        return image
    }
}

