package com.reward.dne.activity

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.TextView
import com.reward.dne.R
import java.net.URL
import java.net.URLDecoder

abstract class BaseActivity : AppCompatActivity() {

    protected var mHandler = Handler()
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        dialog =  Dialog(this@BaseActivity)
    }

    fun showProgress() {
        showProgress(getString(R.string.loading))
    }

    fun showProgress(messageId: Int) {
        showProgress(getString(messageId))
    }

    fun showProgress(message:CharSequence){
        if(applicationContext != null){
            mHandler.post {
                try {
                    dialog =  Dialog(this@BaseActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.setContentView(R.layout.custom_progress_view)
                    dialog.setCancelable(false)
                    dialog.show()
                    val progress : TextView = dialog.findViewById(R.id.text_progressupdate)
                    progress.text = message
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }

        }

    }

    fun hideProgress() {
        mHandler.post {
            try {
                dialog.dismiss()
            }
            catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    fun Context.copyToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.primaryClip = clip
    }

    fun openWebPage(url: Uri) {
        val dd = URL(URLDecoder.decode(url.toString(),
                "UTF-8"))
        //copyToClipboard(dd.toString())
        val webpage = Uri.parse(dd.toString())
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    fun appInstalledOrNot(uri: String): Boolean {
        val pm = this.packageManager
        var app_installed = false
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }

        return app_installed
    }
}