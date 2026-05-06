package com.example.studentcontactapp.ui.home

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studentcontactapp.R
import com.example.studentcontactapp.databinding.ActivityMainBinding
import com.example.studentcontactapp.ui.detail.DetailFragment
import com.example.studentcontactapp.ui.directory.DirectoryFragment
import com.example.studentcontactapp.ui.profile.ProfileFragment
import com.example.studentcontactapp.utils.SettingsManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager

    // ✅ PENTING: terapkan skala font sebelum activity dibuat
    override fun attachBaseContext(newBase: Context) {
        settingsManager = SettingsManager(newBase)
        val config = Configuration(newBase.resources.configuration)
        config.fontScale = settingsManager.fontScale
        applyOverrideConfiguration(config)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Listener backstack (saat DetailFragment dibuka/ditutup)
        supportFragmentManager.addOnBackStackChangedListener {
            updateToolbarForCurrentFragment()
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // Bersihkan backstack agar tidak menumpuk saat pindah tab
            supportFragmentManager.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_directory -> loadFragment(DirectoryFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            supportFragmentManager.executePendingTransactions()
            updateToolbarForCurrentFragment()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        // Pastikan toolbar sesuai setelah kembali dari form atau recreate
        supportFragmentManager.executePendingTransactions()
        updateToolbarForCurrentFragment()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        supportFragmentManager.executePendingTransactions()
        updateToolbarForCurrentFragment()
    }

    private fun updateToolbarForCurrentFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {
            is HomeFragment -> {
                supportActionBar?.title = "Home"
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            is DirectoryFragment -> {
                supportActionBar?.title = "Directory"
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            is DetailFragment -> {
                supportActionBar?.title = "Detail Mahasiswa"
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            is ProfileFragment -> {
                supportActionBar?.title = "Profile"
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            else -> {
                supportActionBar?.title = getString(R.string.app_name)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}