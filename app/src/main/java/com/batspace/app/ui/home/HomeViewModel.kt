package com.batspace.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.batspace.app.data.model.ClonedApp
import com.batspace.app.data.model.InstalledApp
import com.batspace.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = CloneRepository(application)

    private val _clones = MutableLiveData<List<ClonedApp>>()
    val clones: LiveData<List<ClonedApp>> = _clones

    private val _installedApps = MutableLiveData<List<InstalledApp>>()
    val installedApps: LiveData<List<InstalledApp>> = _installedApps

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _searchQuery = MutableLiveData("")

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = withContext(Dispatchers.IO) { repo.getUserApps() }
            val cloneList = withContext(Dispatchers.IO) { repo.getAllClones() }
            _installedApps.value = apps
            _clones.value = cloneList
            _isLoading.value = false
        }
    }

    fun addClone(packageName: String, appName: String, label: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            repo.createClone(packageName, appName, label)
            val updated = repo.getAllClones()
            withContext(Dispatchers.Main) { _clones.value = updated }
        }
    }

    fun deleteClone(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteClone(id)
            val updated = repo.getAllClones()
            withContext(Dispatchers.Main) { _clones.value = updated }
        }
    }

    fun updateCloneLabel(id: String, newLabel: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val clone = repo.getCloneById(id) ?: return@launch
            repo.updateClone(clone.copy(customLabel = newLabel))
            val updated = repo.getAllClones()
            withContext(Dispatchers.Main) { _clones.value = updated }
        }
    }

    fun toggleLock(id: String, locked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val clone = repo.getCloneById(id) ?: return@launch
            repo.updateClone(clone.copy(isLocked = locked))
            val updated = repo.getAllClones()
            withContext(Dispatchers.Main) { _clones.value = updated }
        }
    }

    fun refreshIdentity(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.refreshVirtualIdentity(id)
            val updated = repo.getAllClones()
            withContext(Dispatchers.Main) { _clones.value = updated }
        }
    }

    fun getFilteredApps(query: String): List<InstalledApp> {
        if (query.isBlank()) return _installedApps.value ?: emptyList()
        return _installedApps.value?.filter {
            it.appName.contains(query, ignoreCase = true) ||
            it.packageName.contains(query, ignoreCase = true)
        } ?: emptyList()
    }
}
