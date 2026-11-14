package com.atomstream.tv

import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddSiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = Ui.root(this)
        root.addView(Ui.text(this, "Add a website", 29f, Ui.TEXT, true))
        root.addView(Ui.space(this, 18))
        root.addView(Ui.text(this, "Website name", 16f, Ui.MUTED))
        val name = field("e.g. My streaming site")
        root.addView(name, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Ui.dp(this, 58)))
        root.addView(Ui.space(this, 14))
        root.addView(Ui.text(this, "Website URL", 16f, Ui.MUTED))
        val url = field("example.com")
        url.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_URI
        root.addView(url, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Ui.dp(this, 58)))
        root.addView(Ui.space(this, 24))
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        actions.addView(Ui.button(this, "Save website") {
            val error = SiteStore.add(this, name.text.toString(), url.text.toString())
            if (error == null) { Toast.makeText(this, "Website saved", Toast.LENGTH_SHORT).show(); finish() }
            else Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        })
        actions.addView(Ui.space(this, 1).apply { layoutParams = LinearLayout.LayoutParams(Ui.dp(this@AddSiteActivity, 16), 1) })
        actions.addView(Ui.button(this, "Cancel") { finish() })
        root.addView(actions)
        root.addView(Ui.space(this, 18))
        root.addView(Ui.text(this, "Only HTTP and HTTPS addresses are accepted. If you omit the scheme, https:// is added automatically.", 14f, Ui.MUTED))
        setContentView(root)
        name.requestFocus()
    }

    private fun field(hintText: String) = EditText(this).apply {
        hint = hintText
        textSize = 18f
        setSingleLine(true)
        setTextColor(Ui.TEXT)
        setHintTextColor(Ui.MUTED)
        setPadding(Ui.dp(this@AddSiteActivity, 16), 0, Ui.dp(this@AddSiteActivity, 16), 0)
        setBackgroundResource(R.drawable.focus_tile)
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
