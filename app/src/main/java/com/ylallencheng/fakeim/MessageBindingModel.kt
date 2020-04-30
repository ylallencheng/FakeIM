package com.ylallencheng.fakeim

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.recyclerview.widget.DiffUtil

class MessageBindingModel(
    val id: Int,
    val sendOrReceive: Boolean,
    val timestamp: String,
    val message: String
) : BaseObservable() {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MessageBindingModel>() {
            override fun areItemsTheSame(
                oldItem: MessageBindingModel,
                newItem: MessageBindingModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MessageBindingModel,
                newItem: MessageBindingModel
            ): Boolean =
                oldItem.timestamp == newItem.timestamp
                        && oldItem.message == newItem.message
        }
    }

    var checked: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.checked)
        }

    var isEditing: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.editing)
        }
}