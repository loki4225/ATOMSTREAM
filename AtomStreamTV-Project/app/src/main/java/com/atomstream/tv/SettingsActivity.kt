package com.atomstream.tv

import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = Ui.root(this)
        root.addView(Ui.text(this, "Settings", 29f, Ui.TEXT, true))
        root.addView(Ui.space(this, 22))
        root.addView(Ui.text(this, "AtomStreamTV  v${BuildConfig.VERSION_NAME}", 19f, Ui.TEXT, true))
        root.addView(Ui.text(this, "A lightweight website launcher for Android TV.", 15f, Ui.MUTED))
        root.addView(Ui.space(this, 24))
        root.addView(Ui.button(this, "Manage / add websites") { startActivity(Intent(this, AddSiteActivity::class.java)) })
        root.addView(Ui.space(this, 12))
        root.addView(Ui.button(this, "Clear WebView cache") {
            AlertDialog.Builder(this).setTitle("Clear cache?").setMessage("Cached website files will be removed. Saved websites and login cookies are kept.")
                .setNegativeButton("Cancel", null).setPositiveButton("Clear") { _, _ ->
                    WebView(this).apply { clearCache(true); destroy() }
                    Toast.makeText(this, "WebView cache cleared", Toast.LENGTH_SHORT).show()
                }.show()
        })
        root.addView(Ui.space(this, 12))
        root.addView(Ui.button(this, "Clear browsing data") {
            AlertDialog.Builder(this).setTitle("Clear browsing data?").setMessage("This removes cookies, website storage, cache and saved website sessions. Your custom website list is not removed.")
                .setNegativeButton("Cancel", null).setPositiveButton("Clear data") { _, _ ->
                    CookieManager.getInstance().removeAllCookies { }
                    CookieManager.getInstance().flush()
                    WebStorage.getInstance().deleteAllData()
                    WebView(this).apply { clearCache(true); clearHistory(); destroy() }
                    Toast.makeText(this, "Browsing data cleared", Toast.LENGTH_SHORT).show()
                }.show()
        })
        root.addView(Ui.space(this, 20))
        root.addView(Ui.text(this, "Troubleshooting", 19f, Ui.TEXT, true))
        root.addView(Ui.text(this, "• Check the projector's internet connection.\n• Update Android System WebView / Chrome if available.\n• Some sites block embedded browsers or require supported DRM, codecs or sign-in.\n• Playback and availability are controlled by each website.", 15f, Ui.MUTED))
        root.addView(Ui.space(this, 20))
        root.addView(Ui.button(this, "Back") { finish() })
        setContentView(root)
    }
}
