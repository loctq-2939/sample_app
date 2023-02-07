package com.example.detect_voice_app.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.detect_voice_app.utils.SingleLiveEvent

open class BaseViewModel : ViewModel() {

    // loading flag
    val isLoading by lazy { MutableLiveData(false) }

    // error message
    val errorMessage by lazy { SingleLiveEvent<String>() }

    // optional flags
    val noInternetConnectionEvent by lazy { SingleLiveEvent<Unit>() }
    val connectTimeoutEvent by lazy { SingleLiveEvent<Unit>() }
    val forceUpdateAppEvent by lazy { SingleLiveEvent<Unit>() }
    val serverMaintainEvent by lazy { SingleLiveEvent<Unit>() }
    val forbiddenEvent by lazy { SingleLiveEvent<Unit>() }
    val unknownErrorEvent = SingleLiveEvent<Int?>()
    val httpNotFoundEvent by lazy { SingleLiveEvent<Unit>() }
    val badGatewayEvent by lazy { SingleLiveEvent<Unit>() }
    val httpNotImplement by lazy { SingleLiveEvent<Unit>() }


    open fun showError(e: Throwable) {
        errorMessage.value = e.message
    }

    fun showLoading(needShowLoading: Boolean? = true) {
        isLoading.value = needShowLoading
    }
}


