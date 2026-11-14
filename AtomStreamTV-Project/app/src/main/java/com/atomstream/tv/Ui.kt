package com.atomstream.tv

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

object Ui {
    const val BG = 0xFF090D16.toInt()
    const val PANEL = 0xFF121D2D.toInt()
    const val TEXT = 0xFFF4F7FB.toInt()
    const val MUTED = 0xFFAAB8C9.toInt()
    const val ACCENT = 0xFF67D5FF.toInt()

    fun root(context: Context): LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(context, 34), dp(context, 24), dp(context, 34), dp(context, 20))
        setBackgroundColor(BG)
        isFocusable = false
    }

    fun text(context: Context, value: String, size: Float, color: Int = TEXT, bold: Boolean = false): TextView =
        TextView(context).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
        }

    fun button(context: Context, label: String, onClick: () -> Unit): TextView = text(context, label, 18f, TEXT, true).apply {
        gravity = Gravity.CENTER
        setPadding(dp(context, 20), dp(context, 12), dp(context, 20), dp(context, 12))
        background = GradientDrawable().apply { setColor(PANEL); cornerRadius = dp(context, 12).toFloat(); setStroke(dp(context, 1), 0xFF344B67.toInt()) }
        isFocusable = true
        isClickable = true
        setOnClickListener { onClick() }
        setOnFocusChangeListener { view, focused ->
            view.scaleX = if (focused) 1.04f else 1f
            view.scaleY = if (focused) 1.04f else 1f
            (view.background as? GradientDrawable)?.setStroke(dp(context, if (focused) 3 else 1), if (focused) ACCENT else 0xFF344B67.toInt())
        }
    }

    fun dp(context: Context, value: Int): Int = (value * context.resources.displayMetrics.density + 0.5f).toInt()
    fun space(context: Context, height: Int): View = View(context).apply { layoutParams = LinearLayout.LayoutParams(1, dp(context, height)) }
}
