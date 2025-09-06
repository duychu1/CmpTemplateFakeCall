package com.common.control.dialog

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.common.control.R
import com.common.control.utils.BroadcastUtils

class PrepareLoadingAdsDialogKt : AppCompatActivity() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_DISMISS_DIALOG -> {
                    finish()
                    return
                }
                ACTION_CLEAR_TEXT_AD -> {
                    clearTextAd()
                }
            }
        }
    }

    companion object {
        const val ACTION_DISMISS_DIALOG = "action_dismiss_dialog"
        const val ACTION_CLEAR_TEXT_AD = "action_clear_text_ad"
        const val ACTION_UPDATE_TEXT = "action_update_text"

        fun start(context: Context) {
            val starter = Intent(context, PrepareLoadingAdsDialogKt::class.java)
            context.startActivity(starter)
        }

        fun remove() {
            // Empty implementation
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_prepair_loading_ads)
        
        val filter = IntentFilter().apply {
            addAction(ACTION_DISMISS_DIALOG)
            addAction(ACTION_CLEAR_TEXT_AD)
        }
        BroadcastUtils.registerReceiver(this, receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun clearTextAd() {
        val tvLoading = findViewById<TextView>(R.id.loading_dialog_tv)
        tvLoading.text = "Loading..."
    }
}
