package com.example.ui.screens

import android.net.Uri
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.BackdropMode
import com.example.ui.BatchItem
import com.example.ui.BatchItemStatus
import com.example.ui.ResultViewMode
import com.example.ui.StudioUiState
import com.example.ui.components.CheckerboardBackground
import com.example.ui.components.ComparisonSlider
import com.example.ui.components.DownloadButton
import com.example.ui.components.Footer
import com.example.ui.components.ImagePreview
import com.example.ui.components.ProcessingIndicator
import com.example.ui.components.UploadArea
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.VioletPrimary

@Composable
fun StudioScreen(
  uiState: StudioUiState,
  isServiceConfigured: Boolean,
  isSaving: Boolean,
  onPickPhoto: () -> Unit,
  onPickDocument: () -> Unit,
  onTakePhoto: () -> Unit,
  onPickBatchPhotos: () -> Unit,
  onDropImageUris: (List<Uri>) -> Unit,
  onRemoveBackground: () -> Unit,
  onProcessBatch: () -> Unit,
  onDownloadBatchItem: (String) -> Unit,
  onSaveAllBatchPngs: () -> Unit,
  onBackdropChange: (BackdropMode) -> Unit,
  onViewModeChange: (ResultViewMode) -> Unit,
  onDownloadPng: () -> Unit,
  onSharePng: () -> Unit,
  onResetTool: () -> Unit,
  onNavigateHowItWorks: () -> Unit,
  onNavigatePricing: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 760.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        if (!isServiceConfigured) {
          ConfigurationNoticeBanner()
        }

        AnimatedContent(
          targetState = uiState,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "studio_state_transition",
        ) { state ->
          when (state) {
            is StudioUiState.Idle -> {
              Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                HeroSection(
                  onPrimaryClick = onPickPhoto,
                  onSecondaryClick = onNavigateHowItWorks,
                )

                UploadArea(
                  onBrowsePhotoPicker = onPickPhoto,
                  onBrowseDocumentFiles = onPickDocument,
                  onTakePhoto = onTakePhoto,
                  onPickBatchPhotos = onPickBatchPhotos,
                  onDropImageUris = onDropImageUris,
                )

                WorkflowStrip(activeStep = 1)
              }
            }

            is StudioUiState.ReadyToProcess -> {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                WorkflowStrip(activeStep = 2)

                ImagePreview(
                  imageInfo = state.imageInfo,
                  bitmap = state.originalBitmap,
                )

                Button(
                  onClick = onRemoveBackground,
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("process_remove_background_button"),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Remove Background",
                    style = MaterialTheme.typography.labelLarge,
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  OutlinedButton(
                    onClick = onPickPhoto,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                      .weight(1f)
                      .height(48.dp)
                      .testTag("change_image_button"),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.AddPhotoAlternate,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose Another")
                  }

                  TextButton(
                    onClick = onResetTool,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                      .height(48.dp)
                      .testTag("reset_ready_button"),
                  ) {
                    Text("Reset")
                  }
                }
              }
            }

            is StudioUiState.Processing -> {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                WorkflowStrip(activeStep = 3)
                ProcessingIndicator(
                  originalBitmap = state.originalBitmap,
                  fileName = state.imageInfo.fileName,
                )
              }
            }

            is StudioUiState.ResultReady -> {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                WorkflowStrip(activeStep = 5)

                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = EmeraldSoft,
                  border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.35f)),
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.CheckCircle,
                      contentDescription = null,
                      tint = EmeraldSuccess,
                      modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "Background removed cleanly. Drag the slider to compare or download your transparent PNG.",
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Medium,
                      color = Color(0xFF065F46),
                    )
                  }
                }

                ComparisonSlider(
                  originalBitmap = state.originalBitmap,
                  processedBitmap = state.processedBitmap,
                  backdropMode = state.backdropMode,
                  viewMode = state.viewMode,
                  onBackdropChange = onBackdropChange,
                  onViewModeChange = onViewModeChange,
                )

                DownloadButton(
                  isSaving = isSaving,
                  onDownloadClick = onDownloadPng,
                  onShareClick = onSharePng,
                  onTryAnotherClick = onPickPhoto,
                  onResetClick = onResetTool,
                )
              }
            }

            is StudioUiState.BatchMode -> {
              BatchModeSection(
                state = state,
                isSaving = isSaving,
                onProcessBatch = onProcessBatch,
                onDownloadItem = onDownloadBatchItem,
                onSaveAllPngs = onSaveAllBatchPngs,
                onPickNewBatch = onPickBatchPhotos,
                onResetTool = onResetTool,
              )
            }

            is StudioUiState.Error -> {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("error_state_card"),
                  shape = RoundedCornerShape(18.dp),
                  color = MaterialTheme.colorScheme.errorContainer,
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    verticalAlignment = Alignment.Top,
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.ErrorOutline,
                      contentDescription = "Error",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = if (state.isNotConfigured) {
                          "API Configuration Required"
                        } else {
                          "Background Removal Failed"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                      )
                    }
                  }
                }

                if (state.imageInfo != null && state.originalBitmap != null) {
                  ImagePreview(
                    imageInfo = state.imageInfo,
                    bitmap = state.originalBitmap,
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  if (state.imageInfo != null && state.originalBitmap != null) {
                    Button(
                      onClick = onRemoveBackground,
                      shape = RoundedCornerShape(14.dp),
                      modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("retry_remove_background_button"),
                    ) {
                      Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Try Again")
                    }
                  }

                  OutlinedButton(
                    onClick = onPickPhoto,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                      .weight(1f)
                      .height(48.dp)
                      .testTag("error_choose_another_button"),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.AddPhotoAlternate,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose Image")
                  }
                }

                if (state.imageInfo == null) {
                  UploadArea(
                    onBrowsePhotoPicker = onPickPhoto,
                    onBrowseDocumentFiles = onPickDocument,
                    onTakePhoto = onTakePhoto,
                    onPickBatchPhotos = onPickBatchPhotos,
                    onDropImageUris = onDropImageUris,
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Footer(
          onNavigateHome = onResetTool,
          onNavigateRemover = onPickPhoto,
          onNavigatePricing = onNavigatePricing,
        )
      }
    }
  }
}

