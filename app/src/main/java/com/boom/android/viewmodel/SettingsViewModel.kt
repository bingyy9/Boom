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
    val fileNameFormatUpdated: MutableLiveData<Boolean> = MutableLiveData()

    enum class PostType{
        TIME_DELAY_BEFORE_RECORDING, FILE_NAME_FORMAT
    }


    override fun onCleared() {
        super.onCleared()
    }

    fun postValueUpdated(type: PostType){
        when(type){
            PostType.TIME_DELAY_BEFORE_RECORDING -> timeDelayBeforeRecording.postValue(true)
            PostType.FILE_NAME_FORMAT -> fileNameFormatUpdated.postValue(true)
        }

    }





}