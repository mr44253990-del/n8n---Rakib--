package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.N8nApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class N8nViewModel : ViewModel() {
    private val _workflows = MutableStateFlow<List<JSONObject>>(emptyList())
    val workflows: StateFlow<List<JSONObject>> = _workflows.asStateFlow()

    private val _executions = MutableStateFlow<List<JSONObject>>(emptyList())
    val executions: StateFlow<List<JSONObject>> = _executions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun refreshData() {
        if (N8nApiClient.authMode == 2) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _workflows.value = N8nApiClient.getWorkflows()
                _executions.value = N8nApiClient.getExecutions()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
