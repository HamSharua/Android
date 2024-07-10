package com.example.challengeme

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.challengeme.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView1: BottomNavigationView
    private lateinit var bottomNavigationView2: BottomNavigationView
    private lateinit var bottomNavigationView3: BottomNavigationView
    private lateinit var bottomNavigationViewList: List<BottomNavigationView>


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
        // 各 BottomNavigationView をリストで管理する
        bottomNavigationViewList = listOf(
            findViewById(R.id.top_left_nav_view),
            findViewById(R.id.top_right_nav_view),
            findViewById(R.id.nav_view)
        )
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController2 = navHostFragment.navController

        // 各 BottomNavigationView を対応する NavController2 と紐づける
        bottomNavigationViewList.forEach { bottomNavigationView ->
            bottomNavigationView.setupWithNavController(navController2)
        }

//        ChallengeCamera画面ではナビを非表示にする
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.challengeCameraFragment -> {
                    bottomNavigationViewList.forEach { bottomNavigationView ->
                        bottomNavigationView.visibility = View.GONE
                    }
                }
                else -> {
                    bottomNavigationViewList.forEach { bottomNavigationView ->
                        bottomNavigationView.visibility = View.VISIBLE
                    }
                }
            }
        }

    }
}