package com.ylallencheng.fakeim

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class MainViewModel : ViewModel() {

    // Data
    private var mToBeRemovedMessageIds: MutableSet<Int> = mutableSetOf()
    private var mMessageBindingModels: List<MessageBindingModel> = listOf()

    // Triggers
    val addMessageTrigger: SingleEvent<List<MessageBindingModel>> = SingleEvent()
    val editMessageTrigger: SingleEvent<List<MessageBindingModel>> = SingleEvent()
    val actionModelTrigger: SingleEvent<Boolean> = SingleEvent()

    /**
     * Start action/edit mode
     */
    fun startActionMode() {
        if (mMessageBindingModels.isNotEmpty() &&
            mMessageBindingModels.getOrNull(0)?.isEditing == true
        ) {
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            val result =
                mMessageBindingModels
                    .map {
                        it.checked = false
                        it.isEditing = true
                        it
                    }
                    .toMutableList()

            mMessageBindingModels = result
            withContext(Dispatchers.Main) {
                editMessageTrigger.value = result
            }
        }
        actionModelTrigger.trigger()
    }

    /**
     * Finish action/edit mode
     */
    fun finishActionMode(shouldRemoveMessage: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val result =
                mMessageBindingModels
                    .filterNot {
                        when {
                            shouldRemoveMessage -> mToBeRemovedMessageIds.contains(it.id)
                            else -> false
                        }
                    }
                    .apply {
                        forEach {
                            it.checked = false
                            it.isEditing = false
                        }
                    }

            mMessageBindingModels = result

            withContext(Dispatchers.Main) {
                editMessageTrigger.value = result
            }

            mToBeRemovedMessageIds.clear()
        }
    }

    /**
     * Update selected binding models
     */
    fun updateSelectedBindingModel(
        bindingModel: MessageBindingModel
    ) {
        val isModelRemoved = mToBeRemovedMessageIds.contains(bindingModel.id)

        when {
            isModelRemoved && !bindingModel.checked -> {
                mToBeRemovedMessageIds.remove(bindingModel.id)
            }

            !isModelRemoved && bindingModel.checked -> {
                mToBeRemovedMessageIds.add(bindingModel.id)
            }
        }
    }

    /* ------------------------------ Init */

    init {

        val seed = Random()

        /*
            fake message sender
         */
        viewModelScope.launch(Dispatchers.IO) {
            for (i in 0..29) {
                val modelList = mMessageBindingModels.toMutableList()

                val timestamp = System.currentTimeMillis()
                val timestampLabel =
                    SimpleDateFormat.getTimeInstance().format(Date().apply { time = timestamp })
                val sendOrReceive = timestamp % 3 == 1L

                modelList.add(
                    MessageBindingModel(
                        id = seed.nextInt().absoluteValue,
                        timestamp = timestampLabel,
                        sendOrReceive = sendOrReceive,
                        message = "message #${i + 1}"
                    )
                )

                mMessageBindingModels = modelList
                withContext(Dispatchers.Main) {
                    addMessageTrigger.value = modelList
                }
                Thread.sleep(300)
            }
        }
    }
}