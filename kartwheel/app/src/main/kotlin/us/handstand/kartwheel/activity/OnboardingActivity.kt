package us.handstand.kartwheel.activity

import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.OnboardingController.Companion.ERROR
import us.handstand.kartwheel.controller.OnboardingController.Companion.NONE
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.controller.OnboardingController.Companion.STARTED
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.util.DateFormatter
import java.io.File
import java.io.IOException


class OnboardingActivity : AppCompatActivity(), View.OnClickListener, OnboardingController.Companion.OnboardingStepCompletionListener {
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var pageNumber: TextView
    private lateinit var imageContainer: View
    private lateinit var image: ImageView
    private lateinit var imageDescription: TextView
    private lateinit var emojiRecyclerView: RecyclerView
    private lateinit var button: Button
    private lateinit var makeItRainText: TextView

    private val controller = OnboardingController(this)
    private val REQUEST_IMAGE_CAPTURE = 1;
    private val requestPermissions = FLAG_GRANT_WRITE_URI_PERMISSION or FLAG_GRANT_READ_URI_PERMISSION
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        title = ViewUtil.findView(this, R.id.title)
        description = ViewUtil.findView(this, R.id.description)
        pageNumber = ViewUtil.findView(this, R.id.pageNumber)
        imageContainer = ViewUtil.findView(this, R.id.imageContainer)
        image = ViewUtil.findView(this, R.id.image)
        imageDescription = ViewUtil.findView(this, R.id.imageText)
        emojiRecyclerView = ViewUtil.findView(this, R.id.emojiRecyclerView)
        button = ViewUtil.findView(this, R.id.button)
        makeItRainText = ViewUtil.findView(this, R.id.makeItRainDescription)

        button.setOnClickListener(this)
        image.setOnClickListener(this)

        controller.transition(NONE, Storage.lastOnboardingState)
    }

    override fun showNextStep(previous: Long, next: Long) {
        // Make sure that we're starting with fresh data.
        if (next == ERROR) {
            KartWheel.logout()
        }
        var pageNumberVisibility = VISIBLE
        var emojiRecyclerViewVisibility = GONE
        var makeItRainVisibility = INVISIBLE
        var imageContainerVisibility = VISIBLE
        var imageTextVisibility = GONE
        when (next) {
            STARTED -> {
                emojiRecyclerViewVisibility = VISIBLE
                pageNumberVisibility = INVISIBLE
                imageContainerVisibility = GONE
            }
            SELFIE -> imageTextVisibility = VISIBLE
            POINT_SYSTEM -> makeItRainVisibility = VISIBLE
        }
        title.text = resources.getString(OnboardingController.getTitleStringResIdForStep(next))
        description.text = resources.getString(OnboardingController.getDescriptionStringResIdForStep(next))
        button.text = resources.getString(OnboardingController.getButtonStringResIdForStep(next))
        pageNumber.text = next.toString() + " of 5"
        pageNumber.visibility = pageNumberVisibility
        emojiRecyclerView.visibility = emojiRecyclerViewVisibility
        makeItRainText.visibility = makeItRainVisibility
        val imageResId = OnboardingController.getImageResIdForStep(next)
        if (imageResId == -1) {
            imageContainer.visibility = INVISIBLE
        } else {
            imageContainer.visibility = VISIBLE
            image.setImageResource(imageResId)
        }
        imageDescription.visibility = imageTextVisibility

        Storage.lastOnboardingState = next
    }

    override fun onClick(v: View) {
        // TODO: Enable/Disable button based on state of selfie/buddy upload
        when (v.id) {
            R.id.button -> if (v.isEnabled) controller.onStepCompleted(Storage.lastOnboardingState)
            R.id.image -> if (Storage.lastOnboardingState == SELFIE) takePicture()
        }
    }

    override fun showDialog(message: String) {
        Toast.makeText(this, message, LENGTH_LONG).show()
    }

    override fun onOnboardingFragmentStateChanged() {
        // TODO: Do I need this, since we're not using fragments?
    }

    fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        val resolvedComponent = takePictureIntent.resolveActivity(packageManager)
        if (resolvedComponent != null) {
            // Create the File where the photo should go
            try {
                val photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", createTempImageFile())
                val resolvedActivities = packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolveInfo in resolvedActivities) {
                    grantUriPermission(resolveInfo.activityInfo.packageName, photoURI, requestPermissions)
                    Storage.selfieUri = photoURI.toString()
                    break;
                }
                takePictureIntent.putExtra(EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (e: Exception) {
                Log.e("Onboarding", e.message)
                showDialog(e.message!!)
            }
        } else {
            showDialog("Unable to access your camera!")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val uri = Storage.selfieUri
            revokeUriPermission(Uri.parse(uri), requestPermissions)
            image.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath))
        }
    }

    @Throws(IOException::class)
    private fun createTempImageFile(): File {
        val image = File.createTempFile(
                "JPEG_" + DateFormatter[System.currentTimeMillis()].toString().replace(" ", "_") + "_",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        currentPhotoPath = image.absolutePath
        return image
    }
}
