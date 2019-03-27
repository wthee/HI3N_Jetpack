package cn.wthee.hi3nlite

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import com.anbaoyue.manyiwang.utils.CleanUtil
import java.util.ArrayList
import cn.wthee.hi3nlite.util.ActivityUtil
import android.os.StrictMode
import android.preference.PreferenceManager
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3nlite.ui.video.VideoFragmentDirections
import cn.wthee.hi3nlite.util.PreviewPicUtil
import cn.wthee.hi3nlite.util.ShareUtil
import com.google.android.material.navigation.NavigationView
import com.nineoldandroids.view.ViewHelper

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationMenu: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navheadView: View

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    companion object {
        var isNightMode: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isNightMode = sharedPreferences.getBoolean("isNightMode",false)
        editor = sharedPreferences.edit()
        setNightMode()

        PreviewPicUtil.deleteFile(PreviewPicUtil.file)
        ActivityUtil.instance.currentActivity = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        getAuthority()

        //file-->Uri分享
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()


    }

    private fun setNightMode() {
        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        editor.putBoolean("isNightMode",isNightMode)
        editor.apply()
    }

    private fun start(){
        drawerLayout = binding.drawerLayout
        navigationMenu = binding.navigationView
        navController = Navigation.findNavController(this, R.id.nav_graph)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        navheadView = navigationMenu.getHeaderView(0)
        //Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        setNav()

    }

    private fun setNav() {
        navigationMenu.setupWithNavController(navController)
        navigationMenu.menu.findItem(R.id.cleanCaches).title = "清理缓存    " + CleanUtil.getTotalCacheSize(this)
        var appInfo = navheadView.findViewById<TextView>(R.id.appInfo)
        var switchDN = navheadView.findViewById<Switch>(R.id.dayNight)
        appInfo.text = resources.getText(R.string.app_name).toString() + "   版本:" + packageManager.getPackageInfo(packageName, 0).versionName
        switchDN.isChecked  = isNightMode
        switchDN.setOnClickListener {
            isNightMode = switchDN.isChecked
            setNightMode()
            recreate()
        }
        navigationMenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.news ->{
                    val direction = VideoFragmentDirections.actionVideoFragmentToNewsFragment()
                    this.findNavController(R.id.nav_graph).navigate(direction)
                    drawerLayout.closeDrawers()
                }
                R.id.miyou ->{
                    val direction = VideoFragmentDirections.actionVideoFragmentToMiYouFragment()
                    this.findNavController(R.id.nav_graph).navigate(direction)
                    drawerLayout.closeDrawers()
                }
                R.id.book ->{
                    val direction = VideoFragmentDirections.actionVideoFragmentToBookFragment()
                    this.findNavController(R.id.nav_graph).navigate(direction)
                    drawerLayout.closeDrawers()
                }
                R.id.cleanCaches -> {
                    CleanUtil.clearAllCache(this)
                    navigationMenu.menu.findItem(R.id.cleanCaches).title =
                        "清理缓存    " + CleanUtil.getTotalCacheSize(this)
                    drawerLayout.closeDrawers()
                }
                R.id.shareME ->{
                    var dialog = AlertDialog.Builder(this)
                    dialog.setTitle("要分享给别的舰长吗？")
                    dialog.setIcon(R.drawable.logo)
                    dialog.setPositiveButton("分享(ง •_•)ง") { _, _ ->
                        ShareUtil.shareText("HI3N:https://www.coolapk.com/game/cn.wthee.hi3nlite\n",this)
                    }
                    dialog.setNegativeButton("算了◑﹏◐") { _, _ -> }
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {
                return
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                var mContent = drawerLayout.getChildAt(0)
                var mMenu = drawerView
                if(drawerView.tag == "LEFT"){
                    ViewHelper.setAlpha(mMenu,0.6f + 0.4f * slideOffset)
                    ViewHelper.setTranslationX(mContent,mMenu.measuredWidth * slideOffset)
                    ViewHelper.setPivotX(mContent,0f)
                    ViewHelper.setPivotY(mContent,mContent.measuredHeight / 2f)
                    mContent.invalidate()
                }
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
        }else{
            start()
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
                    start()
                } else {
                    Toast.makeText(this, "拒绝权限将无法正常使用程序", Toast.LENGTH_SHORT).show()
                }
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
