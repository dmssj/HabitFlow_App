package com.example.feature.about

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.feature.about.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        
        // Данные о приложении
        val appName = "HabitFlow"
        val version = "1.0.0"
        val developer = "Захаров Дмитрий"

        composeView.setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Верхняя информационная часть (заголовки)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Название: $appName", style = MaterialTheme.typography.headlineSmall)
                            Text(text = "Версия: $version", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Разработчик: $developer", style = MaterialTheme.typography.bodyMedium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Наш офис на карте:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Интегрированная карта
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            val context = LocalContext.current
                            val lifecycleOwner = LocalLifecycleOwner.current
                            val officePoint = Point(55.751244, 37.618423)
                            
                            // Создаем MapView один раз
                            val mapView = remember { MapView(context) }

                            // Синхронизация жизненного цикла
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
                                
                                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                    MapKitFactory.getInstance().onStart()
                                    mapView.onStart()
                                }

                                onDispose {
                                    lifecycleOwner.lifecycle.removeObserver(observer)
                                }
                            }

                            AndroidView(
                                factory = { 
                                    mapView.apply {
                                        // Настройка карты при создании
                                        mapWindow.map.move(
                                            CameraPosition(officePoint, 15.0f, 0.0f, 0.0f),
                                            Animation(Animation.Type.SMOOTH, 0f),
                                            null
                                        )
                                        mapWindow.map.mapObjects.addPlacemark(officePoint)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
