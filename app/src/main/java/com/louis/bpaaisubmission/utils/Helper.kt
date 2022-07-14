package com.louis.bpaaisubmission.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.louis.bpaaisubmission.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Helper {
    fun ImageView.loadImageUrl(src: String){
        Glide.with(this.context)
            .load(src)
            .placeholder(R.drawable.defaultpicstory)
            .error(R.drawable.defaultpicstory)
            .centerCrop()
            .into(this)
    }

    fun String.toBearerToken() = "Bearer $this"

    fun String.withDateFormat(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = format.parse(this) as Date
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }

}