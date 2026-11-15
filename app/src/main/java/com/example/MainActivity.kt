package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.BackgroundRemoverViewModel
import com.example.ui.components.Header
import com.example.ui.screens.FaqScreen
import com.example.ui.screens.HowItWorksScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.BGRemoverProTheme
import kotlinx.coroutines.launch

const val ROUTE_STUDIO = "studio"
const val ROUTE_HOW_IT_WORKS = "how_it_works"
const val ROUTE_PRICING = "pricing"
const val ROUTE_FAQ = "faq"

private data class NavDestinationItem(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val NAV_ITEMS = listOf(
  NavDestinationItem(
    route = ROUTE_STUDIO,
    label = "Remover",
    selectedIcon = Icons.Rounded.AutoFixHigh,
    unselectedIcon = Icons.Outlined.AutoFixHigh,
    testTag = "nav_studio",
  ),
  NavDestinationItem(
    route = ROUTE_HOW_IT_WORKS,
    label = "Guide",
    selectedIcon = Icons.Rounded.Layers,
    unselectedIcon = Icons.Outlined.Layers,
    testTag = "nav_how_it_works",
  ),
  NavDestinationItem(
    route = ROUTE_PRICING,
    label = "Pricing",
    selectedIcon = Icons.Rounded.Payments,
    unselectedIcon = Icons.Outlined.Payments,
    testTag = "nav_pricing",
  ),
  NavDestinationItem(
    route = ROUTE_FAQ,
    label = "FAQ",
    selectedIcon = Icons.Rounded.Help,
    unselectedIcon = Icons.Outlined.HelpOutline,
    testTag = "nav_faq",
  ),
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BGRemoverProTheme {
        BGRemoverApp()
      }
    }
  }
}

@Composable
fun BGRemoverApp(
  viewModel: BackgroundRemoverViewModel = viewModel(),
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: ROUTE_STUDIO

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
  val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

  LaunchedEffect(feedbackMessage) {
    val message = feedbackMessage
    if (message != null) {
      snackbarHostState.showSnackbar(message)
      viewModel.clearFeedbackMessage()
    }
  }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
  ) { uri ->
    if (uri != null) {
      viewModel.onImagePicked(uri)
    }
  }

  val batchPhotoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
  ) { uris ->
    if (uris.isNotEmpty()) {
      viewModel.onBatchImagesPicked(uris)
    }
  }

  val documentPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument(),
  ) { uri ->
    if (uri != null) {
      viewModel.onImagePicked(uri)
    }
  }

  val takePictureLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture(),
  ) { success ->
    val captured = pendingCameraUri
    pendingCameraUri = null
    if (success && captured != null) {
      viewModel.onImagePicked(captured)
    }
  }

  val createPngDocumentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("image/png"),
  ) { uri ->
    if (uri != null) {
      viewModel.saveToSafUri(uri)
    }
  }

  fun launchPhotoPicker() {
    photoPickerLauncher.launch(
      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )
  }

  fun launchBatchPhotoPicker() {
    batchPhotoPickerLauncher.launch(
      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )
  }

  fun launchDocumentPicker() {
    documentPickerLauncher.launch(
      arrayOf("image/jpeg", "image/png", "image/webp")
    )
  }

  fun launchCameraCapture() {
    val uri = viewModel.createCameraCaptureUri()
    pendingCameraUri = uri
    takePictureLauncher.launch(uri)
  }

  fun navigateToTab(route: String) {
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavigationRail = maxWidth >= 600.dp

    Scaffold(
      modifier = Modifier.fillMaxSize(),
      contentWindowInsets = WindowInsets.safeDrawing,
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      topBar = {
        Header(
          onLogoClick = { navigateToTab(ROUTE_STUDIO) },
          onGetStartedClick = {
            if (currentRoute != ROUTE_STUDIO) {
              navigateToTab(ROUTE_STUDIO)
            } else {
              launchPhotoPicker()
            }
          },
          showGetStarted = currentRoute != ROUTE_STUDIO,
        )
      },
      bottomBar = {
        if (!useNavigationRail) {
          NavigationBar(
            windowInsets = WindowInsets.navigationBars,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
          ) {
            NAV_ITEMS.forEach { item ->
              val selected = currentRoute == item.route
              NavigationBarItem(
                selected = selected,
                onClick = { navigateToTab(item.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                  )
                },
                label = { Text(item.label) },
                modifier = Modifier.testTag(item.testTag),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
      ) {
        if (useNavigationRail) {
          NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surface,
          ) {
            NAV_ITEMS.forEach { item ->
              val selected = currentRoute == item.route
              NavigationRailItem(
                selected = selected,
                onClick = { navigateToTab(item.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                  )
                },
                label = { Text(item.label) },
                modifier = Modifier.testTag(item.testTag),
              )
            }
          }
        }

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
          NavHost(
            navController = navController,
            startDestination = ROUTE_STUDIO,
            modifier = Modifier.fillMaxSize(),
          ) {
            composable(ROUTE_STUDIO) {
              StudioScreen(
                uiState = uiState,
                isServiceConfigured = viewModel.isServiceConfigured,
                isSaving = isSaving,
                onPickPhoto = ::launchPhotoPicker,
                onPickDocument = ::launchDocumentPicker,
                onTakePhoto = ::launchCameraCapture,
                onPickBatchPhotos = ::launchBatchPhotoPicker,
                onDropImageUris = { uris -> viewModel.onBatchImagesPicked(uris) },
                onRemoveBackground = { viewModel.removeBackground() },
                onProcessBatch = { viewModel.processBatch() },
                onDownloadBatchItem = { itemId ->
                  viewModel.downloadBatchItemPng(itemId) { suggestedName ->
                    createPngDocumentLauncher.launch(suggestedName)
                  }
                },
                onSaveAllBatchPngs = { viewModel.saveAllBatchPngs() },
                onBackdropChange = { viewModel.setBackdropMode(it) },
                onViewModeChange = { viewModel.setResultViewMode(it) },
                onDownloadPng = {
                  viewModel.downloadTransparentPng { suggestedName ->
                    createPngDocumentLauncher.launch(suggestedName)
                  }
                },
                onSharePng = {
                  viewModel.shareTransparentPng { uri ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                      type = "image/png"
                      putExtra(Intent.EXTRA_STREAM, uri)
                      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(
                      Intent.createChooser(shareIntent, "Share Transparent PNG")
                    )
                  }
                },
                onResetTool = { viewModel.resetTool() },
                onNavigateHowItWorks = { navigateToTab(ROUTE_HOW_IT_WORKS) },
                onNavigatePricing = { navigateToTab(ROUTE_PRICING) },
              )
            }

            composable(ROUTE_HOW_IT_WORKS) {
              HowItWorksScreen(
                onNavigateStudio = { navigateToTab(ROUTE_STUDIO) },
                onNavigatePricing = { navigateToTab(ROUTE_PRICING) },
              )
            }

            composable(ROUTE_PRICING) {
              PricingScreen(
                onNavigateStudio = { navigateToTab(ROUTE_STUDIO) },
                onShowPlanNote = { note ->
                  coroutineScope.launch {
                    snackbarHostState.showSnackbar(note)
                  }
                },
              )
            }

            composable(ROUTE_FAQ) {
              FaqScreen(
                onNavigateStudio = { navigateToTab(ROUTE_STUDIO) },
                onNavigatePricing = { navigateToTab(ROUTE_PRICING) },
              )
            }
          }
        }
      }
    }
  }
}
