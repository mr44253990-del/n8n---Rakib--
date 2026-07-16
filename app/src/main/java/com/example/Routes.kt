package com.example

import kotlinx.serialization.Serializable

@Serializable object LoginRoute
@Serializable object DashboardRoute
@Serializable object WorkflowsRoute
@Serializable object ChatRoute
@Serializable object SettingsRoute

// Features
@Serializable data class FeatureRoute(val title: String)
