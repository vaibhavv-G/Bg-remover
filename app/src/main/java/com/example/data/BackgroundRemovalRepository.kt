package com.example.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.example.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

data class SelectedImageInfo(
  val uri: Uri,
  val fileName: String,
  val mimeType: String,
  val formatLabel: String,
  val sizeBytes: Long,
  val width: Int,
  val height: Int,
  val rawBytes: ByteArray,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SelectedImageInfo) return false
    return uri == other.uri && fileName == other.fileName && sizeBytes == other.sizeBytes
  }

  override fun hashCode(): Int {
    var result = uri.hashCode()
    result = 31 * result + fileName.hashCode()
    result = 31 * result + sizeBytes.hashCode()
    return result
  }
}

sealed interface ImageLoadResult {
  data class Success(val info: SelectedImageInfo, val bitmap: Bitmap) : ImageLoadResult
  data class Invalid(val message: String) : ImageLoadResult
}

sealed interface BackgroundRemovalResult {
  data class Success(val pngBytes: ByteArray, val bitmap: Bitmap) : BackgroundRemovalResult
  data class NotConfigured(val message: String) : BackgroundRemovalResult
  data class Failure(val message: String) : BackgroundRemovalResult
}

class BackgroundRemovalRepository(
  private val context: Context,
) {
  companion object {
    const val DEFAULT_API_URL = "https://api.remove.bg/v1.0/removebg"
    const val MAX_IMAGE_BYTES = 12L * 1024L * 1024L // 12 MB
    const val NOT_CONFIGURED_MESSAGE =
      "Background removal service is not configured yet. Please configure your API credentials."

    private val SUPPORTED_MIME_TYPES = setOf(
      "image/jpeg",
      "image/jpg",
      "image/png",
      "image/webp",
    )

    private val SUPPORTED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
  }

  private val httpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

  fun isServiceConfigured(): Boolean {
    val apiKey = BuildConfig.BACKGROUND_REMOVAL_API_KEY.trim()
    return apiKey.isNotEmpty() &&
      apiKey != "YOUR_BACKGROUND_REMOVAL_API_KEY" &&
      apiKey != "MY_BACKGROUND_REMOVAL_API_KEY"
  }

  private fun resolvedApiUrl(): String {
    val configuredUrl = BuildConfig.BACKGROUND_REMOVAL_API_URL.trim()
    return configuredUrl.ifEmpty { DEFAULT_API_URL }
  }

  suspend fun loadAndValidateImage(uri: Uri): ImageLoadResult = withContext(Dispatchers.IO) {
    try {
      val contentResolver = context.contentResolver
      val rawMime = contentResolver.getType(uri)?.lowercase()
      val fileName = resolveFileName(uri)
      val ext = fileName.substringAfterLast('.', "").lowercase()

      val effectiveMime = when {
        rawMime != null && rawMime in SUPPORTED_MIME_TYPES -> {
          if (rawMime == "image/jpg") "image/jpeg" else rawMime
        }
        ext in SUPPORTED_EXTENSIONS -> when (ext) {
          "png" -> "image/png"
          "webp" -> "image/webp"
          else -> "image/jpeg"
        }
        else -> {
          val fallbackExt = MimeTypeMap.getSingleton().getExtensionFromMimeType(rawMime)?.lowercase()
          if (fallbackExt in SUPPORTED_EXTENSIONS) {
            when (fallbackExt) {
              "png" -> "image/png"
              "webp" -> "image/webp"
              else -> "image/jpeg"
            }
          } else {
            return@withContext ImageLoadResult.Invalid(
              "Unsupported file format. Please choose a JPG, JPEG, PNG, or WebP image."
            )
          }
        }
      }

      val bytes = contentResolver.openInputStream(uri)?.use { input ->
        val buffer = ByteArrayOutputStream()
        val chunk = ByteArray(16 * 1024)
        var total = 0L
        while (true) {
          val read = input.read(chunk)
          if (read <= 0) break
          total += read
          if (total > MAX_IMAGE_BYTES) {
            return@withContext ImageLoadResult.Invalid(
              "Image exceeds the 12 MB size limit. Please select a smaller image."
            )
          }
          buffer.write(chunk, 0, read)
        }
        buffer.toByteArray()
      } ?: return@withContext ImageLoadResult.Invalid("Unable to read the selected image file.")

      if (bytes.isEmpty()) {
        return@withContext ImageLoadResult.Invalid("The selected image file is empty.")
      }

      val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size, boundsOptions)
      if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) {
        return@withContext ImageLoadResult.Invalid(
          "Could not decode this file as a valid JPG, PNG, or WebP image."
        )
      }

      val previewBitmap = decodeBoundedBitmap(bytes, maxDimension = 2048)
        ?: return@withContext ImageLoadResult.Invalid("Failed to decode image preview.")

      val formatLabel = when (effectiveMime) {
        "image/png" -> "PNG"
        "image/webp" -> "WEBP"
        else -> if (ext == "jpeg") "JPEG" else "JPG"
      }

      ImageLoadResult.Success(
        info = SelectedImageInfo(
          uri = uri,
          fileName = fileName,
          mimeType = effectiveMime,
          formatLabel = formatLabel,
          sizeBytes = bytes.size.toLong(),
          width = boundsOptions.outWidth,
          height = boundsOptions.outHeight,
          rawBytes = bytes,
        ),
        bitmap = previewBitmap,
      )
    } catch (e: SecurityException) {
      ImageLoadResult.Invalid("Permission denied when reading the selected image.")
    } catch (e: IOException) {
      ImageLoadResult.Invalid("Could not read image: ${e.localizedMessage ?: "I/O error"}")
    }
  }

  suspend fun removeBackground(imageInfo: SelectedImageInfo): BackgroundRemovalResult =
    withContext(Dispatchers.IO) {
      if (!isServiceConfigured()) {
        return@withContext BackgroundRemovalResult.NotConfigured(NOT_CONFIGURED_MESSAGE)
      }

      val apiKey = BuildConfig.BACKGROUND_REMOVAL_API_KEY.trim()
      val apiUrl = resolvedApiUrl()

      val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
          "image_file",
          imageInfo.fileName,
          imageInfo.rawBytes.toRequestBody(imageInfo.mimeType.toMediaTypeOrNull()),
        )
        .addFormDataPart("size", "auto")
        .addFormDataPart("format", "png")
        .build()

      val request = Request.Builder()
        .url(apiUrl)
        .addHeader("X-Api-Key", apiKey)
        .addHeader("Accept", "image/png, application/json")
        .post(requestBody)
        .build()

      try {
        httpClient.newCall(request).execute().use { response ->
          val bodyBytes = response.body?.bytes() ?: ByteArray(0)
          val contentType = response.header("Content-Type")?.lowercase().orEmpty()

          if (!response.isSuccessful) {
            val apiDetail = parseErrorDetail(bodyBytes)
            val message = when (response.code) {
              400 -> apiDetail ?: "The image could not be processed. Try another JPG, PNG, or WebP photo with a clear subject."
              402 -> "API credits exhausted. Please check your background removal account balance."
              403 -> "Invalid or unauthorized API credentials. Please verify BACKGROUND_REMOVAL_API_KEY."
              429 -> "Rate limit reached. Please wait a few moments and try again."
              in 500..599 -> "The background removal server is temporarily unavailable. Please try again shortly."
              else -> apiDetail ?: "Background removal failed (HTTP ${response.code})."
            }
            return@withContext BackgroundRemovalResult.Failure(message)
          }

          if (bodyBytes.isEmpty()) {
            return@withContext BackgroundRemovalResult.Failure(
              "The server returned an empty image response."
            )
          }

          val isPngSignature = bodyBytes.size >= 8 &&
            bodyBytes[0] == 0x89.toByte() &&
            bodyBytes[1] == 0x50.toByte() &&
            bodyBytes[2] == 0x4E.toByte() &&
            bodyBytes[3] == 0x47.toByte()

          if (!contentType.contains("image/png") && !isPngSignature) {
            val detail = parseErrorDetail(bodyBytes)
            return@withContext BackgroundRemovalResult.Failure(
              detail ?: "Unexpected response format from the background removal API."
            )
          }

          val decodedBitmap = BitmapFactory.decodeByteArray(
            bodyBytes,
            0,
            bodyBytes.size,
            BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 },
          ) ?: return@withContext BackgroundRemovalResult.Failure(
            "Unable to decode the transparent PNG returned by the API."
          )

          BackgroundRemovalResult.Success(
            pngBytes = bodyBytes,
            bitmap = decodedBitmap,
          )
        }
      } catch (e: IOException) {
        BackgroundRemovalResult.Failure(
          "Network error while contacting the background removal service. Check your internet connection and try again."
        )
      } catch (e: IllegalArgumentException) {
        BackgroundRemovalResult.Failure(
          "Invalid background removal endpoint URL configuration."
        )
      }
    }

  suspend fun saveTransparentPngToMediaStore(
    pngBytes: ByteArray,
    originalFileName: String,
  ): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
      val exportName = buildExportFileName(originalFileName)
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        throw UnsupportedOperationException("Use SAF CreateDocument below Android 10")
      }

      val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, exportName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(
          MediaStore.Images.Media.RELATIVE_PATH,
          "${Environment.DIRECTORY_PICTURES}/BGRemover Pro",
        )
        put(MediaStore.Images.Media.IS_PENDING, 1)
      }

      val resolver = context.contentResolver
      val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
      val itemUri = resolver.insert(collection, values)
        ?: throw IOException("Unable to create MediaStore entry for PNG download.")

      try {
        resolver.openOutputStream(itemUri)?.use { out ->
          out.write(pngBytes)
          out.flush()
        } ?: throw IOException("Unable to open output stream for PNG download.")

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(itemUri, values, null, null)
        "Saved to Pictures/BGRemover Pro/$exportName"
      } catch (e: Exception) {
        resolver.delete(itemUri, null, null)
        throw e
      }
    }
  }

  suspend fun writeTransparentPngToUri(
    pngBytes: ByteArray,
    destinationUri: Uri,
  ): Result<Unit> = withContext(Dispatchers.IO) {
    runCatching {
      context.contentResolver.openOutputStream(destinationUri)?.use { out ->
        out.write(pngBytes)
        out.flush()
      } ?: throw IOException("Could not open destination file for writing.")
    }
  }

  suspend fun createShareablePngUri(
    pngBytes: ByteArray,
    originalFileName: String,
  ): Result<Uri> = withContext(Dispatchers.IO) {
    runCatching {
      val dir = File(context.cacheDir, "cutouts").apply {
        if (!exists()) mkdirs()
      }
      // Clean previous temporary share files so nothing is stored long-term
      dir.listFiles()?.forEach { it.delete() }
      val outFile = File(dir, buildExportFileName(originalFileName))
      outFile.writeBytes(pngBytes)
      FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outFile)
    }
  }

  fun createCameraCaptureUri(): Uri {
    val dir = File(context.cacheDir, "camera").apply {
      if (!exists()) mkdirs()
    }
    dir.listFiles()?.forEach { it.delete() }
    val photoFile = File(dir, "capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
  }

  fun buildExportFileName(originalFileName: String): String {
    val base = originalFileName.substringBeforeLast('.')
      .replace(Regex("[^A-Za-z0-9_-]"), "_")
      .trim('_')
      .ifEmpty { "image" }
    return "${base}_no_bg_${System.currentTimeMillis() % 100000}.png"
  }

  private fun resolveFileName(uri: Uri): String {
    if (uri.scheme == "content") {
      context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null,
      )?.use { cursor ->
        if (cursor.moveToFirst()) {
          val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (index >= 0) {
            val name = cursor.getString(index)
            if (!name.isNullOrBlank()) return name
          }
        }
      }
    }
    return uri.lastPathSegment?.substringAfterLast('/')?.ifBlank { "upload.png" } ?: "upload.png"
  }

  private fun decodeBoundedBitmap(bytes: ByteArray, maxDimension: Int): Bitmap? {
    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
    var sampleSize = 1
    while (opts.outWidth / sampleSize > maxDimension || opts.outHeight / sampleSize > maxDimension) {
      sampleSize *= 2
    }
    val decodeOpts = BitmapFactory.Options().apply {
      inSampleSize = sampleSize
      inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts)
  }

  private fun parseErrorDetail(bodyBytes: ByteArray): String? {
    if (bodyBytes.isEmpty()) return null
    return try {
      val json = JSONObject(bodyBytes.toString(Charsets.UTF_8))
      val errors = json.optJSONArray("errors")
      if (errors != null && errors.length() > 0) {
        errors.getJSONObject(0).optString("title").takeIf { it.isNotBlank() }
      } else {
        json.optString("message").takeIf { it.isNotBlank() }
      }
    } catch (_: Exception) {
      null
    }
  }
}
