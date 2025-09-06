package com.common.control.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.StrictMode
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object AppUtilsKt {
    private val TAG = AppUtilsKt::class.java.name

    var policyUrl: String? = null
    var subject: String? = null
    var email: String? = null

    fun shareApp(context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "https://play.google.com/store/apps/details?id=${context.packageName}"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share to"))
    }

    fun support(context: Context) {
        val mailIntent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:?SUBJECT=$subject&body=&to=$email")
        mailIntent.data = data
        context.startActivity(Intent.createChooser(mailIntent, "Send mail..."))
    }

    fun rateApp(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${context.packageName}")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                )
            )
        }
    }

    fun showPolicy(context: Context) {
        policyUrl?.let { openWeb(context, it) }
    }

    fun openWeb(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun log(text: String) {
        Log.d(TAG, text)
    }

    fun saveFile(fin: InputStream, savePath: String, nameFile: String) {
        val file = File(savePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        try {
            FileOutputStream(File(savePath, nameFile)).use { fout ->
                fin.use { input ->
                    val buff = ByteArray(1024)
                    var length = input.read(buff)
                    while (length > 0) {
                        fout.write(buff, 0, length)
                        length = input.read(buff)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmOverloads
    fun formatTime(duration: Long, isHour: Boolean = false): String {
        val formatter = if (isHour) {
            SimpleDateFormat("HH:mm:ss")
        } else {
            SimpleDateFormat("mm:ss")
        }
        val date = Date(duration)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }

    fun formatDate(duration: Long): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("dd.MM.yyyy")
        val date = Date(duration)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }

    fun shareFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            shareFile(context, uri)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun shareFile(context: Context, uri: Uri) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>())// Pass empty array for no recipients
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getFileName(context, uri))
            emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            emailIntent.setDataAndType(uri, "*/*")
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(emailIntent)
        } catch (ex: Exception) {
            try {
                val intent = ShareCompat.IntentBuilder(context as Activity)
                    .setType(context.contentResolver.getType(uri))
                    .setStream(uri)
                    .intent
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val createChooser = Intent.createChooser(intent, "Share File")
                createChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (createChooser.resolveActivity(context.packageManager) == null) {
                    return
                }
                context.startActivity(createChooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String {
        var result = ""
        try {
            if (uri.scheme == "content") {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
            if (result.isEmpty()) { // Check if empty instead of null, as it's initialized to ""
                result = uri.path ?: ""
                val cut = result.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
