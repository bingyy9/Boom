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
    val resolutionUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val bitrateUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val frameRateUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val audioBitrateUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val audioSampleRateUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val audioChannelUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val cameraIdUpdated: MutableLiveData<Boolean> = MutableLiveData()

    enum class PostType{
        NONE
        , TIME_DELAY_BEFORE_RECORDING
        , FILE_NAME_FORMAT
        , RESOLUTION
        , FRAME_RATE
        , BITRATE
        , AUDIO_BITRATE
        , AUDIO_SAMPLE_RATE
        , AUDIO_CHANNEL
        , CAMERA_ID
    }


    override fun onCleared() {
        super.onCleared()
    }

    fun postValueUpdated(type: PostType){
        when(type){
            PostType.TIME_DELAY_BEFORE_RECORDING -> timeDelayBeforeRecording.postValue(true)
            PostType.FILE_NAME_FORMAT -> fileNameFormatUpdated.postValue(true)
            PostType.BITRATE -> bitrateUpdated.postValue(true)
            PostType.FRAME_RATE -> frameRateUpdated.postValue(true)
            PostType.RESOLUTION -> resolutionUpdated.postValue(true)
            PostType.AUDIO_BITRATE -> audioBitrateUpdated.postValue(true)
            PostType.AUDIO_SAMPLE_RATE -> audioSampleRateUpdated.postValue(true)
            PostType.AUDIO_CHANNEL -> audioChannelUpdated.postValue(true)
            PostType.CAMERA_ID -> cameraIdUpdated.postValue(true)
        }

    }





}