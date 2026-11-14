package com.atomstream.tv

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.SafeBrowsingResponse
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.SslErrorHandler
import android.net.http.SslError

object WebViewManager {
    @SuppressLint("SetJavaScriptEnabled")
    fun configure(context: Context, webView: WebView, onLoading: (Boolean) -> Unit, onError: (String) -> Unit, chromeClient: WebChromeClient) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            loadsImagesAutomatically = true
            mediaPlaybackRequiresUserGesture = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = userAgentString
        }
        CookieManagerCompat.enableThirdPartyCookies(webView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WebView.setSafeBrowsingEnabled(true)
        webView.webChromeClient = chromeClient
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return !isHttp(request.url)
            }
            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = !isHttp(Uri.parse(url))
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) { onLoading(true) }
            override fun onPageFinished(view: WebView, url: String?) { onLoading(false) }
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) {
                    onLoading(false)
                    onError("Unable to load this page. Check your internet connection or try reloading.")
                }
            }
            @Suppress("DEPRECATION")
            override fun onReceivedError(view: WebView, errorCode: Int, description: String?, failingUrl: String?) {
                onLoading(false)
                onError("Unable to load this page. Check your internet connection or try reloading.")
            }
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) { handler.cancel() }
            override fun onSafeBrowsingHit(view: WebView, request: WebResourceRequest, threatType: Int, callback: SafeBrowsingResponse) {
                callback.backToSafety(true)
            }
        }
    }

    private fun isHttp(uri: Uri): Boolean = uri.scheme.equals("http", true) || uri.scheme.equals("https", true)
}

private object CookieManagerCompat {
    fun enableThirdPartyCookies(webView: WebView) {
        android.webkit.CookieManager.getInstance().setAcceptCookie(true)
        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
    }
}
