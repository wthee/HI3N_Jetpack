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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.wthee.hi3njetpack.databinding.ActivityMainBinding
import com.anbaoyue.manyiwang.utils.CleanUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding :ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        getAuthority()
        drawerLayout = binding.drawerLayout
        navController = Navigation.findNavController(this, R.id.graph_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)
        binding.navigationView.menu.findItem(R.id.cleanCaches).title = "清理缓存    " + CleanUtil.getTotalCacheSize(this)
        var appInfo = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.appInfo)
        var packageInfo = packageManager.getPackageInfo(packageName,0)
        appInfo.text  = resources.getText(R.string.app_name).toString()+"   版本:"+packageInfo.versionName
        binding.navigationView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.cleanCaches ->{
                    CleanUtil.clearAllCache(this)
                    binding.navigationView.menu.findItem(R.id.cleanCaches).title = "清理缓存    " + CleanUtil.getTotalCacheSize(this)
                }
            }
            return@setNavigationItemSelectedListener true
        }
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
}
