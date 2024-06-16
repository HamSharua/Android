package com.example.challengeme

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.challengeme.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView1: BottomNavigationView
    private lateinit var bottomNavigationView2: BottomNavigationView
    private lateinit var bottomNavigationView3: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navView: BottomNavigationView = binding.navView
        val topLeftNavView: BottomNavigationView = binding.topLeftNavView
        val topRightNavView: BottomNavigationView = binding.topRightNavView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_challenge, R.id.navigation_calender, R.id.navigation_timeline
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val appBarConfiguration1 = AppBarConfiguration(
            setOf(R.id.navigation_profile)
        )
        topLeftNavView.setupWithNavController(navController)

        val appBarConfiguration2 = AppBarConfiguration(
            setOf(R.id.navigation_notifications)
        )
        topRightNavView.setupWithNavController(navController)

//        ナビを消す
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.top_left_nav_view)
        bottomNavigationView1 = findViewById(R.id.top_left_nav_view)
        bottomNavigationView2 = findViewById(R.id.top_right_nav_view)
        bottomNavigationView3 = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController2 = navHostFragment.navController

        // 各 BottomNavigationView を対応する NavController と紐づける
        bottomNavigationView1.setupWithNavController(navController2)
        bottomNavigationView2.setupWithNavController(navController2)
        bottomNavigationView3.setupWithNavController(navController2)

//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
//        val navController2 = navHostFragment.navController
//
//        bottomNavigationView.setupWithNavController(navController2)
//
//        navController2.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.challengeCameraFragment -> bottomNavigationView.visibility = View.GONE
//                else -> bottomNavigationView.visibility = View.VISIBLE
//            }
//        }
        navController2.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.challengeCameraFragment -> bottomNavigationView1.visibility = View.GONE
                else -> bottomNavigationView1.visibility = View.VISIBLE
            }
        }
    }
}
