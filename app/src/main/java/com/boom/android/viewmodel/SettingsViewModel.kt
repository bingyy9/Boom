package com.boom.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boom.android.BoomApplication
import com.boom.deprecated.FloatingCameraService
import com.boom.deprecated.FloatingCounterService
import com.boom.android.service.MediaRecordService
import com.boom.android.util.PrefsUtil
import com.boom.model.interf.IRecordModel
import com.boom.model.interf.impl.ModelBuilderManager
import com.boom.model.repo.RecordEvent
import com.boom.model.repo.RecordEvent.RECORD_READY_TO_RECORD
import com.boom.model.repo.RecordRepo

class SettingsViewModel : ViewModel(){
    val timeDelayBeforeRecording: MutableLiveData<Boolean> = MutableLiveData()


    override fun onCleared() {
        super.onCleared()
    }



    fun updateTimeDelayBeforeRecording(){
        timeDelayBeforeRecording.postValue(true)
    }




}