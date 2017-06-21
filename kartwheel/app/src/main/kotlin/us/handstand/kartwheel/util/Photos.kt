package us.handstand.kartwheel.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.support.v4.content.FileProvider
import com.crashlytics.android.Crashlytics
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.model.Storage
import java.io.File
import java.io.IOException

object Photos {
    val REQUEST_IMAGE_CAPTURE = 1;
    val requestPermissions = FLAG_GRANT_WRITE_URI_PERMISSION or FLAG_GRANT_READ_URI_PERMISSION

    private var currentPhotoPath: String? = null

    /**
     * Start Camera app for selfie. When the user takes a photo, use the returned File to retrieve it.
     */
    fun takeSelfie(activity: Activity): File? {
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
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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

    fun getSelfieBitmap(activity: Activity): Bitmap {
        val photoUri = Uri.parse(Storage.selfieUri)
        activity.revokeUriPermission(photoUri, Photos.requestPermissions)
        return getBitmapFromPath(currentPhotoPath!!)
    }

    fun getBitmapFromPath(path: String): Bitmap {
        return BitmapFactory.decodeFile(path)
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

