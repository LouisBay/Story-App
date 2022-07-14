package com.louis.bpaaisubmission.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.adapter.ListStoryAdapter
import com.louis.bpaaisubmission.adapter.LoadingStateAdapter
import com.louis.bpaaisubmission.data.local.entity.StoryEntity
import com.louis.bpaaisubmission.databinding.ActivityMainBinding
import com.louis.bpaaisubmission.utils.Helper.toBearerToken
import com.louis.bpaaisubmission.utils.ViewModelFactory
import com.louis.bpaaisubmission.viewmodels.MainViewModel

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listStoryAdapter: ListStoryAdapter

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setRecyclerView()

        binding.fabAdd.setOnClickListener(this)
    }

    private fun setRecyclerView() {
        listStoryAdapter = ListStoryAdapter()

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listStoryAdapter.retry()
                }
            )
        }

        binding.swipeRefresh.setOnRefreshListener {
            refreshStories()
        }

        refreshStories()
    }

    private fun refreshStories(){
        binding.swipeRefresh.isRefreshing = true
        mainViewModel.apply {
            getSession().observe(this@MainActivity) { session ->
                getAllStories(session.token.toBearerToken()).observe(this@MainActivity) { result ->
                    showResult(result)
                }
            }
        }
    }

    private fun showResult(result: PagingData<StoryEntity>) {
        listStoryAdapter.submitData(lifecycle, result)
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.meno_on_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.story_maps -> {
                Intent(this@MainActivity, MapsActivity::class.java).also {
                    startActivity(it)
                }
                true
            }
            R.id.logout -> {
                mainViewModel.deleteSession()
                Intent(this@MainActivity, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
                true
            }
            R.id.setting_language -> {
                Intent(Settings.ACTION_LOCALE_SETTINGS).apply {
                    startActivity(this)
                }
                true
            }
            else -> true
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_add -> {
                Intent(this@MainActivity, AddStoryActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }
}