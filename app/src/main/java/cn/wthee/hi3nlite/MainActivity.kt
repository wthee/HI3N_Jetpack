package cn.wthee.hi3nlite

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import cn.wthee.hi3njetpack.databinding.ActivityMainBinding
import java.util.ArrayList
import cn.wthee.hi3nlite.util.ActivityUtil
import android.os.StrictMode
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.*
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3nlite.util.PreviewPicUtil
import cn.wthee.hi3nlite.util.ShareUtil
import com.anbaoyue.manyiwang.utils.CleanUtil
import com.google.android.material.navigation.NavigationView
import com.nineoldandroids.view.ViewHelper

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var binding: ActivityMainBinding
    private lateinit var navheadView: View
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    companion object {
        lateinit var navController: NavController
        var isNightMode: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtil.instance.currentActivity = this
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isNightMode = sharedPreferences.getBoolean("isNightMode", false)
        editor = sharedPreferences.edit()
        setNightMode()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        navheadView = navigationView.getHeaderView(0)

        //申请权限
        getAuthority()
        setNav()
        //删除分享的图片
        PreviewPicUtil.deleteFile(PreviewPicUtil.file)

        //实现file-->Uri分享
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

    }

    private fun setNightMode() {
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        editor.putBoolean("isNightMode", isNightMode)
        editor.apply()
    }

    private fun setNav() {
        navController = Navigation.findNavController(this, R.id.nav_graph_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navigationView, navController)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.menu.findItem(R.id.cleanCaches).title = "清理缓存    " + CleanUtil.getTotalCacheSize(this)
        var appInfo = navheadView.findViewById<TextView>(R.id.appInfo)
        var switchDN = navheadView.findViewById<Switch>(R.id.dayNight)
        appInfo.text = resources.getText(R.string.app_name).toString() + "   版本:" + packageManager.getPackageInfo(
            packageName,
            0
        ).versionName
        switchDN.isChecked = isNightMode
        switchDN.setOnClickListener {
            isNightMode = switchDN.isChecked
            setNightMode()
            recreate()
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                return
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                var mContent = drawerLayout.getChildAt(0)
                var mMenu = drawerView
                if (drawerView.tag == "LEFT") {
                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * slideOffset)
                    ViewHelper.setTranslationX(mContent, mMenu.measuredWidth * slideOffset)
                    ViewHelper.setPivotX(mContent, 0f)
                    ViewHelper.setPivotY(mContent, mContent.measuredHeight / 2f)
                    mContent.invalidate()
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                return
            }

            override fun onDrawerOpened(drawerView: View) {
                navigationView.menu.findItem(R.id.cleanCaches).title =
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

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId) {
            R.id.backhome -> {
                if (!p0.isChecked) {
                    navController.navigate(R.id.videoFragment)
                    p0.isChecked = true
                }
            }
            R.id.news -> {
                if (!p0.isChecked) {
                    navController.navigate(R.id.newsFragment)
                    p0.isChecked = true
                }
            }
            R.id.miyou -> {
                if (!p0.isChecked) {
                    navController.navigate(R.id.miFragment)
                    p0.isChecked = true
                }
            }
            R.id.book -> {
                if (!p0.isChecked) {
                    navController.navigate(R.id.bookFragment)
                    p0.isChecked = true
                }
            }
            R.id.cleanCaches -> {
                CleanUtil.clearAllCache(this)
                navigationView.menu.findItem(R.id.cleanCaches).title =
                    "清理缓存    " + CleanUtil.getTotalCacheSize(this)
                drawerLayout.closeDrawers()
                p0.isChecked = false
            }
            R.id.shareME -> {
                var dialog = AlertDialog.Builder(this)
                dialog.setTitle("要分享给别的舰长吗？")
                dialog.setIcon(R.drawable.logo)
                dialog.setPositiveButton("分享(ง •_•)ง") { _, _ ->
                    ShareUtil.shareText("HI3N:https://www.coolapk.com/game/cn.wthee.hi3nlite\n", this)
                }
                dialog.setNegativeButton("算了◑﹏◐") { _, _ -> }
                dialog.show()
                p0.isChecked = false
            }
        }

        drawerLayout.closeDrawers()
        return true
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
                if (hasPermissionDismiss) {
                    Toast.makeText(this, "拒绝权限将无法正常使用程序", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(Navigation.findNavController(this@MainActivity, R.id.nav_graph_fragment), drawerLayout)
    }

    override fun onBackPressed() {
        if (binding.toolbar.title.toString() == getString(R.string.app_name)) {
            onDestroy()
        }
        var title = arrayListOf(
            getString(R.string.menu_news),
            getString(R.string.menu_miyou),
            getString(R.string.menu_book)
        )
        if (title.contains(binding.toolbar.title.toString())) {
            navController.navigate(R.id.videoFragment)
        }else{
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        try {
            super.onConfigurationChanged(newConfig)
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
