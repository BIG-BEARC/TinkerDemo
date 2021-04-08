package com.example.retrofitandokhttp.tinker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tinkerdemo.BuildConfig
import com.example.tinkerdemo.R
import com.tencent.tinker.lib.tinker.Tinker
import com.tencent.tinker.lib.tinker.TinkerInstaller
import com.tencent.tinker.loader.shareutil.ShareConstants
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals


class TinkerHotFixActivity : AppCompatActivity() {
    private var path: String = Environment.getExternalStorageDirectory().absolutePath + "/patch_signed_7zip.apk"
//    private val rxPermissions: RxPermissions by lazy {
//        RxPermissions(this)
//    }

    var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tinker_hot_fix)
        textView = findViewById(R.id.text)
        textView?.text = "123"

        this.filesDir
//        rxPermissions.setLogging(true)
//        rxPermissions.request(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ).subscribe { grade ->
//            if (grade) {
//                Toast.makeText(this, "同意", Toast.LENGTH_SHORT).show()
//
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    0
//                )
//                Toast.makeText(this, "拒绝", Toast.LENGTH_SHORT).show()
//
//            }
//        }
        askForRequiredPermissions()
    }
    private fun askForRequiredPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return
        }
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 16) {
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        } else {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            val res = ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            res == PackageManager.PERMISSION_GRANTED
        }
    }


    /**
     * 加载热补丁插件
     */
    fun loadPatch(v: View) {

        TinkerInstaller.onReceiveUpgradePatch(applicationContext, path)

    }

    /**
     * 查看补丁信息
     */
    fun showInfo(v: View) {
        // add more Build Info
        val sb = StringBuilder()
        val tinker = Tinker.with(applicationContext)
        if (tinker.isTinkerLoaded) {
            sb.append(String.format("[补丁已加载] \n Fix Bug!"))
            sb.append(java.lang.String.format("[基准包版本号] %s \n", BuildConfig.TINKER_ID))
            sb.append(
                String.format(
                    "[补丁号] %s \n",
                    tinker.tinkerLoadResultIfPresent
                        .getPackageConfigByName(ShareConstants.TINKER_ID)
                )
            )
            sb.append(
                String.format(
                    "[补丁版本] %s \n",
                    tinker.tinkerLoadResultIfPresent.getPackageConfigByName("patchVersion")
                )
            )
            sb.append(String.format("[补丁占用空间] %s k \n", tinker.tinkerRomSpace))
        } else {
            sb.append(String.format("[补丁未加载] \n"))
            sb.append(java.lang.String.format("[基准包版本号] %s \n", BuildConfig.TINKER_ID))
            sb.append(
                String.format(
                    "[TINKER_ID] %s \n",
                    ShareTinkerInternals.getManifestTinkerID(applicationContext)
                )
            )
        }
        textView?.text = sb
    }

    /**
     * 清除补包
     */
    fun cleanPatch(v: View) {
        Tinker.with(applicationContext).cleanPatch()
    }
}