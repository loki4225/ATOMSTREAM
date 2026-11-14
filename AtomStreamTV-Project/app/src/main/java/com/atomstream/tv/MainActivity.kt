package com.atomstream.tv

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.HorizontalScrollView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var tiles: LinearLayout
    private var restoreSiteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        renderHome()
    }

    override fun onResume() {
        super.onResume()
        if (::tiles.isInitialized) populateTiles()
    }

    private fun renderHome() {
        val root = Ui.root(this)
        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        header.addView(Ui.text(this, "ATOMSTREAMTV", 28f, Ui.TEXT, true), LinearLayout.LayoutParams(0, Ui.dp(this, 54), 1f))
        header.addView(Ui.button(this, "+  Add Website") { startActivity(Intent(this, AddSiteActivity::class.java)) })
        header.addView(Ui.space(this, 1).apply { layoutParams = LinearLayout.LayoutParams(Ui.dp(this@MainActivity, 14), 1) })
        header.addView(Ui.button(this, "⚙  Settings") { startActivity(Intent(this, SettingsActivity::class.java)) })
        root.addView(header)
        root.addView(Ui.space(this, 26))
        root.addView(Ui.text(this, "Your websites", 25f, Ui.TEXT, true))
        root.addView(Ui.text(this, "Choose a site to open it in the built-in browser", 15f, Ui.MUTED))
        root.addView(Ui.space(this, 24))
        tiles = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.TOP }
        val tileScroller = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isFillViewport = true
            addView(tiles, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT))
        }
        root.addView(tileScroller, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        val footer = Ui.text(this, "▲ ▼ Navigate    •    OK Open    •    BACK Return     |     AtomStreamTV  v${BuildConfig.VERSION_NAME}", 13f, Ui.MUTED)
        root.addView(footer)
        setContentView(root)
        populateTiles()
    }

    private fun populateTiles() {
        if (!::tiles.isInitialized) return
        tiles.removeAllViews()
        val sites = SiteStore.all(this)
        val screenWidthDp = (resources.displayMetrics.widthPixels / resources.displayMetrics.density).toInt()
        val tileWidth = if (sites.size <= 3) ((screenWidthDp - 68 - 18 * (sites.size - 1)) / sites.size).coerceIn(190, 300) else 260
        sites.forEachIndexed { index, site ->
            val tile = TextView(this).apply {
                text = "${site.name}\n\n${if (site.isDefault) "FEATURED WEBSITE" else "CUSTOM WEBSITE"}\n\n${site.url}"
                textSize = 18f
                setTextColor(Ui.TEXT)
                gravity = Gravity.CENTER
                setPadding(Ui.dp(this@MainActivity, 18), Ui.dp(this@MainActivity, 18), Ui.dp(this@MainActivity, 18), Ui.dp(this@MainActivity, 18))
                background = getDrawable(R.drawable.focus_tile)
                isFocusable = true
                isClickable = true
                isLongClickable = !site.isDefault
                setOnClickListener {
                    restoreSiteId = site.id
                    startActivity(Intent(this@MainActivity, PlayerActivity::class.java).putExtra(PlayerActivity.EXTRA_URL, site.url).putExtra(PlayerActivity.EXTRA_TITLE, site.name))
                }
                setOnLongClickListener {
                    if (!site.isDefault) confirmRemove(site)
                    true
                }
                setOnFocusChangeListener { view, focused ->
                    view.scaleX = if (focused) 1.045f else 1f
                    view.scaleY = if (focused) 1.045f else 1f
                    view.elevation = if (focused) Ui.dp(this@MainActivity, 12).toFloat() else 0f
                }
                contentDescription = "${site.name}, ${site.url}${if (site.isDefault) "" else ". Long press to remove"}"
            }
            val params = LinearLayout.LayoutParams(Ui.dp(this, tileWidth), Ui.dp(this, 230)).apply {
                marginEnd = Ui.dp(this@MainActivity, 18)
            }
            tiles.addView(tile, params)
            if (index == 0) tile.post { if (!hasWindowFocus()) return@post; tile.requestFocus() }
        }
        if (tiles.childCount > 0 && tiles.focusedChild == null) tiles.getChildAt(0).requestFocus()
    }

    private fun confirmRemove(site: Site) {
        AlertDialog.Builder(this)
            .setTitle("Remove ${site.name}?")
            .setMessage("This removes the website from your saved list. It does not affect the website itself.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Remove") { _, _ -> SiteStore.remove(this, site.id); populateTiles() }
            .show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && restoreSiteId != null) {
            val id = restoreSiteId
            restoreSiteId = null
            val index = SiteStore.all(this).indexOfFirst { it.id == id }
            if (index >= 0) tiles.post { tiles.getChildAt(index)?.requestFocus() }
        }
    }
}
