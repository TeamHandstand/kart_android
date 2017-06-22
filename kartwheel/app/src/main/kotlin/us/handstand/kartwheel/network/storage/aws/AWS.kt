package us.handstand.kartwheel.network.storage.aws

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.network.storage.StorageProvider
import us.handstand.kartwheel.network.storage.TransferObserver
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object AWS : StorageProvider {
    // We only need one instance of the clients and credentials provider
    private var s3Client: AmazonS3Client? = null
    private var credProvider: CognitoCachingCredentialsProvider? = null
    private var transferUtility: TransferUtility? = null

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     * @param context An Context instance.
     *
     * @return A default credential provider.
     */
    private fun getCredProvider(context: Context): CognitoCachingCredentialsProvider {
        if (credProvider == null) {
            credProvider = CognitoCachingCredentialsProvider(
                    context.applicationContext,
                    BuildConfig.AWS_COGNITO_POOL_ID,
                    Regions.US_EAST_1)
        }
        return credProvider!!
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     * @param context An Context instance.
     *
     * @return A default S3 client.
     */
    private fun getS3Client(context: Context): AmazonS3Client {
        if (s3Client == null) {
            s3Client = AmazonS3Client(getCredProvider(context.applicationContext))
            s3Client!!.setRegion(Region.getRegion(Regions.US_EAST_1))
        }
        return s3Client!!
    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     * @param context
     *
     * @return a TransferUtility instance
     */
    private fun getTransferUtility(context: Context): TransferUtility {
        if (transferUtility == null) {
            transferUtility = TransferUtility(getS3Client(context.applicationContext), context.applicationContext)
        }
        return transferUtility!!
    }

    @Throws(IOException::class)
    fun copyContentUriToFile(context: Context, uri: Uri): File {
        val copiedFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), UUID.randomUUID().toString())
        if (copiedFile.createNewFile()) {
            context.contentResolver.openInputStream(uri).copyTo(FileOutputStream(copiedFile))
        }
        return copiedFile
    }

    /**
     * Converts number of bytes into proper scale.
     * @param bytes number of bytes to be converted.
     *
     * @return A string that represents the bytes in a proper scale.
     */
    fun getBytesString(bytes: Long): String {
        val quantifiers = arrayOf("KB", "MB", "GB", "TB")
        var speedNum = bytes.toDouble()
        var i = 0
        while (true) {
            if (i >= quantifiers.size) {
                return ""
            }
            speedNum /= 1024.0
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i]
            }
            i++
        }
    }

    override fun upload(photoUri: Uri, context: Context): TransferObserver {
        val photoFile = AWS.copyContentUriToFile(context, photoUri)
        return AWSTransferObserver(AWS.getTransferUtility(context).upload(BuildConfig.AWS_BUCKET_NAME + "/user-profile-pictures", photoFile.name + "-user-profile-picture.jpeg", photoFile))
    }

    override fun getTransferById(id: Int): TransferObserver? {
        val transferObserver = transferUtility?.getTransferById(id)
        return if (transferObserver == null) null else AWSTransferObserver(transferObserver)
    }
}

