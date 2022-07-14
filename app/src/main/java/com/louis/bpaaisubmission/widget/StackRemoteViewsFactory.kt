package com.louis.bpaaisubmission.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.data.local.entity.StoryEntity
import com.louis.bpaaisubmission.data.local.room.StoryDatabase

internal class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private val listStory = ArrayList<StoryEntity>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        val database = StoryDatabase.getInstance(context)

        database.storyDao().getAllStoryForWidget().forEach {
            listStory.add(
                StoryEntity(it.id, it.name, it.description, it.lon, it.lat, it.photoUrl, it.createdAt)
            )
        }
    }

    override fun onDestroy() {}

    override fun getCount() = listStory.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, listStory[position].photoUrl.toBitmap())

        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to listStory[position].name
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount() = 1

    override fun getItemId(p0: Int) = 0L

    override fun hasStableIds() = false

    private fun String.toBitmap() : Bitmap {
        return Glide.with(context)
            .asBitmap()
            .load(this)
            .submit()
            .get()
    }
}