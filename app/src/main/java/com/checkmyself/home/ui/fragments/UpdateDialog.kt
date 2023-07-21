package com.checkmyself.home.ui.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.checkmyself.databinding.DialogUpdateBinding
import java.util.Objects

/*
    This is Dialog which is showing information/Api Error or other errors to user in app
 */
class UpdateDialog(var activity: Activity, isForceUpgrade1: Boolean) : DialogFragment() {
    var binding: DialogUpdateBinding? = null
    var msg: String? = null
    var isForceUpgrade = false

    init {
        isForceUpgrade = isForceUpgrade1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogUpdateBinding.inflate(layoutInflater)
        val mView = binding!!.root
        Objects.requireNonNull(dialog)?.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        //        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        isCancelable = !isForceUpgrade;
//        isCancelable = false
        dialog!!.setCanceledOnTouchOutside(false)

//        if(msg!=null && msg.length()>0){
//            binding.tvInfoMsg.setText(msg);
//            binding.btnUpdate.setText(activity.getResources().getString(R.string.ok));
//        }

        if (isForceUpgrade) {
            binding!!.tvSkip.visibility = View.GONE;
        } else {
            binding!!.tvSkip.visibility = View.VISIBLE;
        }
        binding!!.btnUpdate.setOnClickListener {
            dismiss()
            val appPackageName = activity.packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
        binding!!.tvSkip.setOnClickListener { dismiss() }
        return mView
    }
}