package com.example.android.marsphotos.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.example.android.marsphotos.network.MarsApi
import com.example.android.marsphotos.network.MarsApiService
import com.example.android.marsphotos.network.MarsPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
enum class MarsApiStatus { LOADING, ERROR, DONE }

class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()
    private val _photos = MutableLiveData<List<MarsPhoto>>()
    val photos: LiveData<List<MarsPhoto>> = _photos
    // The external immutable LiveData for the request status
    val status: LiveData<MarsApiStatus> = _status
    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [LiveData].
     */
    private fun getMarsPhotos() {
        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            try {
                withContext(Dispatchers.IO){
                    _photos.postValue(MarsApiService().parse())
                }
                _status.value = MarsApiStatus.DONE

            } catch (e: Exception) {
                Log.d("ASD", "$e Coroutine Error")
                e.printStackTrace()
                _status.value = MarsApiStatus.ERROR
                _photos.value = listOf()
            }
        }
    }

}