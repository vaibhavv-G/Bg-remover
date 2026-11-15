package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BackgroundRemovalRepository
import com.example.data.BackgroundRemovalResult
import com.example.data.ImageLoadResult
import com.example.data.SelectedImageInfo
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BackdropMode {
  CHECKERBOARD,
  LIGHT,
  DARK,
}

enum class ResultViewMode {
  COMPARE,
  RESULT,
  ORIGINAL,
}

sealed interface BatchItemStatus {
  data object Queued : BatchItemStatus
  data object Processing : BatchItemStatus
  data class Done(
    val processedBitmap: Bitmap,
    val pngBytes: ByteArray,
  ) : BatchItemStatus {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Done) return false
      return pngBytes.contentEquals(other.pngBytes)
    }

    override fun hashCode(): Int = pngBytes.contentHashCode()
  }

  data class Failed(val message: String) : BatchItemStatus
}

data class BatchItem(
  val id: String = UUID.randomUUID().toString(),
  val imageInfo: SelectedImageInfo,
  val originalBitmap: Bitmap,
  val status: BatchItemStatus = BatchItemStatus.Queued,
)

sealed interface StudioUiState {
  data object Idle : StudioUiState

  data class ReadyToProcess(
    val imageInfo: SelectedImageInfo,
    val originalBitmap: Bitmap,
  ) : StudioUiState

  data class Processing(
    val imageInfo: SelectedImageInfo,
    val originalBitmap: Bitmap,
  ) : StudioUiState

  data class ResultReady(
    val imageInfo: SelectedImageInfo,
    val originalBitmap: Bitmap,
    val processedBitmap: Bitmap,
    val processedPngBytes: ByteArray,
    val backdropMode: BackdropMode = BackdropMode.CHECKERBOARD,
    val viewMode: ResultViewMode = ResultViewMode.COMPARE,
  ) : StudioUiState {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is ResultReady) return false
      return imageInfo == other.imageInfo &&
        backdropMode == other.backdropMode &&
        viewMode == other.viewMode &&
        processedPngBytes.contentEquals(other.processedPngBytes)
    }

    override fun hashCode(): Int {
      var result = imageInfo.hashCode()
      result = 31 * result + backdropMode.hashCode()
      result = 31 * result + viewMode.hashCode()
      result = 31 * result + processedPngBytes.contentHashCode()
      return result
    }
  }

  data class BatchMode(
    val items: List<BatchItem>,
    val isProcessing: Boolean = false,
  ) : StudioUiState

  data class Error(
    val imageInfo: SelectedImageInfo?,
    val originalBitmap: Bitmap?,
    val message: String,
    val isNotConfigured: Boolean = false,
  ) : StudioUiState
}

class BackgroundRemoverViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = BackgroundRemovalRepository(application.applicationContext)

  private val _uiState = MutableStateFlow<StudioUiState>(StudioUiState.Idle)
  val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

  private val _isSaving = MutableStateFlow(false)
  val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

  private val _feedbackMessage = MutableStateFlow<String?>(null)
  val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

  private var pendingSafBytes: ByteArray? = null

  val isServiceConfigured: Boolean
    get() = repository.isServiceConfigured()

  fun createCameraCaptureUri(): Uri = repository.createCameraCaptureUri()

  fun onImagePicked(uri: Uri) {
    viewModelScope.launch {
      _feedbackMessage.value = null
      when (val result = repository.loadAndValidateImage(uri)) {
        is ImageLoadResult.Success -> {
          _uiState.value = StudioUiState.ReadyToProcess(
            imageInfo = result.info,
            originalBitmap = result.bitmap,
          )
        }
        is ImageLoadResult.Invalid -> {
          _uiState.value = StudioUiState.Error(
            imageInfo = null,
            originalBitmap = null,
            message = result.message,
            isNotConfigured = false,
          )
        }
      }
    }
  }

  fun onBatchImagesPicked(uris: List<Uri>) {
    if (uris.isEmpty()) return
    if (uris.size == 1) {
      onImagePicked(uris.first())
      return
    }
    viewModelScope.launch {
      _feedbackMessage.value = null
      val validItems = mutableListOf<BatchItem>()
      var skippedCount = 0
      for (uri in uris.take(5)) {
        when (val res = repository.loadAndValidateImage(uri)) {
          is ImageLoadResult.Success -> {
            validItems += BatchItem(
              imageInfo = res.info,
              originalBitmap = res.bitmap,
            )
          }
          is ImageLoadResult.Invalid -> {
            skippedCount++
          }
        }
      }
      if (validItems.isEmpty()) {
        _uiState.value = StudioUiState.Error(
          imageInfo = null,
          originalBitmap = null,
          message = "None of the selected files were valid JPG, PNG, or WebP images under 12 MB.",
        )
      } else {
        _uiState.value = StudioUiState.BatchMode(items = validItems, isProcessing = false)
        if (skippedCount > 0) {
          _feedbackMessage.value = "$skippedCount file(s) skipped due to unsupported format or size > 12 MB."
        }
      }
    }
  }

  fun removeBackground() {
    val current = _uiState.value
    val (imageInfo, originalBitmap) = when (current) {
      is StudioUiState.ReadyToProcess -> current.imageInfo to current.originalBitmap
      is StudioUiState.Error -> {
        if (current.imageInfo != null && current.originalBitmap != null) {
          current.imageInfo to current.originalBitmap
        } else {
          return
        }
      }
      else -> return
    }

    if (!repository.isServiceConfigured()) {
      _uiState.value = StudioUiState.Error(
        imageInfo = imageInfo,
        originalBitmap = originalBitmap,
        message = BackgroundRemovalRepository.NOT_CONFIGURED_MESSAGE,
        isNotConfigured = true,
      )
      return
    }

    viewModelScope.launch {
      _uiState.value = StudioUiState.Processing(imageInfo = imageInfo, originalBitmap = originalBitmap)
      when (val apiResult = repository.removeBackground(imageInfo)) {
        is BackgroundRemovalResult.Success -> {
          _uiState.value = StudioUiState.ResultReady(
            imageInfo = imageInfo,
            originalBitmap = originalBitmap,
            processedBitmap = apiResult.bitmap,
            processedPngBytes = apiResult.pngBytes,
          )
        }
        is BackgroundRemovalResult.NotConfigured -> {
          _uiState.value = StudioUiState.Error(
            imageInfo = imageInfo,
            originalBitmap = originalBitmap,
            message = apiResult.message,
            isNotConfigured = true,
          )
        }
        is BackgroundRemovalResult.Failure -> {
          _uiState.value = StudioUiState.Error(
            imageInfo = imageInfo,
            originalBitmap = originalBitmap,
            message = apiResult.message,
            isNotConfigured = false,
          )
        }
      }
    }
  }

  fun processBatch() {
    val current = _uiState.value as? StudioUiState.BatchMode ?: return
    if (current.isProcessing) return

    if (!repository.isServiceConfigured()) {
      val updated = current.items.map { item ->
        if (item.status is BatchItemStatus.Done) {
          item
        } else {
          item.copy(status = BatchItemStatus.Failed(BackgroundRemovalRepository.NOT_CONFIGURED_MESSAGE))
        }
      }
      _uiState.value = current.copy(items = updated, isProcessing = false)
      _feedbackMessage.value = BackgroundRemovalRepository.NOT_CONFIGURED_MESSAGE
      return
    }

    viewModelScope.launch {
      var workingList = current.items
      _uiState.value = StudioUiState.BatchMode(items = workingList, isProcessing = true)

      for (index in workingList.indices) {
        val item = workingList[index]
        if (item.status is BatchItemStatus.Done) continue

        workingList = workingList.toMutableList().also {
          it[index] = item.copy(status = BatchItemStatus.Processing)
        }
        _uiState.value = StudioUiState.BatchMode(items = workingList, isProcessing = true)

        val nextStatus = when (val res = repository.removeBackground(item.imageInfo)) {
          is BackgroundRemovalResult.Success -> BatchItemStatus.Done(res.bitmap, res.pngBytes)
          is BackgroundRemovalResult.NotConfigured -> BatchItemStatus.Failed(res.message)
          is BackgroundRemovalResult.Failure -> BatchItemStatus.Failed(res.message)
        }

        workingList = workingList.toMutableList().also {
          it[index] = item.copy(status = nextStatus)
        }
        _uiState.value = StudioUiState.BatchMode(items = workingList, isProcessing = true)
      }

      _uiState.value = StudioUiState.BatchMode(items = workingList, isProcessing = false)
    }
  }

  fun setBackdropMode(mode: BackdropMode) {
    val current = _uiState.value as? StudioUiState.ResultReady ?: return
    _uiState.value = current.copy(backdropMode = mode)
  }

  fun setResultViewMode(mode: ResultViewMode) {
    val current = _uiState.value as? StudioUiState.ResultReady ?: return
    _uiState.value = current.copy(viewMode = mode)
  }

  fun downloadTransparentPng(onLaunchLegacySaf: (suggestedFileName: String) -> Unit) {
    val current = _uiState.value as? StudioUiState.ResultReady ?: return
    savePngBytes(
      pngBytes = current.processedPngBytes,
      originalFileName = current.imageInfo.fileName,
      onLaunchLegacySaf = onLaunchLegacySaf,
    )
  }

  fun downloadBatchItemPng(
    itemId: String,
    onLaunchLegacySaf: (suggestedFileName: String) -> Unit,
  ) {
    val current = _uiState.value as? StudioUiState.BatchMode ?: return
    val item = current.items.firstOrNull { it.id == itemId } ?: return
    val done = item.status as? BatchItemStatus.Done ?: return
    savePngBytes(
      pngBytes = done.pngBytes,
      originalFileName = item.imageInfo.fileName,
      onLaunchLegacySaf = onLaunchLegacySaf,
    )
  }

  fun saveAllBatchPngs() {
    val current = _uiState.value as? StudioUiState.BatchMode ?: return
    if (_isSaving.value) return
    val completed = current.items.mapNotNull { item ->
      val done = item.status as? BatchItemStatus.Done ?: return@mapNotNull null
      item.imageInfo.fileName to done.pngBytes
    }
    if (completed.isEmpty()) return

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      _feedbackMessage.value = "Use individual Save buttons on Android 9 and below."
      return
    }

    viewModelScope.launch {
      _isSaving.value = true
      var savedCount = 0
      for ((name, bytes) in completed) {
        if (repository.saveTransparentPngToMediaStore(bytes, name).isSuccess) {
          savedCount++
        }
      }
      _isSaving.value = false
      _feedbackMessage.value = if (savedCount > 0) {
        "Saved $savedCount transparent PNG(s) to Pictures/BGRemover Pro."
      } else {
        "Unable to save transparent PNGs."
      }
    }
  }

  private fun savePngBytes(
    pngBytes: ByteArray,
    originalFileName: String,
    onLaunchLegacySaf: (suggestedFileName: String) -> Unit,
  ) {
    if (_isSaving.value) return

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      pendingSafBytes = pngBytes
      onLaunchLegacySaf(repository.buildExportFileName(originalFileName))
      return
    }

    viewModelScope.launch {
      _isSaving.value = true
      val result = repository.saveTransparentPngToMediaStore(
        pngBytes = pngBytes,
        originalFileName = originalFileName,
      )
      _isSaving.value = false
      _feedbackMessage.value = result.fold(
        onSuccess = { message -> message },
        onFailure = { error -> "Download failed: ${error.localizedMessage ?: "Storage error"}" },
      )
    }
  }

  fun saveToSafUri(destinationUri: Uri) {
    val bytes = pendingSafBytes ?: (_uiState.value as? StudioUiState.ResultReady)?.processedPngBytes ?: return
    pendingSafBytes = null
    viewModelScope.launch {
      _isSaving.value = true
      val result = repository.writeTransparentPngToUri(
        pngBytes = bytes,
        destinationUri = destinationUri,
      )
      _isSaving.value = false
      _feedbackMessage.value = result.fold(
        onSuccess = { "Transparent PNG saved successfully." },
        onFailure = { error -> "Save failed: ${error.localizedMessage ?: "Unable to write file"}" },
      )
    }
  }

  fun shareTransparentPng(onShareReady: (Uri) -> Unit) {
    val current = _uiState.value as? StudioUiState.ResultReady ?: return
    viewModelScope.launch {
      val result = repository.createShareablePngUri(
        pngBytes = current.processedPngBytes,
        originalFileName = current.imageInfo.fileName,
      )
      result.fold(
        onSuccess = { uri -> onShareReady(uri) },
        onFailure = { error ->
          _feedbackMessage.value = "Share failed: ${error.localizedMessage ?: "Unable to prepare PNG"}"
        },
      )
    }
  }

  fun resetTool() {
    pendingSafBytes = null
    _feedbackMessage.value = null
    _uiState.value = StudioUiState.Idle
  }

  fun clearFeedbackMessage() {
    _feedbackMessage.value = null
  }
}
