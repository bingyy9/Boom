package com.boom.android.viewmodel

import androidx.lifecycle.ViewModel
import com.boom.android.service.MediaRecordService
import com.boom.model.interf.IRecordModel
import com.boom.model.interf.impl.ModelBuilderManager
import com.boom.model.repo.RecordEvent
import com.boom.model.repo.RecordEvent.RECORD_READY_TO_RECORD
import com.boom.model.repo.RecordRepo

class RecordViewModel : ViewModel()
        , IRecordModel.RecordEvtListener{
    var recordModel: IRecordModel = ModelBuilderManager.getModelBuilder().recordModel
    var recordRepo: RecordRepo? = null

    private val recordService: MediaRecordService? = null

    init {
        registerListener()
    }

    private fun initSiRepo(){
        this.recordRepo = recordModel.recordRepo
    }

    private fun registerListener(){
        ModelBuilderManager.getModelBuilder().recordModel?.addRecordEvtListener(this)
    }

    private fun unregisterListener(){
        ModelBuilderManager.getModelBuilder().recordModel?.removeRecordEvtListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        unregisterListener()
    }

    override fun onRecordEvt(evt: RecordEvent?) {
        when (evt!!.type) {
            RECORD_READY_TO_RECORD -> {
            }
        }
    }



}