@Composable
private fun BatchModeSection(
  state: StudioUiState.BatchMode,
  isSaving: Boolean,
  onProcessBatch: () -> Unit,
  onDownloadItem: (String) -> Unit,
  onSaveAllPngs: () -> Unit,
  onPickNewBatch: () -> Unit,
  onResetTool: () -> Unit,
) {
  val doneCount = state.items.count { it.status is BatchItemStatus.Done }
  val hasPending = state.items.any { it.status is BatchItemStatus.Queued || it.status is BatchItemStatus.Failed }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("batch_mode_section"),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.Collections,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Batch Removal Queue",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "$doneCount of ${state.items.size} completed",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }

    state.items.forEach { item ->
      BatchItemCard(
        item = item,
        onSaveClick = { onDownloadItem(item.id) },
      )
    }

    if (hasPending) {
      Button(
        onClick = onProcessBatch,
        enabled = !state.isProcessing,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("process_batch_button"),
      ) {
        if (state.isProcessing) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.5.dp,
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text("Processing Batch…")
        } else {
          Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Remove Backgrounds (${state.items.size} Images)")
        }
      }
    }

    if (doneCount > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      Button(
        onClick = onSaveAllPngs,
        enabled = !isSaving && !state.isProcessing,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondary,
          contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("save_all_batch_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.Download,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Download All Transparent PNGs ($doneCount)")
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      OutlinedButton(
        onClick = onPickNewBatch,
        enabled = !state.isProcessing,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .weight(1f)
          .height(48.dp),
      ) {
        Text("Pick New Batch")
      }

      TextButton(
        onClick = onResetTool,
        enabled = !state.isProcessing,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.height(48.dp),
      ) {
        Text("Reset")
      }
    }
  }
}

