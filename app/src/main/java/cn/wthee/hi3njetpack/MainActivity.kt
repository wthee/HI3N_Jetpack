package cn.wthee.hi3njetpack

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.wthee.hi3njetpack.databinding.ActivityMainBinding
import cn.wthee.hi3njetpack.view.NewsFragmentDirections
import cn.wthee.hi3njetpack.view.VideoFragmentDirections
import com.anbaoyue.manyiwang.utils.CleanUtil
import java.util.ArrayList
import android.app.Activity
import cn.wthee.hi3njetpack.util.ActivityUtil
import java.lang.ref.WeakReference
import android.os.StrictMode
import android.view.View
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationMenu: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // android 7.0系统解决拍照的问题
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        ActivityUtil.instance.currentActivity = this
        getAuthority()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        navigationMenu = binding.navigationView
        navController = Navigation.findNavController(this, R.id.nav_graph)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        //Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)


        // Set up navigation menu
        navigationMenu.setupWithNavController(navController)
        navigationMenu.menu.findItem(R.id.cleanCaches).title = "清理缓存    " + CleanUtil.getTotalCacheSize(this)
        var appInfo = navigationMenu.getHeaderView(0).findViewById<TextView>(R.id.appInfo)
        var packageInfo = packageManager.getPackageInfo(packageName, 0)
        appInfo.text = resources.getText(R.string.app_name).toString() + "   版本:" + packageInfo.versionName
        navigationMenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.cleanCaches -> {
                    CleanUtil.clearAllCache(this)
                    navigationMenu.menu.findItem(R.id.cleanCaches).title =
                        "清理缓存    " + CleanUtil.getTotalCacheSize(this)
                    drawerLayout.closeDrawers()
                }
                R.id.change ->{
                    val direction = VideoFragmentDirections.actionVideoFragmentToNewsFragment()
                    this.findNavController(R.id.nav_graph).navigate(direction)
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {
                return
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                return
            }

            override fun onDrawerClosed(drawerView: View) {
                return
            }

            override fun onDrawerOpened(drawerView: View) {
                navigationMenu.menu.findItem(R.id.cleanCaches).title =
                    "清理缓存    " + CleanUtil.getTotalCacheSize(MyApplication.context)
            }

        })

    }

    private fun getAuthority() {
        //申请权限
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val mPermissions = ArrayList<String>()
        for (string in permissions) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, string) !== PackageManager.PERMISSION_GRANTED) {
                mPermissions.add(string)
            }
        }
        if (mPermissions.size > 0) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                //有权限没有通过
                var hasPermissionDismiss = false
                for (i in grantResults.indices) {
                    if (grantResults[i] == -1) {
                        hasPermissionDismiss = true
                        break
                    }
                }
                if (!hasPermissionDismiss) {

                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
