package com.checkmyself.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.databinding.DialogProgressBinding

object CustomProgressDialog {
    private var progressDialog: Dialog? = null

    fun showProgressDialog(mContext: Context?) {
        dismissProgressDialog()
        progressDialog = null
        System.gc()
        if (mContext != null) {
            progressDialog = Dialog(mContext).apply {
                val progressBinding: DialogProgressBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.dialog_progress, null, false
                )
                setContentView(progressBinding.root)
                window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                setCancelable(false)
            }
            progressDialog!!.show()
        }
    }

    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog?.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

//    fun setProgressDialogText(text: String) {
//        val textView = progressDialog!!.findViewById<TextView>(R.id.textView1)
//        textView.text = text
//    }

}
