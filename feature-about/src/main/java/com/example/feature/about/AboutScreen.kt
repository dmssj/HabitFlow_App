package com.example.feature.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val officePoint = Point(55.751244, 37.618423)

    DisposableEffect(Unit) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        
        mapView.mapWindow.map.move(
            CameraPosition(officePoint, 11.0f, 0.0f, 0.0f)
        )
        mapView.mapWindow.map.mapObjects.addPlacemark(officePoint)
        
        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "О приложении HabitFlow",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "HabitFlow Inc. — лидер в разработке инструментов продуктивности. Наша миссия — помочь людям формировать полезные привычки.",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Наш офис: Технологический проспект, 123, Москва",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Button(
            onClick = {
                mapView.mapWindow.map.move(
                    CameraPosition(officePoint, 16.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Показать офис на карте")
        }
        
        AndroidView(
            factory = { mapView },
            modifier = Modifier.weight(1f)
        )
    }
}
