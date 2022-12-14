package com.wirajasa.wirajasabisnis.feature_buyer.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.wirajasa.wirajasabisnis.R
import com.wirajasa.wirajasabisnis.core.utility.constant.Dump.EXTRA_SERVICE_POST
import com.wirajasa.wirajasabisnis.core.utility.constant.Dump.GUEST
import com.wirajasa.wirajasabisnis.databinding.ActivityMainBinding
import com.wirajasa.wirajasabisnis.feature_buyer.ui.epoxy.ListOfServiceController
import com.wirajasa.wirajasabisnis.feature_buyer.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var controller: ListOfServiceController
    private val viewModel by viewModels<MainViewModel>()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbarMain)
        setContentView(binding.root)
        viewModel.getAllService()

        val currentUser = viewModel.getUser()
        if (!currentUser.username.contains(GUEST)) binding.tvGreetUser.text =
            getString(R.string.tv_greeting, currentUser.username)

        controller = ListOfServiceController(onSelected = {
            startActivity(
                Intent(this, DetailServiceActivity::class.java).putExtra(
                    EXTRA_SERVICE_POST, it
                )
            )
        }, onRetry = {
            viewModel.getAllService()
        })

        val arrayAdapter =
            ArrayAdapter(this, R.layout.textview, resources.getStringArray(R.array.province))
        binding.toolbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.menu_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                else -> false
            }
        }
        binding.apply {
            edtSearch.setAdapter(arrayAdapter)
            edtSearch.addTextChangedListener {
                viewModel.getServiceByName(it.toString())
            }
            epoxyService.apply {
                setController(controller)
                layoutManager = LinearLayoutManager(this@MainActivity)
                setItemSpacingDp(8)
                setHasFixedSize(true)
            }
        }
        subscribe()
    }

    private fun subscribe() {
        viewModel.listOfService.observe(this) {
            controller.data = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

}