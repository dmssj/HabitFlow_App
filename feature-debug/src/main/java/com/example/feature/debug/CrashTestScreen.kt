package com.example.feature.debug

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.service.CrashReporter

@Composable
fun CrashTestScreen(
    crashReporter: CrashReporter
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crash Test Room", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                crashReporter.log("Generate crash button clicked")
                throw NullPointerException("Manual crash from control task")
            }
        ) {
            Text("Generate Crash")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                crashReporter.log("Non-fatal error triggered")
                crashReporter.recordNonFatal(IllegalStateException("Test non-fatal error"))
            }
        ) {
            Text("Record Non-Fatal")
        }
    }
}
