package com.louis.bpaaisubmission.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.data.local.entity.StoryEntity
import com.louis.bpaaisubmission.data.remote.response.StoryItem
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

    fun StoryItem.toEntityModel(): StoryEntity {
        return StoryEntity(
            this.id.toString(),
            this.name.toString(),
            this.description.toString(),
            this.lon,
            this.lat,
            this.photoUrl.toString(),
            this.createdAt.toString()
        )
    }
}