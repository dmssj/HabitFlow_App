package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.navigation.NavigationContract
import com.example.domain.service.AuthService
import com.example.domain.service.RemoteConfigService
import com.example.domain.service.CrashReporter
import com.example.feature.auth.LoginScreen
import com.example.feature.auth.LoginViewModel
import com.example.feature.about.AboutCompanyViewModel
import com.example.feature.create.CreateHabitScreen
import com.example.feature.create.CreateHabitViewModel
import com.example.feature.main.details.HabitDetailsScreen
import com.example.feature.main.details.HabitDetailsViewModel
import com.example.feature.main.list.HabitListScreen
import com.example.feature.main.list.HabitListViewModel
import com.example.feature.statistics.HabitStatisticsScreen
import com.example.feature.statistics.HabitStatisticsViewModel
import com.example.feature.debug.CrashTestScreen
import com.example.feature.detection.ObjectDetectionScreen
import com.example.feature.detection.ObjectDetectionViewModel
import com.example.feature.profile.ProfileScreen
import com.example.feature.profile.ProfileViewModel
import com.example.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authService: AuthService
    @Inject lateinit var remoteConfigService: RemoteConfigService
    @Inject lateinit var crashReporter: CrashReporter

    private val habitListViewModel: HabitListViewModel by viewModels()
    private val habitDetailsViewModel: HabitDetailsViewModel by viewModels()
    private val createHabitViewModel: CreateHabitViewModel by viewModels()
    private val habitStatisticsViewModel: HabitStatisticsViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val aboutCompanyViewModel: AboutCompanyViewModel by viewModels()
    private val detectionViewModel: ObjectDetectionViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Handle deep link / intent navigation
                    val currentIntent = intent
                    LaunchedEffect(currentIntent, currentIntent?.getStringExtra("navigate_to")) {
                        if (currentIntent?.getStringExtra("navigate_to") == "about_company") {
                            // Clear the extra so it doesn't trigger again on recomposition
                            intent?.removeExtra("navigate_to")
                            navController.navigate("about_company") {
                                launchSingleTop = true
                            }
                        }
                    }

                    val navigator = object : NavigationContract {
                        override fun navigateToHabitList() {
                            navController.navigate("habit_list") {
                                popUpTo("habit_list") { inclusive = true }
                            }
                        }

                        override fun navigateToCreateHabit() {
                            navController.navigate("create_habit")
                        }

                        override fun navigateToHabitDetails(habitId: Long) {
                            navController.navigate("habit_details/$habitId")
                        }

                        override fun navigateToStatistics() {
                            navController.navigate("statistics")
                        }

                        override fun navigateToProfile() {
                            navController.navigate("profile")
                        }

                        override fun navigateToAbout() {
                            val intent = android.content.Intent(this@MainActivity, com.example.feature.about.AboutActivity::class.java)
                            startActivity(intent)
                        }

                        override fun navigateToAboutCompany() {
                            navController.navigate("about_company")
                        }

                        override fun navigateToDetection() {
                            navController.navigate("detection")
                        }

                        override fun navigateToCrashTest() {
                            navController.navigate("crash_test")
                        }

                        override fun navigateToLogin() {
                            navController.navigate("login") {
                                popUpTo("habit_list") { inclusive = true }
                            }
                        }

                        override fun navigateBack() {
                            navController.popBackStack()
                        }
                    }

                    val startDestination = if (authService.getCurrentUser() != null) "habit_list" else "login"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    navigator.navigateToHabitList()
                                }
                            )
                        }

                        composable("habit_list") { _: NavBackStackEntry ->
                            HabitListScreen(
                                viewModel = habitListViewModel,
                                navigator = navigator
                            )
                        }

                        composable("create_habit") { _: NavBackStackEntry ->
                            CreateHabitScreen(
                                viewModel = createHabitViewModel,
                                navigator = navigator
                            )
                        }

                        composable(
                            route = "habit_details/{habitId}",
                            arguments = listOf(
                                navArgument("habitId") { type = NavType.LongType }
                            )
                        ) { backStackEntry: NavBackStackEntry ->
                            val habitId = backStackEntry.arguments?.getLong("habitId") ?: -1L
                            HabitDetailsScreen(
                                habitId = habitId,
                                viewModel = habitDetailsViewModel,
                                navigator = navigator
                            )
                        }

                        composable("statistics") { _: NavBackStackEntry ->
                            HabitStatisticsScreen(
                                viewModel = habitStatisticsViewModel,
                                navigator = navigator
                            )
                        }

                        composable("about_company") {
                            com.example.feature.about.AboutCompanyScreen(
                                viewModel = aboutCompanyViewModel,
                                navigator = navigator
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                remoteConfigService = remoteConfigService,
                                onBack = { navigator.navigateToHabitList() }
                            )
                        }

                        composable("detection") {
                            ObjectDetectionScreen(viewModel = detectionViewModel)
                        }

                        composable("crash_test") {
                            CrashTestScreen(crashReporter = crashReporter)
                        }
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Already granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
