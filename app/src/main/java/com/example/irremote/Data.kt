package com.example.irremote

import android.content.Context
import org.json.JSONObject

data class IrButton(val name: String, val address: Int, val command: Int)
data class Remote(val type: String, val brand: String, val buttons: List<IrButton>) {
    val power get() = buttons.firstOrNull { it.name == "power" }
}

val typeLabels = linkedMapOf("tv" to "تلفاز", "receiver" to "رسيفر", "ac" to "مكيف")

fun loadRemotes(ctx: Context): List<Remote> {
    val json = ctx.assets.open("codes.json").bufferedReader().use { it.readText() }
    val arr = JSONObject(json).getJSONArray("remotes")
    return (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        val b = o.getJSONArray("buttons")
        Remote(o.getString("type"), o.getString("brand"), (0 until b.length()).map { j ->
            val x = b.getJSONObject(j); IrButton(x.getString("name"), x.getInt("a"), x.getInt("c"))
        })
    }
}
