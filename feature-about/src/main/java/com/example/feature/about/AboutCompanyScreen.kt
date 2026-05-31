package com.example.feature.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.core.navigation.NavigationContract
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutCompanyScreen(
    viewModel: AboutCompanyViewModel,
    navigator: NavigationContract
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Координаты офиса (Москва, например)
    val officePoint = Point(55.751244, 37.618423)
    
    // Create MapView once and remember it
    val mapView = remember { MapView(context) }

    LaunchedEffect(Unit) {
        viewModel.onMapOpened()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapView.onStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    mapView.onStop()
                    MapKitFactory.getInstance().onStop()
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        // Handle case where we are already in started state
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            MapKitFactory.getInstance().onStart()
            mapView.onStart()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О компании") },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            AndroidView(
                factory = { 
                    mapView.apply {
                        map.move(
                            CameraPosition(officePoint, 15.0f, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 0f),
                            null
                        )
                        // Добавляем маркер офиса
                        map.mapObjects.addPlacemark(officePoint)
                        
                        // Включаем слой местоположения пользователя
                        val mapKit = MapKitFactory.getInstance()
                        mapKit.createUserLocationLayer(mapWindow).apply {
                            isVisible = true
                            isHeadingEnabled = true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Button(
                onClick = {
                    viewModel.onBuildRouteClicked()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Построить маршрут")
            }
        }
    }
}
