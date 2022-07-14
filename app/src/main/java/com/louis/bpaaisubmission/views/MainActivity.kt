package com.louis.bpaaisubmission.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.adapter.ListStoryAdapter
import com.louis.bpaaisubmission.data.remote.response.StoryItem
import com.louis.bpaaisubmission.databinding.ActivityMainBinding
import com.louis.bpaaisubmission.utils.Helper.toBearerToken
import com.louis.bpaaisubmission.utils.Result
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

    private fun scrollToTop() {
        binding.rvStories.apply {
            layoutManager?.smoothScrollToPosition(this, null, 0)
        }
    }

    private fun setRecyclerView() {
        listStoryAdapter = ListStoryAdapter()

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listStoryAdapter
        }

        binding.swipeRefresh.setOnRefreshListener {
            refreshStories()
            scrollToTop()
        }

        refreshStories()
    }

    private fun refreshStories(){
        mainViewModel.apply {
            getSession().observe(this@MainActivity) { session ->
                getAllStories(session.token.toBearerToken()).observe(this@MainActivity) { result ->
                    showResult(result)
                }
            }
        }
    }

    private fun showResult(result: Result<ArrayList<StoryItem>>?) {
        binding.swipeRefresh.isRefreshing = true
        when (result) {
            is Result.Loading -> {
                binding.swipeRefresh.isRefreshing = true
            }

            is Result.Success -> {
                listStoryAdapter.submitList(result.data)
                binding.swipeRefresh.isRefreshing = false
            }

            is Result.Error -> {
                Snackbar.make(
                    binding.root,
                    resources.getString(R.string.error, result.errorMessage),
                    Snackbar.LENGTH_SHORT
                ).show()


                binding.swipeRefresh.isRefreshing = false

            }
            else -> {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.meno_on_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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