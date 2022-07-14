package com.louis.bpaaisubmission.views

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.data.local.entity.StoryEntity
import com.louis.bpaaisubmission.databinding.ActivityDetailStoryBinding
import com.louis.bpaaisubmission.utils.Helper.withDateFormat

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportPostponeEnterTransition()

        setToolbar()
        parseStoryDetail()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun parseStoryDetail() {
        val data = intent.getParcelableExtra<StoryEntity>(EXTRA_DETAIL)

        binding.apply {
            tvDate.text = resources.getString(R.string.uploaded_on, data?.createdAt?.withDateFormat())
            tvName.text = data?.name
            tvDescription.text = data?.description


            val listener = object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

            }

            Glide.with(this@DetailStoryActivity)
                .load(data?.photoUrl.toString())
                .listener(listener)
                .placeholder(R.drawable.defaultpicstory)
                .error(R.drawable.defaultpicstory)
                .into(ivStory)
        }

    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    companion object {
        const val EXTRA_DETAIL = "extra_story"
    }
}