package io.github.droidkaigi.confsched2020

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.auto.service.AutoService
import com.willowtreeapps.hyperion.plugin.v1.Plugin
import com.willowtreeapps.hyperion.plugin.v1.PluginModule

@AutoService(Plugin::class)
class DeepLinkPlugin : Plugin() {
    override fun createPluginModule() = object : PluginModule() {
        override fun createPluginView(layoutInflater: LayoutInflater, parent: ViewGroup): View? =
            layoutInflater.inflate(R.layout.plugin_deeplink, parent, false).apply {
                setOnClickListener {
                    (extension.activity as? AppCompatActivity)?.supportFragmentManager?.also {
                        DeepLinkDialog().show(it, DeepLinkDialog.TAG)
                    }
                }
            }
    }
}

class DeepLinkDialog : DialogFragment() {

    companion object {
        val TAG = DeepLinkDialog::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_deeplink, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_deeplink, null).apply {
                findViewById<View>(R.id.deeplink_fix_button)?.setOnClickListener {
                    dialog?.findViewById<TextView>(R.id.deeplink_fix_input)
                        ?.also { dom -> launchIntent(dom.text.toString()) }
                }
                findViewById<View>(R.id.deeplink_free_button)?.setOnClickListener {
                    dialog?.findViewById<EditText>(R.id.deeplink_free_input)
                        ?.also { dom -> launchIntent(dom.text.toString()) }
                }
            }
            AlertDialog.Builder(it).setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onPause() {
        dismiss()
        super.onPause()
    }

    private fun launchIntent(uri: String) {
        runCatching {
            Intent(Intent.ACTION_VIEW, Uri.parse(uri)).also { activity?.startActivity(it) }
        }
    }
}
