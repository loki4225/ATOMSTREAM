package com.atomstream.tv

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object SiteStore {
    private const val PREFS = "atomstream_sites"
    private const val KEY_CUSTOM = "custom_sites"

    fun defaults(): List<Site> = listOf(
        Site("default_gotaku", "Gotaku", "https://gotaku.to/", true),
        Site("default_cinejoy", "Cinejoy", "https://cinejoy.pk/", true),
        Site("default_movy", "Movy", "https://www.movy.sx/", true)
    )

    fun all(context: Context): List<Site> = defaults() + custom(context)

    fun custom(context: Context): List<Site> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_CUSTOM, "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val item = array.optJSONObject(i) ?: continue
                    val name = item.optString("name").trim()
                    val url = item.optString("url").trim()
                    if (name.isNotEmpty() && isValidHttpUrl(url)) {
                        add(Site(item.optString("id", UUID.randomUUID().toString()), name, url, false))
                    }
                }
            }
        } catch (_: Exception) { emptyList() }
    }

    fun add(context: Context, name: String, inputUrl: String): String? {
        val cleanName = name.trim()
        val url = normalizeUrl(inputUrl) ?: return "Enter a valid HTTP or HTTPS website address."
        if (cleanName.isBlank()) return "Enter a website name."
        if (all(context).any { it.url.trimEnd('/').equals(url.trimEnd('/'), ignoreCase = true) }) {
            return "This website is already in your list."
        }
        val sites = custom(context).toMutableList()
        sites.add(Site(UUID.randomUUID().toString(), cleanName, url, false))
        save(context, sites)
        return null
    }

    fun remove(context: Context, id: String) {
        save(context, custom(context).filterNot { it.id == id })
    }

    private fun save(context: Context, sites: List<Site>) {
        val array = JSONArray()
        sites.forEach { site ->
            array.put(JSONObject().put("id", site.id).put("name", site.name).put("url", site.url))
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_CUSTOM, array.toString()).apply()
    }

    fun normalizeUrl(input: String): String? {
        val candidate = input.trim().let { if (it.contains("://")) it else "https://$it" }
        val uri = runCatching { android.net.Uri.parse(candidate) }.getOrNull() ?: return null
        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") return null
        if (uri.host.isNullOrBlank() || uri.host!!.contains(' ')) return null
        return uri.buildUpon().fragment(null).build().toString()
    }

    fun isValidHttpUrl(url: String): Boolean = normalizeUrl(url) != null
}
