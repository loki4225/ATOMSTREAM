package com.atomstream.tv

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_URL = "site_url"
        const val EXTRA_TITLE = "site_title"
    }

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var container: FrameLayout
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalSystemUi = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        originalSystemUi = window.decorView.systemUiVisibility
        container = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        webView = WebView(this)
        progress = ProgressBar(this).apply { isIndeterminate = true }
        errorText = TextView(this).apply {
            setTextColor(Color.WHITE); textSize = 18f; gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32); visibility = View.GONE; isFocusable = true
            setOnClickListener { visibility = View.GONE; webView.reload() }
        }
        container.addView(webView, FrameLayout.LayoutParams(-1, -1))
        container.addView(progress, FrameLayout.LayoutParams(Ui.dp(this, 48), Ui.dp(this, 48), Gravity.CENTER))
        container.addView(errorText, FrameLayout.LayoutParams(-1, -1))
        setContentView(container)

        val url = intent.getStringExtra(EXTRA_URL).orEmpty()
        if (!SiteStore.isValidHttpUrl(url)) {
            showError("Invalid website address.")
            return
        }
        WebViewManager.configure(this, webView,
            onLoading = { loading -> progress.visibility = if (loading) View.VISIBLE else View.GONE },
            onError = { message -> showError(message) },
            chromeClient = createChromeClient()
        )
        webView.loadUrl(url)
    }

    private fun createChromeClient() = object : WebChromeClient() {
        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            if (customView != null) { callback.onCustomViewHidden(); return }
            customView = view
            customViewCallback = callback
            webView.visibility = View.GONE
            progress.visibility = View.GONE
            errorText.visibility = View.GONE
            container.addView(view, FrameLayout.LayoutParams(-1, -1))
            enterImmersiveMode()
        }

        override fun onHideCustomView() { exitCustomView() }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            progress.visibility = if (newProgress in 0..99) View.VISIBLE else View.GONE
        }

        override fun onReceivedTitle(view: WebView?, title: String?) { super.onReceivedTitle(view, title) }
    }

    private fun enterImmersiveMode() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    private fun exitCustomView() {
        val view = customView ?: return
        container.removeView(view)
        customView = null
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
        webView.visibility = View.VISIBLE
        window.decorView.systemUiVisibility = originalSystemUi
    }

    private fun showError(message: String) {
        errorText.text = "$message\n\nSelect OK to reload."
        errorText.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        when {
            customView != null -> exitCustomView()
            ::webView.isInitialized && webView.canGoBack() -> webView.goBack()
            else -> super.onBackPressed()
        }
    }

    override fun onPause() {
        if (::webView.isInitialized) webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::webView.isInitialized) webView.onResume()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.stopLoading()
            webView.webChromeClient = null
            webView.destroy()
        }
        super.onDestroy()
    }
}
