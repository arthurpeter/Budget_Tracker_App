package com.android.budgettrackerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

fun Context.changeActivity(targetActivity: Class<out AppCompatActivity>, vararg extras: Pair<String, Any?>) {
    val intent = Intent(this, targetActivity)

    for (extra in extras) {
        val key = extra.first
        when (val value = extra.second) {
            is Int -> intent.putExtra(key, value)
            is String -> intent.putExtra(key, value)
            is Boolean -> intent.putExtra(key, value)
            is Float -> intent.putExtra(key, value)
            is Long -> intent.putExtra(key, value)
            is Double -> intent.putExtra(key, value)
            is Bundle -> intent.putExtra(key, value)
            is Transaction -> intent.putExtra(key, value)
            else -> throw IllegalArgumentException("Unsupported extra type for key \"$key\"")
        }
    }

    startActivity(intent)
}