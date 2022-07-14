package com.louis.bpaaisubmission.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.louis.bpaaisubmission.data.local.entity.StoryEntity
import com.louis.bpaaisubmission.databinding.ItemStoryBinding
import com.louis.bpaaisubmission.utils.Helper.loadImageUrl
import com.louis.bpaaisubmission.utils.Helper.withDateFormat
import com.louis.bpaaisubmission.views.DetailStoryActivity

class ListStoryAdapter : PagingDataAdapter<StoryEntity, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position) as StoryEntity
        holder.bind(story)
    }

    class ListViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            binding.apply {
                tvName.text = story.name
                tvDate.text = story.createdAt.withDateFormat()
                tvDescription.text = story.description
                ivStory.loadImageUrl(story.photoUrl)

                root.setOnClickListener {

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            it.context as Activity,
                            Pair(tvDate, "date"),
                            Pair(tvName, "name"),
                            Pair(tvDescription, "description"),
                            Pair(ivUserpic, "profil_pic"),
                            Pair(ivStory, "story_pic")
                        )

                    Intent(it.context, DetailStoryActivity::class.java).apply {
                        putExtra(DetailStoryActivity.EXTRA_DETAIL, story)
                    }.also { intent ->
                        it.context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldStory: StoryEntity, newStory: StoryEntity): Boolean {
                return oldStory.id == newStory.id
            }

            override fun areContentsTheSame(oldStory: StoryEntity, newStory: StoryEntity): Boolean {
                return oldStory == newStory
            }
        }
    }
}