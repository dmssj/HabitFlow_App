package com.example.feature.about

import androidx.lifecycle.ViewModel
import com.example.domain.service.AnalyticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutCompanyViewModel @Inject constructor(
    private val analyticsService: AnalyticsService
) : ViewModel() {

    fun onMapOpened() {
        analyticsService.trackEvent("map_opened")
    }

    fun onBuildRouteClicked() {
        analyticsService.trackEvent("build_route_clicked")
    }
}
