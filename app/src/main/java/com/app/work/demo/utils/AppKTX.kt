import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.app.work.demo.BuildConfig
import java.io.File
import java.io.IOException
import java.io.InputStream

fun logv(msg: String) {
    Log.v("MoviesApp-LOG", msg)
}

fun loge(msg: String) {
    Log.e("MoviesApp-LOG", msg)
}

fun readXMLinString(fileName: String, c: Context): String {
    return try {
        val `is`: InputStream = c.getAssets().open(fileName)
        val size: Int = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        String(buffer)
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

fun Context.createUri(videoUrl: String): Uri {
    val folder = this?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val fileName: String = videoUrl.substring(videoUrl.lastIndexOf("/") + 1)
    val file = File(folder, fileName)
    val uri = this?.let {
        FileProvider.getUriForFile(
            it,
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
    }
    val extension = MimeTypeMap.getFileExtensionFromUrl(uri?.path)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    return uri
}

fun Context.getVideoFileName(videoUrl: String): File {
    val folder = this?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val fileName: String = videoUrl.substring(videoUrl.lastIndexOf("/") + 1)
    val file = File(folder, fileName)
    return file
}


fun Context.checkFileExists(videoUrl: String): Boolean {
    val folder = this?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val fileName: String = videoUrl.substring(videoUrl.lastIndexOf("/") + 1)
    val file = File(folder, fileName)
    return file.exists()
}

fun Context.viewFile(file: File) {
    this.let { context ->

        val uri = this?.let {
            FileProvider.getUriForFile(
                it,
                "${BuildConfig.APPLICATION_ID}.provider",
                file
            )
        }

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooser = Intent.createChooser(intent, "Open with")

        if (intent.resolveActivity(context.packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(context, "No suitable application to open file", Toast.LENGTH_LONG)
                .show()
        }
    }
}
