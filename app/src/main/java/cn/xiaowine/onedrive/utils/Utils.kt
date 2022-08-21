package cn.xiaowine.onedrive.utils

import android.content.Context
import android.text.format.Formatter

object Utils {

    fun formatSize(context: Context,value: Long): String {
       return Formatter.formatFileSize(context,value)
    }


    fun Any?.isNull(callback: () -> Unit) {
        if (this == null) callback()
    }

    fun Any?.isNotNull(callback: () -> Unit) {
        if (this != null) callback()
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null
}