@Composable
private fun BatchItemCard(
  item: BatchItem,
  onSaveClick: () -> Unit,
) {
  val thumbBitmap = remember(item.status, item.originalBitmap) {
    when (val s = item.status) {
      is BatchItemStatus.Done -> s.processedBitmap.asImageBitmap()
      else -> item.originalBitmap.asImageBitmap()
    }
  }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    shadowElevation = 2.dp,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
      ) {
        if (item.status is BatchItemStatus.Done) {
          CheckerboardBackground(modifier = Modifier.fillMaxSize())
        } else {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.surfaceVariant)
          )
        }
        Image(
          bitmap = thumbBitmap,
          contentDescription = item.imageInfo.fileName,
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize(),
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.imageInfo.fileName,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(3.dp))
        when (val status = item.status) {
          is BatchItemStatus.Queued -> {
            Text(
              text = "Queued • ${item.imageInfo.formatLabel}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          is BatchItemStatus.Processing -> {
            Text(
              text = "Removing background…",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Medium,
            )
          }
          is BatchItemStatus.Done -> {
            Text(
              text = "Ready • Transparent PNG",
              style = MaterialTheme.typography.bodySmall,
              color = EmeraldSuccess,
              fontWeight = FontWeight.Medium,
            )
          }
          is BatchItemStatus.Failed -> {
            Text(
              text = status.message,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.error,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      when (item.status) {
        is BatchItemStatus.Processing -> {
          CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.5.dp,
          )
        }
        is BatchItemStatus.Done -> {
          OutlinedButton(
            onClick = onSaveClick,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.height(48.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.Download,
              contentDescription = "Save transparent PNG",
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("PNG")
          }
        }
        else -> Unit
      }
    }
  }
}

@Composable
private fun HeroSection(
  onPrimaryClick: () -> Unit,
  onSecondaryClick: () -> Unit,
) {
  BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    val isWide = maxWidth >= 600.dp

    Surface(
      shape = RoundedCornerShape(26.dp),
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
      shadowElevation = 4.dp,
    ) {
      if (isWide) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalArrangement = Arrangement.spacedBy(24.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          HeroCopy(
            onPrimaryClick = onPrimaryClick,
            onSecondaryClick = onSecondaryClick,
            modifier = Modifier.weight(1.1f),
          )
          HeroShowcaseArt(modifier = Modifier.weight(0.9f))
        }
      } else {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
          HeroCopy(
            onPrimaryClick = onPrimaryClick,
            onSecondaryClick = onSecondaryClick,
          )
          HeroShowcaseArt()
        }
      }
    }
  }
}

@Composable
private fun HeroCopy(
  onPrimaryClick: () -> Unit,
  onSecondaryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Surface(
      shape = RoundedCornerShape(999.dp),
      color = MaterialTheme.colorScheme.primaryContainer,
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Rounded.AutoAwesome,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(15.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "AI Background Removal",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = stringResource(R.string.hero_headline),
      style = MaterialTheme.typography.displaySmall,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = stringResource(R.string.hero_subtitle),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(modifier = Modifier.height(18.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Button(
        onClick = onPrimaryClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("hero_remove_bg_button"),
      ) {
        Text(
          text = stringResource(R.string.cta_remove_background),
          style = MaterialTheme.typography.labelLarge,
        )
      }

      OutlinedButton(
        onClick = onSecondaryClick,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        modifier = Modifier
          .height(50.dp)
          .testTag("hero_how_it_works_button"),
      ) {
        Text(
          text = stringResource(R.string.cta_see_how_it_works),
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}

@Composable
private fun HeroShowcaseArt(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(16f / 9f)
      .clip(RoundedCornerShape(18.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
  ) {
    Image(
      painter = painterResource(id = R.drawable.img_hero_cutout_1794755406943),
      contentDescription = "AI studio transparent background cutout preview",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize(),
    )
    Surface(
      shape = RoundedCornerShape(999.dp),
      color = Color(0xFF0F172A).copy(alpha = 0.78f),
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(10.dp),
    ) {
      Text(
        text = "Transparent PNG Output",
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkflowStrip(activeStep: Int) {
  val steps = listOf(
    1 to "Upload",
    2 to "Remove BG",
    3 to "AI Process",
    4 to "Preview",
    5 to "Download PNG",
  )

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier.fillMaxWidth(),
  ) {
    FlowRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      steps.forEachIndexed { index, (stepNumber, title) ->
        val isActive = stepNumber == activeStep
        val isCompleted = stepNumber < activeStep
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = when {
              isActive -> MaterialTheme.colorScheme.primary
              isCompleted -> MaterialTheme.colorScheme.primaryContainer
              else -> MaterialTheme.colorScheme.surface
            },
            border = if (!isActive && !isCompleted) {
              BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            } else {
              null
            },
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              val stepCircleBrush = if (isActive) {
                Brush.linearGradient(
                  listOf(Color.White.copy(alpha = 0.24f), Color.White.copy(alpha = 0.24f))
                )
              } else {
                Brush.linearGradient(listOf(VioletPrimary, BlueAccent))
              }
              Box(
                modifier = Modifier
                  .size(18.dp)
                  .clip(CircleShape)
                  .background(stepCircleBrush),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  text = stepNumber.toString(),
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) {
                  MaterialTheme.colorScheme.onPrimary
                } else {
                  MaterialTheme.colorScheme.onSurface
                },
              )
            }
          }
          if (index < steps.lastIndex) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.size(14.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ConfigurationNoticeBanner() {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("api_not_configured_banner"),
    shape = RoundedCornerShape(16.dp),
    color = AmberSoft,
    border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f)),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Rounded.Info,
        contentDescription = null,
        tint = AmberWarning,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = stringResource(R.string.error_not_configured),
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF78350F),
      )
    }
  }
}
